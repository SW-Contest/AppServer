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

// 멤버에서 그동안 진행한 경매 리스트를 보여주려면 1:N 구조를 가져야 함
// 근데, 옥션 입장에서 낙찰자는 1명이니까 1:1임