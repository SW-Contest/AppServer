package com.artfolio.artfolio.business.controller;

import com.artfolio.artfolio.business.dto.FollowDto;
import com.artfolio.artfolio.business.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/follow")
@RestController
public class FollowController {
    private final FollowService followService;

    /* 유저 팔로워, 팔로잉 목록 반환 */
    @GetMapping("/{userId}")
    public ResponseEntity<FollowDto.getFollowInfoRes> getFollowInfos(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(followService.getFollowInfo(userId));
    }

    /* 유저 팔로워 추가 */
    @PostMapping
    public ResponseEntity<Long> toggleFollower(@RequestBody FollowDto.FollowUserReq req) {
        return ResponseEntity.ok(followService.toggleFollow(req));
    }

    /* fromUser가 toUser를 팔로우 했는지 확인 */
    @PostMapping("/check")
    public ResponseEntity<Long> checkFollow(@RequestBody FollowDto.FollowUserReq req) {
        Long result = followService.checkFollow(req) ? 1L : 0L;
        return ResponseEntity.ok(result);
    }
}
