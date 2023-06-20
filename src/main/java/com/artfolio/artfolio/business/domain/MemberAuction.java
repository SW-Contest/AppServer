package com.artfolio.artfolio.business.domain;

import com.artfolio.artfolio.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MemberAuction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "memberId", nullable = false)
    private User member;

    @ManyToOne
    @JoinColumn(name = "auctionId", nullable = false)
    private Auction auction;

    public MemberAuction(User member, Auction auction) {
        this.member = member;
        this.auction = auction;
    }
}
