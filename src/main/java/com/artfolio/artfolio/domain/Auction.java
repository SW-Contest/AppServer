package com.artfolio.artfolio.domain;

import com.artfolio.artfolio.domain.audit.AuditingFields;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Auction extends AuditingFields {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auction_start_price", nullable = false, updatable = false)
    private Long startPrice;

    @Column(name ="auction_final_price", nullable = false, updatable = false)
    private Long finalPrice;

    @Setter
    @OneToOne
    @JoinColumn(name = "art_bidder_id")
    private Member bidder;

    @Builder
    public Auction(Long startPrice, Long finalPrice) {
        this.startPrice = startPrice;
        this.finalPrice = finalPrice;
    }
}
