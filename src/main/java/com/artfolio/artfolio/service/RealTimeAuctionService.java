package com.artfolio.artfolio.service;

import com.artfolio.artfolio.domain.ArtPiecePhoto;
import com.artfolio.artfolio.dto.RealTimeAuctionInfo;
import com.artfolio.artfolio.exception.AuctionNotFoundException;
import com.artfolio.artfolio.repository.ArtPiecePhotoRepository;
import com.artfolio.artfolio.repository.RealTimeAuctionRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class RealTimeAuctionService {
    private final AuctionService auctionService;
    private final ArtPiecePhotoRepository artPiecePhotoRepository;
    private final RealTimeAuctionRedisRepository realTimeAuctionRedisRepository;

    public String createAuction(RealTimeAuctionInfo auctionInfo) {
        List<String> artPiecePhotos = artPiecePhotoRepository
                .getArtPiecePhotoByArtPiece_Id(auctionInfo.getArtPieceId())
                .stream()
                .map(ArtPiecePhoto::getFilePath)
                .collect(Collectors.toList());

        auctionInfo.setLike(0L);
        auctionInfo.setPhotoPaths(artPiecePhotos);
        auctionInfo.setAuctionCurrentPrice(auctionInfo.getAuctionStartPrice());

        RealTimeAuctionInfo save = realTimeAuctionRedisRepository.save(auctionInfo);

        return save.getId();
    }

    public Object getAuction(String auctionKey) {
        return realTimeAuctionRedisRepository.findById(auctionKey)
                .orElseThrow(() -> new AuctionNotFoundException(auctionKey));
    }

    public void getAuctionList(Long start, Pageable pageable) {
    }

    public Long updatePrice(String auctionKey, Long bidderId, Long price) {
        RealTimeAuctionInfo auctionInfo = realTimeAuctionRedisRepository.findById(auctionKey)
                .orElseThrow(() -> new AuctionNotFoundException(auctionKey));

        /* 현재가보다 낮은 경우 예외 처리 */
        if (auctionInfo.getAuctionCurrentPrice() >= price) return 0L;

        auctionInfo.setBidderId(bidderId);
        auctionInfo.setAuctionCurrentPrice(price);
        realTimeAuctionRedisRepository.save(auctionInfo);

        return 1L;
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

        if (Objects.equals(info.getArtistId(), artistId)) {
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

    /*
    private Object auctionGetter(Long auctionKey) {
        ValueOperations<Long, Object> vop = redisTemplate.opsForValue();
        return vop.get(auctionKey);
    }

    private Boolean auctionUpdater(Long auctionKey, RealTimeAuctionInfo auctionInfo) {
        ValueOperations<Long, Object> vop = redisTemplate.opsForValue();
        return vop.setIfPresent(auctionKey, auctionInfo);
    }

    private Object auctionRemover(Long auctionKey) {
        ValueOperations<Long, Object> vop = redisTemplate.opsForValue();
        return vop.getAndDelete(auctionKey);
    }
     */
}
