package com.artfolio.artfolio.app.domain;

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

    @Column(nullable = false, unique = true, updatable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false, updatable = false)
    private String email;

    @Column(nullable = false)
    private Boolean isArtist;

    @Column(name = "member_like", nullable = false)
    private Integer like;

    @Column(nullable = false)
    private String profilePhoto;

    @Column(length = 1500, nullable = false)
    private String content;

    @Setter
    @Column(nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "artist")
    private final List<ArtPiece> artPieces = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private final List<MemberAuction> memberAuctions = new ArrayList<>();

    @Builder
    public Member(String username, String password, String name, String email, Boolean isArtist, Integer like, String profilePhoto, String content, Boolean active) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.isArtist = isArtist;
        this.like = like;
        this.profilePhoto = profilePhoto;
        this.content = content;
        this.active = active;
    }
}