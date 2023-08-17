package com.artfolio.artfolio.user.controller;

import com.artfolio.artfolio.user.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class OAuthController {
    private final CustomOAuth2UserService customOAuth2UserService;

    @GetMapping("/login/oauth/{provider}")
    public void login(
            @PathVariable("provider") String provider,
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            @RequestParam(value = "redirectUri", required = false) String redirectUri,
            HttpServletResponse res
    ) {
        customOAuth2UserService.login(res, provider, code, state, redirectUri);
    }
}
