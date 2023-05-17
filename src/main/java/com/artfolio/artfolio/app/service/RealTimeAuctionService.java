package com.artfolio.artfolio.app.service;

import com.artfolio.artfolio.app.domain.ArtPiecePhoto;
import com.artfolio.artfolio.app.domain.Member;
import com.artfolio.artfolio.app.domain.redis.AuctionBidInfo;
import com.artfolio.artfolio.app.domain.redis.RealTimeAuctionInfo;
import com.artfolio.artfolio.app.dto.*;
import com.artfolio.artfolio.app.repository.*;
import com.artfolio.artfolio.global.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class RealTimeAuctionService {
    private final MemberRepository memberRepository;
    private final AuctionService auctionService;
    private final ArtPieceRepository artPieceRepository;
    private final ArtPiecePhotoRepository artPiecePhotoRepository;
    private final RealTimeAuctionRedisRepository realTimeAuctionRedisRepository;
    private final BidRedisRepository bidderRedisRepository;

    @Transactional(readOnly = true)
    public CreateAuction.Res createAuction(CreateAuction.Req req) {
        // 이미 경매가 진행중인 예술품인 경우 예외처리
        Long artPieceId = req.getArtPieceId();
        Optional<RealTimeAuctionInfo> byArtPieceId = realTimeAuctionRedisRepository.findByArtPieceId(artPieceId);

        if (byArtPieceId.isPresent()) {
            throw new AuctionAlreadyExistsException(artPieceId);
        }

        // artist 존재하지 않는 경우 예외 처리 (ref 체크 -> EntityNotFoundException, 추후 테스트 단계 지나면 제거)
        Long artistId = req.getArtistId();

        memberRepository.findById(artistId)
                .orElseThrow(() -> new MemberNotFoundException(artistId));

        // 해당 예술품에 대한 사진 목록 가져오기
        List<String> photoPaths = artPiecePhotoRepository
                .findArtPiecePhotoByArtPiece_Id(artPieceId)
                .stream()
                .map(ArtPiecePhoto::getFilePath)
                .collect(Collectors.toList());

        // redis 저장 후 ID 반환
        RealTimeAuctionInfo auctionInfo = RealTimeAuctionInfo.of(req, photoPaths);
        String auctionId = realTimeAuctionRedisRepository.save(auctionInfo).getId();

        return CreateAuction.Res.of(auctionId);
    }

    @Transactional(readOnly = true)
    public AuctionDetails.Res getAuction(String auctionKey) {
        // 실시간 경매 정보 찾아오기
        RealTimeAuctionInfo realTimeAuctionInfo = realTimeAuctionRedisRepository.findById(auctionKey)
                .orElseThrow(() -> new AuctionNotFoundException(auctionKey));

        // 작가 정보를 DB에서 가져온다
        Member artist = memberRepository.findById(realTimeAuctionInfo.getArtistId())
                .orElseThrow(() -> new MemberNotFoundException(realTimeAuctionInfo.getArtistId()));

        // 예술품 사진 정보를 DB에서 가져온다
        List<ArtPiecePhoto> artPiecePhotos = artPiecePhotoRepository.findArtPiecePhotoByArtPiece_Id(realTimeAuctionInfo.getArtPieceId());

        // 입찰자 목록을 가져온다
        List<AuctionBidInfo> bidInfos = bidderRedisRepository.findByAuctionKey(realTimeAuctionInfo.getId());

        // 응답 DTO 만들어서 반환
        return AuctionDetails.Res.of(realTimeAuctionInfo, bidInfos, artPiecePhotos, artist);
    }

    public RealTimeAuctionPreview.Res getAuctionList(Pageable pageable) {
        Slice<RealTimeAuctionInfo> infos = realTimeAuctionRedisRepository.findAll(pageable);
        List<RealTimeAuctionPreview.PreviewInfo> data = new ArrayList<>();

        for (RealTimeAuctionInfo info : infos) {
            Long artistId = info.getArtistId();

            Member artist = memberRepository.findById(artistId)
                    .orElseThrow(() -> new MemberNotFoundException(artistId));

            List<String> thumbnailPaths = artPiecePhotoRepository.findArtPiecePhotoByArtPiece_Id(info.getArtPieceId())
                    .stream()
                    .filter(ArtPiecePhoto::getIsThumbnail)
                    .map(ArtPiecePhoto::getFilePath)
                    .toList();

            String thumbnailPath = thumbnailPaths.isEmpty() ? "null" : thumbnailPaths.get(0);

            if (!thumbnailPath.equals("null")) {
                int lastDotIdx = thumbnailPath.lastIndexOf(".");

                String thumbnailFullPath = thumbnailPath.substring(0, lastDotIdx);
                String thumbnailExt = thumbnailPath.substring(lastDotIdx);
                thumbnailPath = thumbnailFullPath + "_compressed" + thumbnailExt;
            }

            RealTimeAuctionPreview.PreviewInfo preview = RealTimeAuctionPreview.PreviewInfo.of(info, artist, thumbnailPath);
            data.add(preview);
        }

        return RealTimeAuctionPreview.Res.of(infos, data);
    }

    @Transactional(readOnly = true)
    public AuctionBid.Res updatePrice(Principal principal, AuctionBid.Req req) {
        String auctionKey = req.getAuctionId();
        Long price = req.getPrice();
        Long bidderId = req.getBidderId();

        // 실시간 경매 정보를 가져온다
        RealTimeAuctionInfo auctionInfo = realTimeAuctionRedisRepository.findById(auctionKey)
                .orElseThrow(() -> new AuctionNotFoundException(auctionKey));

        // 현재가보다 낮은 경우 예외 처리
        if (auctionInfo.getAuctionCurrentPrice() >= price) {
            throw new InvalidBidPriceException(principal, auctionInfo.getAuctionCurrentPrice(), price, auctionKey);
        }

        // 입찰자 정보를 DB에서 찾아온 뒤 DTO 생성
        Member bidder = memberRepository.findById(bidderId)
                .orElseThrow(() -> new MemberNotFoundException(bidderId));

        // redis 입찰 기록 저장
        AuctionBidInfo bidInfo = AuctionBidInfo.of(bidder, auctionKey, price);
        bidderRedisRepository.save(bidInfo);

        // redis 경매 정보 업데이트
        auctionInfo.updateCurrentPrice(price);
        realTimeAuctionRedisRepository.save(auctionInfo);

        // 응답 객체를 만들어 반환
        return AuctionBid.Res.of(bidInfo);
    }

    public void updateImage(Long artPieceId, String s3Path) {
        RealTimeAuctionInfo auctionInfo = realTimeAuctionRedisRepository.findByArtPieceId(artPieceId)
                .orElseThrow(() -> new AuctionNotFoundException(artPieceId));

        auctionInfo.getPhotoPaths().add(s3Path);
        realTimeAuctionRedisRepository.save(auctionInfo);
    }

    public Integer updateLike(String auctionKey, Long memberId) {
        RealTimeAuctionInfo auctionInfo = realTimeAuctionRedisRepository.findById(auctionKey)
                .orElseThrow(() -> new AuctionNotFoundException(auctionKey));

        // 멤버 정보 찾기
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        // 이미 좋아요가 눌린 상태에서 다시 누르면 취소, 아니면 + 1
        Set<Long> likeMembers = auctionInfo.getLikeMembers();
        Integer like = likeMembers.size();

        if (likeMembers.contains(memberId)) {
            likeMembers.remove(memberId);
            like--;
        }
        else {
            likeMembers.add(memberId);
            like++;
        }

        auctionInfo.updateLike(like);
        realTimeAuctionRedisRepository.save(auctionInfo);

        return like;
    }

    public Long deleteAuction(String auctionKey, Long artistId) {
        RealTimeAuctionInfo info = realTimeAuctionRedisRepository.findById(auctionKey)
                .orElseThrow(() -> new AuctionNotFoundException(auctionKey));

        // 레디스에서 관련 경매 정보와 연관된 입찰 정보를 모두 삭제한다
        if (Objects.equals(info.getArtistId(), artistId)) {
            realTimeAuctionRedisRepository.deleteById(auctionKey);
            List<String> bidIDs = bidderRedisRepository.findByAuctionKey(auctionKey)
                            .stream()
                            .map(AuctionBidInfo::getId)
                            .toList();
            bidderRedisRepository.deleteAllById(bidIDs);
            return 1L;
        }

        return 0L;
    }

    public Long finishAuction(String auctionKey, Boolean isSold) {
        RealTimeAuctionInfo auctionInfo = realTimeAuctionRedisRepository.findById(auctionKey)
                .orElseThrow(() -> new AuctionNotFoundException(auctionKey));

        realTimeAuctionRedisRepository.deleteById(auctionKey);
        return auctionService.saveAuctionInfo(auctionInfo, isSold, 1L);
    }

    /*
    public Long finishAuctionWithBidder(String auctionKey, Long bidderId, Long finalPrice) {
        RealTimeAuctionInfo auctionInfo = realTimeAuctionRedisRepository.findById(auctionKey)
                .orElseThrow(() -> new AuctionNotFoundException(auctionKey));

        // 현재가보다 낮은 최종가가 들어온 경우 예외 처리
        if (auctionInfo.getAuctionCurrentPrice() > finalPrice) return 0L;

        // 레디스에서 삭제 후 DB에 경매 기록 저장
        auctionInfo.setAuctionCurrentPrice(finalPrice);
        realTimeAuctionRedisRepository.deleteById(auctionKey);

        return auctionService.saveAuctionInfo(auctionInfo, true, 1L);
    }
     */
}
