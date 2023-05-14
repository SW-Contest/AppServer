package com.artfolio.artfolio.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter @Setter
public class AuctionBidInfoRes {
    private MemberInfo bidderInfo;
    private Long bidPrice;
    private LocalDateTime bidDate;
}

