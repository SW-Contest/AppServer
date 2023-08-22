package com.artfolio.artfolio.user.controller;

import com.artfolio.artfolio.user.dto.UserDto;
import com.artfolio.artfolio.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/test/sign-up")
    public ResponseEntity<Long> testUserSignUp(@RequestBody UserDto.SignUpReq req) {
        Long userId = userService.testUserSignUp(req);
        return ResponseEntity.ok(userId);
    }

    /* 유저가 참여하고 있는 진행중인 경매 목록 */
    @GetMapping("/auction/live/{userId}")
    public ResponseEntity<?> getLiveAuctionList(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.getLiveAuctionList(userId));
    }

    /* 유저가 낙찰받은 종료된 경매 기록 내역 */
    @GetMapping("/auction/finish/{userId}")
    public ResponseEntity<?> getFinishAuctionList(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.getBidAuctionList(userId));
    }

    /* 특정 유저가 등록한 예술품 리스트 반환 */
    @GetMapping("/art_piece/{userId}")
    public ResponseEntity<?> getArtPieceList(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.getArtPieceList(userId));
    }

    @GetMapping("/art_piece/like/{userId}")
    public ResponseEntity<UserDto.UserLikeArtPiecesRes> getLikeArtPieces(@PathVariable("userId") Long userId) {
        UserDto.UserLikeArtPiecesRes likeArtPieces = userService.getLikeArtPieces(userId);
        return ResponseEntity.ok(likeArtPieces);
    }

    @GetMapping("/auction/like/{userId}")
    public ResponseEntity<UserDto.UserLikeAuctionsRes> getLikeAuctions(@PathVariable("userId") Long userId) {
        UserDto.UserLikeAuctionsRes likeAuctions = userService.getLikeAuctions(userId);
        return ResponseEntity.ok(likeAuctions);
    }

    @PatchMapping("/content")
    public ResponseEntity<Long> updateUserContent(@RequestBody Map<String, Object> map) {
        String content = String.valueOf(map.get("content"));
        Long userId = Long.parseLong(String.valueOf(map.get("userId")));
        Long result = userService.updateUserContent(userId, content);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto.UserInfo> getUserInfo(@PathVariable("userId") Long userId) {
        UserDto.UserInfo userInfo = userService.getUserInfo(userId);
        return ResponseEntity.ok(userInfo);
    }
}