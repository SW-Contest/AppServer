package com.artfolio.artfolio.service;

import com.artfolio.artfolio.domain.ArtPiecePhoto;
import com.artfolio.artfolio.dto.RealTimeAuctionInfo;
import com.artfolio.artfolio.repository.ArtPiecePhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class RealTimeAuctionService {
    private final RedisTemplate<Long, Object> redisTemplate;
    private final AuctionService auctionService;
    private final ArtPiecePhotoRepository artPiecePhotoRepository;

    public Long createAuction(RealTimeAuctionInfo auctionInfo) {
        ValueOperations<Long, Object> vop = redisTemplate.opsForValue();

        Long auctionKey = auctionInfo.getArtPieceId();
        List<String> artPiecePhotos = artPiecePhotoRepository
                .getArtPiecePhotoByArtPiece_Id(auctionKey)
                .stream()
                .map(ArtPiecePhoto::getFilePath)
                .collect(Collectors.toList());

        auctionInfo.setLike(0L);
        auctionInfo.setPhotoPaths(artPiecePhotos);
        auctionInfo.setAuctionCurrentPrice(auctionInfo.getAuctionStartPrice());

        vop.setIfAbsent(auctionKey, auctionInfo);
        return auctionKey;
    }

    public Object getAuction(Long auctionKey) {
        return auctionGetter(auctionKey);
    }

    public Long updatePrice(Long auctionKey, Long bidderId, Long price) {
        RealTimeAuctionInfo auctionInfo = (RealTimeAuctionInfo) auctionGetter(auctionKey);

        /* 현재가보다 낮은 경우 예외 처리 */
        if (auctionInfo.getAuctionCurrentPrice() >= price) return 0L;

        auctionInfo.setBidderId(bidderId);
        auctionInfo.setAuctionCurrentPrice(price);
        Boolean result = auctionUpdater(auctionKey, auctionInfo);
        return result ? 1L : 0L;
    }

    public Long updateLike(Long auctionKey, Long memberId) {
        RealTimeAuctionInfo auctionInfo = (RealTimeAuctionInfo) auctionGetter(auctionKey);

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
        auctionUpdater(auctionKey, auctionInfo);

        return likes;
    }

    public Long deleteAuction(Long auctionKey, Long artistId) {
        RealTimeAuctionInfo info = (RealTimeAuctionInfo) auctionGetter(auctionKey);

        if (Objects.equals(info.getArtistId(), artistId)) {
            Object obj = auctionRemover(auctionKey);
            return obj == null ? 0L : 1L;
        }

        return 0L;
    }

    public Long finishAuction(Long auctionKey, Boolean isSold) {
        RealTimeAuctionInfo auctionInfo = (RealTimeAuctionInfo) auctionRemover(auctionKey);
        return auctionService.saveAuctionInfo(auctionInfo, isSold);
    }

    public Long finishAuctionWithBidder(Long auctionKey, Long bidderId, Long finalPrice) {
        RealTimeAuctionInfo auctionInfo = (RealTimeAuctionInfo) auctionGetter(auctionKey);

        /* 현재가보다 낮은 최종가가 들어온 경우 예외 처리 */
        if (auctionInfo.getAuctionCurrentPrice() > finalPrice) return 0L;

        /* 레디스에서 삭제 후 DB에 경매 기록 저장 */
        auctionInfo.setBidderId(bidderId);
        auctionInfo.setAuctionCurrentPrice(finalPrice);
        auctionRemover(auctionKey);

        return auctionService.saveAuctionInfo(auctionInfo, true);
    }

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
}
