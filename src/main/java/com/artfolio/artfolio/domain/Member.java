package com.artfolio.artfolio.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false, updatable = false)
    private String email;

    @Column(nullable = false)
    private Boolean isArtist;

    @Column(name = "member_like", nullable = false)
    private Long like;

    @Column(nullable = false)
    private String profilePhoto;

    @Column(length = 1500, nullable = false)
    private String content;

    @OneToMany(mappedBy = "artist")
    private final List<ArtPiece> artPieces = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private final List<MemberAuction> memberAuctions = new ArrayList<>();

    @Builder
    public Member(String name, String email, Boolean isArtist, Long like, String profilePhoto, String content) {
        this.name = name;
        this.email = email;
        this.isArtist = isArtist;
        this.like = like;
        this.profilePhoto = profilePhoto;
        this.content = content;
    }
}