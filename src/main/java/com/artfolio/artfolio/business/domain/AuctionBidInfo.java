package com.artfolio.artfolio.business.domain;

import com.artfolio.artfolio.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@Getter
@RedisHash(value = "bidder", timeToLive = 3600 * 24 * 7)
public class AuctionBidInfo {
    @Id private String id;
    @Indexed private final String auctionKey;
    @Indexed private final Long bidderId;
    private final Long bidPrice;
    private final String name;
    private final String username;
    private final String profilePhotoPath;
    private final LocalDateTime bidDate;

    @Builder
    private AuctionBidInfo(
            String auctionKey,
            Long bidderId,
            Long bidPrice,
            String name,
            String username,
            String profilePhotoPath,
            LocalDateTime bidDate
    ) {
        this.auctionKey = auctionKey;
        this.bidderId = bidderId;
        this.bidPrice = bidPrice;
        this.name = name;
        this.username = username;
        this.profilePhotoPath = profilePhotoPath;
        this.bidDate = bidDate;
    }

    /* DB에서 꺼내온 입찰자 정보로 redis 엔티티를 생성해주는 정적 메서드 */
    public static AuctionBidInfo of(User user, String auctionKey, Long bidPrice) {
        return AuctionBidInfo.builder()
                .auctionKey(auctionKey)
                .bidderId(user.getId())
                .bidPrice(bidPrice)
                .name(user.getNickname())
                .username(user.getEmail())
                .profilePhotoPath(user.getProfilePhoto())
                .bidDate(LocalDateTime.now())
                .build();
    }
}
