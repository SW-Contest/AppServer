package com.artfolio.artfolio.app.repository;

import com.artfolio.artfolio.app.domain.ArtPiecePhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtPiecePhotoRepository extends JpaRepository<ArtPiecePhoto, Long> {
    List<ArtPiecePhoto> findArtPiecePhotoByArtPiece_Id(Long artPieceId);
}
