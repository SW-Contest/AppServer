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
    private String hashedFilename;

    @Column(nullable = false)
    private String fileExtension;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private Boolean isThumbnail;

    @Setter
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "art_piece_id", nullable = false)
    private ArtPiece artPiece;

    @Builder
    public ArtPiecePhoto(String originalFilename, String hashedFilename, String fileExtension, String filePath, String contentType, Long size, Boolean isThumbnail, ArtPiece artPiece) {
        this.originalFilename = originalFilename;
        this.hashedFilename = hashedFilename;
        this.fileExtension = fileExtension;
        this.filePath = filePath;
        this.contentType = contentType;
        this.size = size;
        this.isThumbnail = isThumbnail;
        this.artPiece = artPiece;
    }
}
