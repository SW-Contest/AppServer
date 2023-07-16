package com.artfolio.artfolio.user.controller;

import com.artfolio.artfolio.user.dto.CustomOAuth2User;
import com.artfolio.artfolio.user.dto.UserDto;
import com.artfolio.artfolio.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/oauth")
@RequiredArgsConstructor
@RestController
public class OAuthController {
    private final UserService userService;

    @GetMapping("/loginInfo")
    public ResponseEntity<UserDto.OAuth2LoginInfoRes> oauthLoginInfo(Authentication authentication) {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getEmail();
        return ResponseEntity.ok(userService.getSocialUserInfo(email));
    }
}
