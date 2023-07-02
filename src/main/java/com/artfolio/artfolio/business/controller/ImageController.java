package com.artfolio.artfolio.business.controller;

import com.artfolio.artfolio.business.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RequestMapping("/image")
@RequiredArgsConstructor
@RestController
public class ImageController {
    private final ImageService imageService;

    /*
    @PostMapping("/upload")
    public ResponseEntity<Long> uploadArtPiecePhoto(
            @RequestParam("artPieceId") Long artPieceId,
            @RequestParam("files") MultipartFile[] files
    ) {
        Long result = imageService.uploadImage(artPieceId, files);
        return ResponseEntity.ok(result);
    }

     */
}
