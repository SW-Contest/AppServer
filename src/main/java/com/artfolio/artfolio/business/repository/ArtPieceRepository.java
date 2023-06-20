package com.artfolio.artfolio.business.repository;

import com.artfolio.artfolio.business.domain.ArtPiece;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtPieceRepository extends JpaRepository<ArtPiece, Long> {
    @EntityGraph(attributePaths = { "artist" } )
    Optional<ArtPiece> findArtPieceById(Long artPieceId);
}
