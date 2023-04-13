package com.artfolio.artfolio.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ArtPiecePhoto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String fileExtension;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private Boolean isThumbnail;

    @Setter
    @ManyToOne
    @JoinColumn(name = "art_piece_id", nullable = false)
    private ArtPiece artPiece;

    @Builder
    public ArtPiecePhoto(String originalFilename, String fileExtension, String filePath, ArtPiece artPiece, Boolean isThumbnail) {
        this.originalFilename = originalFilename;
        this.fileExtension = fileExtension;
        this.filePath = filePath;
        this.artPiece = artPiece;
        this.isThumbnail = isThumbnail;
    }
}
