package com.artfolio.artfolio.business.repository;

import com.artfolio.artfolio.business.domain.ArtPiecePhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtPiecePhotoRepository extends JpaRepository<ArtPiecePhoto, Long> {
    List<ArtPiecePhoto> findArtPiecePhotoByArtPiece_Id(Long artPieceId);
}
