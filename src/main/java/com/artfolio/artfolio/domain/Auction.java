package com.artfolio.artfolio.domain;

import jakarta.persistence.*;
import lombok.*;

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

    @Setter
    @OneToOne
    @JoinColumn(name = "art_bidder_id")
    private Member bidder;

    @Builder
    public Auction(Long artStartPrice, Long artFinalPrice) {
        this.artStartPrice = artStartPrice;
        this.artFinalPrice = artFinalPrice;
    }
}
