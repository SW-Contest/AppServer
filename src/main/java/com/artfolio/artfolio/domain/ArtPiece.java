package com.artfolio.artfolio.domain;

import com.artfolio.artfolio.domain.audit.AuditingFields;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ArtPiece extends AuditingFields {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String title;

    @Column(length = 10000, nullable = false)
    private String content;

    @Column(nullable = false)
    private Long art_like;

    @Setter
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private Member creator;

    @Column(nullable = false)
    @OneToMany(mappedBy = "artPiece")
    private final Set<ArtPiecePhoto> artPiecePhotos = new HashSet<>();

    @Builder
    public ArtPiece(String title, String content, Long art_like, Member creator) {
        this.title = title;
        this.content = content;
        this.art_like = art_like;
        this.creator = creator;
    }

    public void addPhoto(ArtPiecePhoto artPiecePhoto) {
        artPiecePhotos.add(artPiecePhoto);
        artPiecePhoto.setArtPiece(this);
    }
}
