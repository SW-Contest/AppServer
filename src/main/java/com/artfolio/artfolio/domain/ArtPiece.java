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
public class ArtPiece {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String title;

    @Column(length = 10000, nullable = false)
    private String content;

    @Column(nullable = false)
    private Long art_like;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private Member creator;

    @Column(nullable = false)
    @OneToMany(mappedBy = "artPiece")
    private Set<ArtPiecePhoto> artPiecePhotos = new HashSet<>();

    @Builder
    public ArtPiece(String title, String content, Long art_like, Member creator) {
        this.title = title;
        this.content = content;
        this.art_like = art_like;
        this.creator = creator;
    }

    public void updateArtPiece(Member creator) {
        creator.getArtPieces().add(this);
        this.creator = creator;
    }
}
