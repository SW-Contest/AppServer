package com.artfolio.artfolio.domain;

import com.artfolio.artfolio.domain.audit.AuditingFields;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    @JoinColumn(name = "artist_id", nullable = false)
    private Member artist;

    @Column(nullable = false)
    @OneToMany(mappedBy = "artPiece")
    private final List<ArtPiecePhoto> artPiecePhotos = new ArrayList<>();

    @Column(nullable = false)
    @OneToMany(mappedBy = "artPiece")
    private final List<Auction> auctions = new ArrayList<>();

    @Builder
    public ArtPiece(String title, String content, Long like, Member artist) {
        this.title = title;
        this.content = content;
        this.like = like;
        this.artist = artist;
    }
}
