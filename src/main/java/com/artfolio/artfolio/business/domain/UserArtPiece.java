package com.artfolio.artfolio.business.domain;

import com.artfolio.artfolio.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserArtPiece {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    @ManyToOne
    @JoinColumn(name = "art_piece_id")
    private ArtPiece artPiece;

    @Column(nullable = false)
    private Boolean isLiked;

    public UserArtPiece(User user, ArtPiece artPiece) {
        this.user = user;
        this.artPiece = artPiece;
        this.isLiked = false;
    }

    public void toggleIsLiked() {
        this.isLiked = !isLiked;
    }
}
