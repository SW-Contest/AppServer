package com.artfolio.artfolio.domain.redis;

import com.artfolio.artfolio.domain.Member;
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
    @Indexed private String auctionKey;
    private Long bidderId;
    private Long like;
    private Long bidPrice;
    private String name;
    private String email;
    private String profilePhotoPath;
    private LocalDateTime bidDate;

    @Builder
    public AuctionBidInfo(
            String auctionKey,
            Long bidderId,
            Long like,
            Long bidPrice,
            String name,
            String email,
            String profilePhotoPath,
            LocalDateTime bidDate
    ) {
        this.auctionKey = auctionKey;
        this.bidderId = bidderId;
        this.like = like;
        this.bidPrice = bidPrice;
        this.name = name;
        this.email = email;
        this.profilePhotoPath = profilePhotoPath;
        this.bidDate = bidDate;
    }

    /* DB에서 꺼내온 입찰자 정보로 redis 엔티티를 생성해주는 정적 메서드 */
    public static AuctionBidInfo of(Member member, String auctionKey, Long bidPrice) {
        return AuctionBidInfo.builder()
                .auctionKey(auctionKey)
                .bidderId(member.getId())
                .like(member.getLike())
                .bidPrice(bidPrice)
                .name(member.getName())
                .email(member.getEmail())
                .profilePhotoPath(member.getProfilePhoto())
                .bidDate(LocalDateTime.now())
                .build();
    }
}
