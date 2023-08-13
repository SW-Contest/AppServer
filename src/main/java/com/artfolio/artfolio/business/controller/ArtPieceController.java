package com.artfolio.artfolio.business.controller;

import com.artfolio.artfolio.business.domain.AIInfo;
import com.artfolio.artfolio.business.dto.ArtPieceDto;
import com.artfolio.artfolio.business.dto.AuctionDto;
import com.artfolio.artfolio.business.dto.ImageDto;
import com.artfolio.artfolio.business.service.ArtPieceService;
import com.artfolio.artfolio.business.service.ImageService;
import com.artfolio.artfolio.business.service.VoiceExtractService;
import com.artfolio.artfolio.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



@Slf4j
@RequestMapping("/art_piece")
@RequiredArgsConstructor
@RestController
public class ArtPieceController {
    private final ArtPieceService artPieceService;
    private final ImageService imageService;
    private final VoiceExtractService voiceExtractService;

    @PostMapping
    public ResponseEntity<Long> createArtPiece(@RequestBody ArtPieceDto.CreationReq req, Authentication authentication) {
        log.info("is authenticated? = " + authentication.isAuthenticated());
        return new ResponseEntity<>(artPieceService.createArtPiece(req), HttpStatus.CREATED);
    }

    @GetMapping("/{artPieceId}")
    public ResponseEntity<ArtPieceDto.ArtPieceInfoRes> getArtPiece(@PathVariable("artPieceId") Long artPieceId) {
        return ResponseEntity.ok(artPieceService.getArtPiece(artPieceId));
    }

    @DeleteMapping
    public ResponseEntity<Long> deleteArtPiece(@RequestBody ArtPieceDto.DeletionReq req) {
        return ResponseEntity.ok(artPieceService.deleteArtPiece(req));
    }

    @PatchMapping("/title")
    public ResponseEntity<Long> updateArtPieceTitle(@RequestBody ArtPieceDto.UpdateTitleReq req) {
        return ResponseEntity.ok(artPieceService.updateTitle(req));
    }

    @PatchMapping("/content")
    public ResponseEntity<Long> updateArtPieceContent(@RequestBody ArtPieceDto.UpdateContentReq req) {
        return ResponseEntity.ok(artPieceService.updateContent(req));
    }

    @PatchMapping("/like")
    public ResponseEntity<Integer> updateArtPieceLike(@RequestBody ArtPieceDto.UpdateLikeReq req) {
        return ResponseEntity.ok(artPieceService.updateLike(req));
    }

    @PostMapping("/image")
    public ResponseEntity<Long> uploadArtPiecePhoto(
            @RequestParam("artistId") Long artistId,
            @RequestParam("artPieceId") Long artPieceId,
            @RequestParam("files") MultipartFile[] files
    ) {
        Long result = imageService.uploadImage(artistId, artPieceId, files);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/image")
    public ResponseEntity<Long> deleteArtPiecePhoto(@RequestBody ImageDto.DeleteReq req) {
        Long result = imageService.deleteFile(req);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/like/{artPieceId}")
    public ResponseEntity<UserDto.LikeUsersRes> getLikeUsers(@PathVariable("artPieceId") Long artPieceId) {
        UserDto.LikeUsersRes list = artPieceService.getLikeUserList(artPieceId);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/analyze/image")
    public ResponseEntity<AuctionDto.AIInfo> analyzeImage(@RequestBody ArtPieceDto.AnalyzeReq req) throws Exception {
        log.info("analyzeImage() 실행 dto = {}", req.toString());
        AuctionDto.AIInfo aiInfo = imageService.analyzeImage(req);
        log.info("분석 완료 : {}", aiInfo.toString());
        return ResponseEntity.ok(aiInfo);
    }

    @GetMapping("/analyze/info/{artPieceId}")
    public ResponseEntity<AIInfo> getArtPieceAnalyzeInfo(@PathVariable("artPieceId") Long artPieceId) {
        AIInfo aiInfo = artPieceService.getArtPieceAnalyzeInfo(artPieceId);
        return ResponseEntity.ok(aiInfo);
    }

    @PostMapping("/voice/extract")
    public void extractVoice(
            @RequestParam("artPieceId") Long artPieceId,
            @RequestParam("text") String text
        ) {
        voiceExtractService.extractVoice(artPieceId, text);
    }
}
