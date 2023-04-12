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

    @Column(nullable = false)
    private Boolean isSold;

    @Setter
    @OneToOne
    @JoinColumn(name = "auction_bidder_id")
    private Member bidder;

    @ManyToOne
    @JoinColumn(name = "art_piece_id", nullable = false)
    private ArtPiece artPiece;

    @Builder
    public Auction(Long startPrice, Long finalPrice, Boolean isSold, Member bidder, ArtPiece artPiece) {
        this.startPrice = startPrice;
        this.finalPrice = finalPrice;
        this.isSold = isSold;
        this.bidder = bidder;
        this.artPiece = artPiece;
    }
}
