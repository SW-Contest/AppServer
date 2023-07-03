package com.artfolio.artfolio.business.domain;

import com.artfolio.artfolio.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Auction extends AuditingFields {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* TODO: 나중에 세션 정보에서 빼오도록 리팩터링 (Audit) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private User artist;

    @Column(nullable = false, updatable = false)
    private Long startPrice;

    @Column(nullable = false, updatable = false)
    private Long finalPrice;

    @Column(nullable = false, name = "auction_like")
    private Integer like;

    @Column(nullable = false)
    private Boolean isSold;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidder_id")
    private User bidder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "art_piece_id", nullable = false)
    private ArtPiece artPiece;

    @OneToMany(mappedBy = "auction")
    private final List<MemberAuction> memberAuctions = new ArrayList<>();

    @Builder
    public Auction(User artist, Long startPrice, Long finalPrice, Integer like, Boolean isSold, User bidder, ArtPiece artPiece) {
        this.artist = artist;
        this.startPrice = startPrice;
        this.finalPrice = finalPrice;
        this.like = like;
        this.isSold = isSold;
        this.bidder = bidder;
        this.artPiece = artPiece;
    }
}
