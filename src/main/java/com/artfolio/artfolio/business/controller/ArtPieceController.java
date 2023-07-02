package com.artfolio.artfolio.business.controller;

import com.artfolio.artfolio.business.dto.ArtPieceDto;
import com.artfolio.artfolio.business.service.ArtPieceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/art_piece")
@RequiredArgsConstructor
@RestController
public class ArtPieceController {
    private final ArtPieceService artPieceService;

    @PostMapping
    public ResponseEntity<Long> createArtPiece(@RequestBody ArtPieceDto.CreationReq req) {
        return ResponseEntity.ok(artPieceService.createArtPiece(req));
    }
}
