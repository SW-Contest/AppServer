package com.artfolio.artfolio.repository;

import com.artfolio.artfolio.domain.ArtPiece;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtPieceRepository extends JpaRepository<ArtPiece, Long> {
}
