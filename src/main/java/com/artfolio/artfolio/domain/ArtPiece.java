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

    @Column(nullable = false)
    private String title;

    @Column(length = 10000, nullable = false)
    private String content;

    @Column(name = "art_piece_like", nullable = false)
    private Long like;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private Member creator;

    @Column(nullable = false)
    @OneToMany(mappedBy = "artPiece")
    private final Set<ArtPiecePhoto> artPiecePhotos = new HashSet<>();

    @Column(nullable = false)
    @OneToMany(mappedBy = "artPiece")
    private final Set<Auction> auctions = new HashSet<>();

    @Builder
    public ArtPiece(String title, String content, Long like, Member creator) {
        this.title = title;
        this.content = content;
        this.like = like;
        this.creator = creator;
    }

    public void updatePhoto(ArtPiecePhoto artPiecePhoto) {
        artPiecePhotos.add(artPiecePhoto);
        artPiecePhoto.setArtPiece(this);
    }
}
