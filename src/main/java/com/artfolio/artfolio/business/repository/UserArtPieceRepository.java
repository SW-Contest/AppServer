package com.artfolio.artfolio.business.repository;

import com.artfolio.artfolio.business.domain.ArtPiece;
import com.artfolio.artfolio.business.domain.UserArtPiece;
import com.artfolio.artfolio.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserArtPieceRepository extends JpaRepository<UserArtPiece, Long> {
    Optional<UserArtPiece> findByUserAndArtPiece(User user, ArtPiece artPiece);
}
