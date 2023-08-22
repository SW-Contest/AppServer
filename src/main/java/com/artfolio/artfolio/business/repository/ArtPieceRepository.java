package com.artfolio.artfolio.business.repository;

import com.artfolio.artfolio.business.domain.ArtPiece;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ArtPieceRepository extends JpaRepository<ArtPiece, Long> {
}
