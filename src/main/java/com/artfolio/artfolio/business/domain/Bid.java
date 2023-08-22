package com.artfolio.artfolio.business.domain;

import com.artfolio.artfolio.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Bid {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "bidder_id")
    private User bidder;

    @OneToOne
    @JoinColumn(name = "auction_id")
    private Auction auction;

    private Long bidPrice;
    private LocalDateTime bidDate;

    @Builder
    public Bid(User bidder, Auction auction, Long bidPrice, LocalDateTime bidDate) {
        this.bidder = bidder;
        this.auction = auction;
        this.bidPrice = bidPrice;
        this.bidDate = bidDate;
    }
}
