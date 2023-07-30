package com.artfolio.artfolio.business.repository;

import com.artfolio.artfolio.business.domain.ArtPiece;
import com.artfolio.artfolio.business.domain.UserArtPiece;
import com.artfolio.artfolio.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserArtPieceRepository extends JpaRepository<UserArtPiece, Long> {
    Optional<UserArtPiece> findByUserAndArtPiece(User user, ArtPiece artPiece);
    List<UserArtPiece> findByArtPiece_IdAndIsLikedTrue(Long artPieceId);

    @Query("select ua from UserArtPiece ua " +
            "join fetch ArtPiece " +
            "where ua.isLiked = true " +
            "and " +
            "ua.user.id = :userId")
    List<UserArtPiece> findAllUserLikes(Long userId);
}
