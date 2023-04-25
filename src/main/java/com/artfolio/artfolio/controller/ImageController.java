package com.artfolio.artfolio.controller;

import com.artfolio.artfolio.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RequestMapping(value = "/image")
@RequiredArgsConstructor
@RestController
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<Long> uploadArtPiecePhoto(
            @RequestParam("artpieceId") Long artPieceId,
            @RequestParam("files") MultipartFile[] files
    ) {
        Long result = imageService.uploadImage(artPieceId, files);
        return ResponseEntity.ok(result);
    }
}
