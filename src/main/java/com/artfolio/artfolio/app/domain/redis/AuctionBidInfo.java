package com.artfolio.artfolio.app.domain.redis;

import com.artfolio.artfolio.app.domain.Member;
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
    private Long bidPrice;
    private Integer like;
    private String name;
    private String username;
    private String email;
    private String profilePhotoPath;
    private LocalDateTime bidDate;

    @Builder
    public AuctionBidInfo(
            String auctionKey,
            Long bidderId,
            Long bidPrice,
            Integer like,
            String name,
            String username,
            String email,
            String profilePhotoPath,
            LocalDateTime bidDate
    ) {
        this.auctionKey = auctionKey;
        this.bidderId = bidderId;
        this.bidPrice = bidPrice;
        this.like = like;
        this.name = name;
        this.username = username;
        this.email = email;
        this.profilePhotoPath = profilePhotoPath;
        this.bidDate = bidDate;
    }

    /* DB에서 꺼내온 입찰자 정보로 redis 엔티티를 생성해주는 정적 메서드 */
    public static AuctionBidInfo of(Member member, String auctionKey, Long bidPrice) {
        return AuctionBidInfo.builder()
                .auctionKey(auctionKey)
                .bidderId(member.getId())
                .bidPrice(bidPrice)
                .like(member.getLike())
                .name(member.getName())
                .username(member.getUsername())
                .email(member.getEmail())
                .profilePhotoPath(member.getProfilePhoto())
                .bidDate(LocalDateTime.now())
                .build();
    }
}
