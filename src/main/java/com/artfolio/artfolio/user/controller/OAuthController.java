package com.artfolio.artfolio.user.controller;

import com.artfolio.artfolio.user.dto.LoginDto;
import com.artfolio.artfolio.user.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class OAuthController {
    private final CustomOAuth2UserService customOAuth2UserService;

    @GetMapping("/login/oauth/{provider}")
    public ResponseEntity<?> login(
            @PathVariable("provider") String provider,
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            HttpServletResponse res
    ) {
        LoginDto.UserInfoRes userInfoRes = customOAuth2UserService.loadUser(res, provider, code, state);
        return ResponseEntity.ok(userInfoRes);
    }
}
