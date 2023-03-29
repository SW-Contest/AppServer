package com.artfolio.artfolio.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ArtPiecePhoto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String path;

    @ManyToOne
    @JoinColumn(name = "art_piece_id")
    private ArtPiece artPiece;

    @Builder
    public ArtPiecePhoto(String path, ArtPiece artPiece) {
        this.path = path;
        this.artPiece = artPiece;
    }

    public void updateArtPiecePhoto(ArtPiece artPiece) {
        artPiece.getArtPiecePhotos().add(this);
        this.artPiece = artPiece;
    }
}
