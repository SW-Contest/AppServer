package com.artfolio.artfolio.service;

import com.artfolio.artfolio.domain.ArtPiece;
import com.artfolio.artfolio.domain.ArtPiecePhoto;
import com.artfolio.artfolio.dto.ArtPieceImageReq;
import com.artfolio.artfolio.exception.ArtPieceNotFoundException;
import com.artfolio.artfolio.repository.ArtPiecePhotoRepository;
import com.artfolio.artfolio.repository.ArtPieceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class ImageService {
    private final ArtPiecePhotoRepository artPiecePhotoRepository;
    private final ArtPieceRepository artPieceRepository;

    public void uploadImage(
            Long artPieceId,
            String filePath,
            Boolean isThumbnail,
            List<ArtPieceImageReq> images
    ) {
        ArtPiece artPiece = artPieceRepository.findById(artPieceId)
                .orElseThrow(() -> new ArtPieceNotFoundException(artPieceId));

        List<ArtPiecePhoto> photos = images.stream()
                .map(dto -> dto.toEntity(artPiece, filePath, isThumbnail))
                .toList();

        artPiecePhotoRepository.saveAll(photos);
    }
}
