package com.artfolio.artfolio.domain;

import com.artfolio.artfolio.domain.audit.AuditingFields;
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
public class Member extends AuditingFields {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_name",length = 255, nullable = false)
    private String name;

    @Column(name = "member_email",length = 255, unique = true, nullable = false, updatable = false)
    private String email;

    @Column(nullable = false)
    private Boolean isCreator;

    @Column(name = "member_like",nullable = false)
    private Long like;

    @Column(length = 255, nullable = false)
    private String profilePhoto;

    @Column(name = "member_content",length = 1500, nullable = false)
    private String content;

    @OneToMany(mappedBy = "creator")
    private final Set<ArtPiece> artPieces = new HashSet<>();

    @OneToOne(mappedBy = "bidder")
    private Auction auction;

    @Builder
    public Member(String name, String email, Boolean isCreator, Long like, String profilePhoto, String content) {
        this.name = name;
        this.email = email;
        this.isCreator = isCreator;
        this.like = like;
        this.profilePhoto = profilePhoto;
        this.content = content;
    }

    public void updateArtPiece(ArtPiece artPiece) {
        artPieces.add(artPiece);
        artPiece.setCreator(this);
    }
}