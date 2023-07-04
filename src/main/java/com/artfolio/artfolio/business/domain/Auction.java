package com.artfolio.artfolio.business.domain;

import com.artfolio.artfolio.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Auction extends AuditingFields {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
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

    @Column(nullable = false, updatable = false)
    private Long currentPrice;

    @Column(nullable = false, name = "auction_like")
    private Integer like;

    @Column(nullable = false)
    private Boolean isFinish;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidder_id")
    private User bidder;

    @OneToMany(mappedBy = "auction")
    private final List<MemberAuction> memberAuctions = new ArrayList<>();

    @Transient
    private final Set<Long> likeUsersId = new HashSet<>();

    @Builder
    public Auction(String title, String content, User artist, ArtPiece artPiece, Long startPrice, Long currentPrice, Integer like, Boolean isFinish, User bidder) {
        this.auctionUuId = UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
        this.artist = artist;
        this.artPiece = artPiece;
        this.startPrice = startPrice;
        this.currentPrice = currentPrice;
        this.like = 0;
        this.isFinish = false;
        this.bidder = null;
    }

    public void finishAuction() {
        this.isFinish = true;
    }

    public void updateLastBidder(User bidder) {
        this.bidder = bidder;
    }

    public void updateCurrentPrice(Long price) {
        this.currentPrice = price;
    }

    public void updateLike(Long userId) {
        if (likeUsersId.contains(userId)) likeUsersId.remove(userId);
        else likeUsersId.add(userId);
        this.like = likeUsersId.size();
    }
}
