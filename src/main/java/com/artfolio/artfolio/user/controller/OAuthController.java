package com.artfolio.artfolio.user.controller;

import com.artfolio.artfolio.user.dto.CustomOAuth2User;
import com.artfolio.artfolio.user.dto.LoginDto;
import com.artfolio.artfolio.user.dto.UserDto;
import com.artfolio.artfolio.user.service.CustomOAuth2UserService;
import com.artfolio.artfolio.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RequiredArgsConstructor
@RestController
public class OAuthController {
    private final UserService userService;
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

    @GetMapping("/oauth/loginInfo")
    public ResponseEntity<UserDto.OAuth2LoginInfoRes> oauthLoginInfo(Authentication authentication) {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getEmail();
        return ResponseEntity.ok(userService.getSocialUserInfo(email));
    }
}
