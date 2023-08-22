package com.artfolio.artfolio.business.domain;

import com.artfolio.artfolio.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "auction", indexes = {
        @Index(name = "idx_auction_uuid", columnList = "auctionUuId")
})
@Entity
public class Auction extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String auctionUuId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private User artist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "art_piece_id", nullable = false)
    private ArtPiece artPiece;

    @Column(nullable = false, updatable = false)
    private Long startPrice;

    @Column(nullable = false)
    private Long currentPrice;

    @Column(nullable = false)
    private Boolean isFinish;

    @Column(nullable = false)
    private Integer likes;

    @OneToOne
    private Bid bid;

    @OneToMany(mappedBy = "auction", orphanRemoval = true, cascade = CascadeType.ALL)
    private final List<UserAuction> userAuctions = new ArrayList<>();


    @Builder
    public Auction(String title, String content, User artist, ArtPiece artPiece, Long startPrice, Long currentPrice) {
        this.auctionUuId = UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
        this.artist = artist;
        this.artPiece = artPiece;
        this.startPrice = startPrice;
        this.currentPrice = currentPrice;
        this.likes = 0;
        this.isFinish = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Auction auction = (Auction) o;
        return Objects.equals(auctionUuId, auction.auctionUuId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(auctionUuId);
    }


    public void updateCurrentPrice(Long price) {
        this.currentPrice = price;
    }

    public void updateUserAuction(UserAuction ua) {
        this.userAuctions.add(ua);
        ua.setAuction(this);
    }

    public void increaseLike(UserAuction ua) {
        this.likes++;
        ua.toggleIsLiked();
    }

    public void decreaseLike(UserAuction ua) {
        this.likes--;
        ua.toggleIsLiked();
    }

    public void updateBidInfo(Bid bid) {
        this.isFinish = true;
        this.bid = bid;
    }
}

