package com.artfolio.artfolio.user.controller;

import com.artfolio.artfolio.user.dto.UserSignUpDto;
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

    /* 추가 정보를 입력받을 경우 사용 */
    @PostMapping("/sign-up")
    public ResponseEntity<Long> signUp(@RequestBody UserSignUpDto userSignUpDto) throws Exception {
        return ResponseEntity.ok(userService.signUp(userSignUpDto));
    }

    /* 유저가 참여하고 있는 진행중인 경매 목록
    @GetMapping("/auction/live/{userId}")
    public ResponseEntity<?> getLiveAuctionList(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.getLiveAuctionList(userId));
    }

     */

    /* 유저가 참여했던 종료된 경매 기록 내역
    @GetMapping("/auction/finish/{userId}")
    public ResponseEntity<?> getFinishAuctionList(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.getFinishAuctionList(userId));
    }

     */

    /* 특정 유저가 등록한 예술품 리스트 반환 */
    @GetMapping("/art_piece/{userId}")
    public ResponseEntity<?> getArtPieceList(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.getArtPieceList(userId));
    }
}