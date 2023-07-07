package com.artfolio.artfolio.business.controller;

import com.artfolio.artfolio.business.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class FollowController {
    private final FollowService followService;

    /* 유저 팔로워 목록 반환 */
    public void getFollowers() {

    }

    /* 유저 팔로잉 목록 반환 */
    public void getFollowings() {

    }
}
