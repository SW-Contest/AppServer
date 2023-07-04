package com.artfolio.artfolio.business.domain;

import com.artfolio.artfolio.business.dto.ArtPieceDto;
import com.artfolio.artfolio.user.entity.User;
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
    private User artist;

    @Column(nullable = false)
    @OneToMany(mappedBy = "artPiece")
    private final List<ArtPiecePhoto> artPiecePhotos = new ArrayList<>();

    @Column(nullable = false)
    @OneToMany(mappedBy = "artPiece", fetch = FetchType.EAGER)
    private final List<Auction> auctions = new ArrayList<>();

    @Builder
    public ArtPiece(String title, String content, Long like, User artist) {
        this.title = title;
        this.content = content;
        this.like = like;
        this.artist = artist;
    }

    public static ArtPiece of(ArtPieceDto.CreationReq req, User artist) {
        return ArtPiece.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .like(0L)
                .artist(artist)
                .build();
    }

    public void updatePhoto(ArtPiecePhoto artPiecePhoto) {
        this.artPiecePhotos.add(artPiecePhoto);
        artPiecePhoto.setArtPiece(this);
    }
}
