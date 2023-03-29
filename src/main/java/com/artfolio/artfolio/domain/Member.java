package com.artfolio.artfolio.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String name;

    @Column(length = 255, unique = true, nullable = false, updatable = false)
    private String email;

    @Column(nullable = false)
    private Boolean isCreator;

    @Column(name = "member_like", nullable = false)
    private Long like;

    @Column(length = 255, nullable = false)
    private String photo;

    @Column(length = 1500, nullable = false)
    private String content;

    @OneToMany(mappedBy = "bidder")
    private final Set<Auction> bids = new HashSet<>();

    @OneToMany(mappedBy = "creator")
    private final Set<ArtPiece> artPieces = new HashSet<>();

    @Builder
    public Member(String name, String email, Boolean isCreator, Long like, String photo, String content) {
        this.name = name;
        this.email = email;
        this.isCreator = isCreator;
        this.like = like;
        this.photo = photo;
        this.content = content;
    }

    public void updateArtPiece(ArtPiece artPiece) {
        artPieces.add(artPiece);
        artPiece.setCreator(this);
    }
}
