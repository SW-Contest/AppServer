package com.artfolio.artfolio.business.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ArtPiecePhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileExtension;

    @Column(nullable = false, unique = true)
    private String filePath;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private Boolean isThumbnail;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "art_piece_id", nullable = false)
    private ArtPiece artPiece;

    @Builder
    public ArtPiecePhoto(String fileName, String fileExtension, String filePath, Long size, Boolean isThumbnail, ArtPiece artPiece) {
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.filePath = filePath;
        this.size = size;
        this.isThumbnail = isThumbnail;
        this.artPiece = artPiece;
    }
}
