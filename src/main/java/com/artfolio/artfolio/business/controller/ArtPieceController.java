package com.artfolio.artfolio.business.controller;

import com.artfolio.artfolio.business.dto.ArtPieceDto;
import com.artfolio.artfolio.business.service.ArtPieceService;
import com.artfolio.artfolio.business.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/art_piece")
@RequiredArgsConstructor
@RestController
public class ArtPieceController {
    private final ArtPieceService artPieceService;
    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<Long> createArtPiece(@RequestBody ArtPieceDto.CreationReq req) {
        return new ResponseEntity<>(artPieceService.createArtPiece(req), HttpStatus.CREATED);
    }

    @PostMapping("/image")
    public ResponseEntity<Long> uploadArtPiecePhoto(
            @RequestParam("artPieceId") Long artPieceId,
            @RequestParam("files") MultipartFile[] files
    ) {
        Long result = imageService.uploadImage(artPieceId, files);
        return ResponseEntity.ok(result);
    }
}
