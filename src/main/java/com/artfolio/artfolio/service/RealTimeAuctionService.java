package com.artfolio.artfolio.service;

import com.artfolio.artfolio.domain.ArtPiecePhoto;
import com.artfolio.artfolio.domain.Member;
import com.artfolio.artfolio.dto.AuctionBidInfoRes;
import com.artfolio.artfolio.dto.MemberInfo;
import com.artfolio.artfolio.dto.RealTimeAuctionInfo;
import com.artfolio.artfolio.dto.RealTimeAuctionPreviewRes;
import com.artfolio.artfolio.exception.*;
import com.artfolio.artfolio.repository.ArtPiecePhotoRepository;
import com.artfolio.artfolio.repository.ArtPieceRepository;
import com.artfolio.artfolio.repository.MemberRepository;
import com.artfolio.artfolio.repository.RealTimeAuctionRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class RealTimeAuctionService {
    private final AuctionService auctionService;
    private final ArtPieceRepository artPieceRepository;
    private final ArtPiecePhotoRepository artPiecePhotoRepository;
    private final RealTimeAuctionRedisRepository realTimeAuctionRedisRepository;
    private final MemberRepository memberRepository;

    public String createAuction(RealTimeAuctionInfo auctionInfo) {
        Long artPieceId = auctionInfo.getArtPieceId();

        Optional<RealTimeAuctionInfo> byArtPieceId = realTimeAuctionRedisRepository.findByArtPieceId(artPieceId);

        // 이미 경매가 진행중인 예술품인 경우 예외처리
        if (byArtPieceId.isPresent()) {
            throw new AuctionAlreadyExistsException(artPieceId);
        }

        List<String> artPiecePhotos = artPiecePhotoRepository
                .getArtPiecePhotoByArtPiece_Id(artPieceId)
                .stream()
                .map(ArtPiecePhoto::getFilePath)
                .collect(Collectors.toList());

        Long artistId = auctionInfo.getArtistId();

        Member artist = memberRepository.findById(artistId)
                        .orElseThrow(() -> new MemberNotFoundException(artistId));

        auctionInfo.setLike(0L);
        auctionInfo.setPhotoPaths(artPiecePhotos);
        auctionInfo.setAuctionCurrentPrice(auctionInfo.getAuctionStartPrice());
        auctionInfo.setCreatedAt(LocalDateTime.now());
        auctionInfo.setArtistInfo(MemberInfo.of(artist));

        RealTimeAuctionInfo save = realTimeAuctionRedisRepository.save(auctionInfo);

        return save.getId();
    }

    public RealTimeAuctionInfo getAuction(String auctionKey) {
        RealTimeAuctionInfo auctionInfo = realTimeAuctionRedisRepository.findById(auctionKey)
                .orElseThrow(() -> new AuctionNotFoundException(auctionKey));

        List<String> photoPaths = auctionInfo.getPhotoPaths();

        for (String path : photoPaths) {
            if (path.contains("_compressed")) {
                photoPaths.remove(path);
                break;
            }
        }

        auctionInfo.setPhotoPaths(photoPaths);

        return auctionInfo;
    }

    public RealTimeAuctionPreviewRes getAuctionList(Pageable pageable) {
        Slice<RealTimeAuctionInfo> infos = realTimeAuctionRedisRepository.findAll(pageable);
        List<RealTimeAuctionPreviewRes.PreviewInfo> data = new ArrayList<>();

        /* 각 실시간 경매에 대한 썸네일 경로를 추출한다 */
        for (RealTimeAuctionInfo info : infos) {
            Long artPieceId = info.getArtPieceId();
            List<ArtPiecePhoto> thumbnailPhoto = artPiecePhotoRepository.getArtPiecePhotoByArtPiece_Id(artPieceId)
                    .stream()
                    .filter(ArtPiecePhoto::getIsThumbnail)
                    .toList();

            String thumbnailPath = thumbnailPhoto.isEmpty() ? "null" : thumbnailPhoto.get(0).getFilePath();

            if (!thumbnailPath.equals("null")) {
                int lastDotIdx = thumbnailPath.lastIndexOf(".");

                String thumbnailFullPath = thumbnailPath.substring(0, lastDotIdx);
                String thumbnailExt = thumbnailPath.substring(lastDotIdx);
                thumbnailPath = thumbnailFullPath + "_compressed" + thumbnailExt;
            }

            Member artist = artPieceRepository.findArtPieceById(artPieceId)
                    .orElseThrow(() -> new ArtPieceNotFoundException(artPieceId))
                    .getArtist();

            MemberInfo artistInfo = MemberInfo.of(artist);
            data.add(RealTimeAuctionPreviewRes.PreviewInfo.of(artistInfo, info, thumbnailPath));
        }

        return RealTimeAuctionPreviewRes.of(infos, data);
    }

    public AuctionBidInfoRes updatePrice(Principal principal, String auctionKey, Long bidderId, Long price) {
        /* 실시간 경매 정보를 가져온다 */
        RealTimeAuctionInfo auctionInfo = realTimeAuctionRedisRepository.findById(auctionKey)
                .orElseThrow(() -> new AuctionNotFoundException(auctionKey));

        Long currentPrice = auctionInfo.getAuctionCurrentPrice();
        log.info("current price : " + currentPrice);
        log.info("bid price : " + price);

        /* 현재가보다 낮은 경우 예외 처리 */
        if (currentPrice >= price) {
            throw new InvalidBidPriceException(principal, currentPrice, price, auctionKey);
        }

        /* 실시간 경매 정보를 업데이트 하고 */
        auctionInfo.setBidderId(bidderId);
        auctionInfo.setAuctionCurrentPrice(price);
        realTimeAuctionRedisRepository.save(auctionInfo);

        /* 입찰자 정보를 DB에서 찾아온 뒤 DTO 생성 후 반환 */
        Member bidder = memberRepository.findById(bidderId)
                .orElseThrow(() -> new MemberNotFoundException(bidderId));

        MemberInfo bidderInfo = MemberInfo.of(bidder);

        return AuctionBidInfoRes.builder()
                .bidPrice(price)
                .bidderInfo(bidderInfo)
                .bidDate(LocalDateTime.now())
                .build();
    }

    public void updateImage(Long artPieceId, String s3Path) {
        RealTimeAuctionInfo auctionInfo = realTimeAuctionRedisRepository.findByArtPieceId(artPieceId)
                .orElseThrow(() -> new AuctionNotFoundException(artPieceId));

        auctionInfo.getPhotoPaths().add(s3Path);
        realTimeAuctionRedisRepository.save(auctionInfo);
    }

    public Long updateLike(String auctionKey, Long memberId) {
        RealTimeAuctionInfo auctionInfo = realTimeAuctionRedisRepository.findById(auctionKey)
                .orElseThrow(() -> new AuctionNotFoundException(auctionKey));

        /* 이미 좋아요가 눌린 상태에서 다시 누르면 취소, 아니면 + 1 */
        Set<Long> likeMembers = auctionInfo.getLikeMembers();
        Long likes = auctionInfo.getLike();

        if (likeMembers.contains(memberId)) {
            likes--;
            likeMembers.remove(memberId);
        } else {
            likes++;
            likeMembers.add(memberId);
        }

        auctionInfo.setLike(likes);
        realTimeAuctionRedisRepository.save(auctionInfo);

        return likes;
    }

    public Long deleteAuction(String auctionKey, Long artistId) {
        RealTimeAuctionInfo info = realTimeAuctionRedisRepository.findById(auctionKey)
                .orElseThrow(() -> new AuctionNotFoundException(auctionKey));

        if (Objects.equals(info.getArtistInfo().getMemberId(), artistId)) {
            realTimeAuctionRedisRepository.deleteById(auctionKey);
            return 1L;
        }

        return 0L;
    }

    public Long finishAuction(String auctionKey, Boolean isSold) {
        RealTimeAuctionInfo auctionInfo = realTimeAuctionRedisRepository.findById(auctionKey)
                .orElseThrow(() -> new AuctionNotFoundException(auctionKey));

        realTimeAuctionRedisRepository.deleteById(auctionKey);
        return auctionService.saveAuctionInfo(auctionInfo, isSold);
    }

    public Long finishAuctionWithBidder(String auctionKey, Long bidderId, Long finalPrice) {
        RealTimeAuctionInfo auctionInfo = realTimeAuctionRedisRepository.findById(auctionKey)
                .orElseThrow(() -> new AuctionNotFoundException(auctionKey));

        /* 현재가보다 낮은 최종가가 들어온 경우 예외 처리 */
        if (auctionInfo.getAuctionCurrentPrice() > finalPrice) return 0L;

        /* 레디스에서 삭제 후 DB에 경매 기록 저장 */
        auctionInfo.setBidderId(bidderId);
        auctionInfo.setAuctionCurrentPrice(finalPrice);
        realTimeAuctionRedisRepository.deleteById(auctionKey);

        return auctionService.saveAuctionInfo(auctionInfo, true);
    }
}
