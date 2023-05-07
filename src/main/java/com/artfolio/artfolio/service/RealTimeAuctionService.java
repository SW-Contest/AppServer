package com.artfolio.artfolio.service;

import com.artfolio.artfolio.dto.RealTimeAuctionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class RealTimeAuctionService {
    private final RedisTemplate<Long, Object> redisTemplate;
    private final AuctionService auctionService;

    public Long createAuction(RealTimeAuctionInfo auctionInfo) {
        ValueOperations<Long, Object> vop = redisTemplate.opsForValue();
        Long auctionKey = auctionInfo.getArtPieceId();
        vop.setIfAbsent(auctionKey, auctionInfo);
        return auctionKey;
    }

    public Object getAuction(Long auctionKey) {
        return auctionGetter(auctionKey);
    }

    public Long updatePrice(Long auctionKey, Long price) {
        RealTimeAuctionInfo auctionInfo = (RealTimeAuctionInfo) auctionGetter(auctionKey);

        /* 현재가보다 낮은 경우 예외 처리 */
        if (auctionInfo.getAuctionCurrentPrice() >= price) return 0L;

        auctionInfo.setAuctionCurrentPrice(price);
        Boolean result = auctionUpdater(auctionKey, auctionInfo);
        return result ? 1L : 0L;
    }

    public Long updateLike(Long auctionKey, Long memberId) {
        RealTimeAuctionInfo auctionInfo = (RealTimeAuctionInfo) auctionGetter(auctionKey);

        /* 자신이 올린 경매에 좋아요 불가 */
        if (Objects.equals(auctionInfo.getArtistId(), memberId)) return 0L;

        auctionInfo.setLike(auctionInfo.getLike() + 1);
        Boolean result = auctionUpdater(auctionKey, auctionInfo);
        return result ? 1L : 0L;
    }

    public Long deleteAuction(Long auctionKey) {
        Object obj = auctionRemover(auctionKey);
        return obj == null ? 0L : 1L;
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
