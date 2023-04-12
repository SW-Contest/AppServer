package com.artfolio.artfolio.domain;

import com.artfolio.artfolio.domain.audit.AuditingFields;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ArtPiecePhoto extends AuditingFields {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "art_piece_photo_path",length = 255, nullable = false)
    private String path;

    @Setter
    @ManyToOne
    @JoinColumn(name = "art_piece_id", nullable = false)
    private ArtPiece artPiece;

    @Column(nullable = false)
    private Boolean isThumbnail;

    @Builder
    public ArtPiecePhoto(String path, ArtPiece artPiece, Boolean isThumbnail) {
        this.path = path;
        this.artPiece = artPiece;
        this.isThumbnail = isThumbnail;
    }
}
