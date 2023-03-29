package com.artfolio.artfolio.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Auction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long artStartPrice;

    @Column(nullable = false)
    private Long artFinalPrice;

    @ManyToOne
    @JoinColumn(name = "art_bidder_id")
    private Member bidder;

    @Builder
    public Auction(Long artStartPrice, Long artFinalPrice) {
        this.artStartPrice = artStartPrice;
        this.artFinalPrice = artFinalPrice;
    }

    public void bigSuccessful(Member bidder) {
        bidder.getBids().add(this);
        this.bidder = bidder;
    }
}
