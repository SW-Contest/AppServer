package com.artfolio.artfolio.dto;

import com.artfolio.artfolio.domain.Member;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter @Setter
public class AuctionBidInfoRes {
    private BidderInfo bidderInfo;
    private Long bidPrice;
    private LocalDateTime bidDate;

    @AllArgsConstructor
    @Getter
    public static class BidderInfo {
        private Long bidderId;
        private String name;
        private String email;
        private String profilePhotoPath;
        private Long like;

        public static BidderInfo of(Member member) {
            return new BidderInfo(
                    member.getId(),
                    member.getName(),
                    member.getEmail(),
                    member.getProfilePhoto(),
                    member.getLike()
            );
        }
    }
}
