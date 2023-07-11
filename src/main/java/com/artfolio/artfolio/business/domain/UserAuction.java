package com.artfolio.artfolio.business.domain;

import com.artfolio.artfolio.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserAuction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Setter
    @ManyToOne
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @Column(nullable = false)
    private Boolean isLiked;

    public UserAuction(User user, Auction auction) {
        this.user = user;
        this.auction = auction;
        this.isLiked = false;
    }

    public void toggleIsLiked() {
        this.isLiked = !isLiked;
    }
}
