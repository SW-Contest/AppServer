package com.artfolio.artfolio.repository;

import com.artfolio.artfolio.domain.ArtPiecePhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtPiecePhotoRepository extends JpaRepository<ArtPiecePhoto, Long> {
    List<ArtPiecePhoto> getArtPiecePhotoByArtPiece_Id(Long artPieceId);
}
