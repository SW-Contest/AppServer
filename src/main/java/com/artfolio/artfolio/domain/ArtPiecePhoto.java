package com.artfolio.artfolio.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ArtPiecePhoto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String path;

    @Setter
    @ManyToOne
    @JoinColumn(name = "art_piece_id")
    private ArtPiece artPiece;

    @Builder
    public ArtPiecePhoto(String path, ArtPiece artPiece) {
        this.path = path;
        this.artPiece = artPiece;
    }
}
