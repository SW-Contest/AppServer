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

    @Column(length = 255, nullable = false)
    private String path;

    @Setter
    @ManyToOne
    @JoinColumn(name = "art_piece_id", nullable = false)
    private ArtPiece artPiece;

    @Builder
    public ArtPiecePhoto(String path, ArtPiece artPiece) {
        this.path = path;
        this.artPiece = artPiece;
    }
}
