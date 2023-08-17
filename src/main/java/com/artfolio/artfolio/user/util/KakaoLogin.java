package com.artfolio.artfolio.user.util;

import com.artfolio.artfolio.user.dto.LoginDto;
import lombok.*;
import org.springframework.web.reactive.function.client.WebClient;

public class KakaoLogin implements SocialLogin {
    private static final String DEFAULT_GRANT_TYPE = "authorization_code";
    private static final String KAKAO_CLIENT_ID = "7f9e17f1074f3fee9f9de841ec5cfb02";
    private static final String KAKAO_TOKEN_URI = "https://kauth.kakao.com/oauth/token";
    private static final String KAKAO_USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";
    private static final String DEFAULT_CONTENT_TYPE = "application/x-www.form-urlencoded;charset=utf-8";
    private static LoginDto.KakaoUserInfo userInfo;

    private final String redirectUri;
    private final String code;

    public KakaoLogin(String code, String redirectUri) {
        this.code = code;
        this.redirectUri = redirectUri;
        userInfo = null;
    }

    @Override
    public LoginDto.KakaoUserInfo login() {
        if (userInfo != null) return userInfo;

        LoginDto.KakaoTokenRes token = getToken();
        userInfo = getUserInfo(token);

        return userInfo;
    }

    @Override
    public String getEmail() {
        if (userInfo != null) {
            return userInfo.getKakao_account().getEmail();
        }

        return null;
    }

    private LoginDto.KakaoTokenRes getToken() {
        String params = "grant_type=" + DEFAULT_GRANT_TYPE + "&" +
                "client_id=" + KAKAO_CLIENT_ID + "&" +
                "redirect_uri=" + redirectUri + "&" +
                "code=" + code;

        TokenRequest req = TokenRequest.builder()
                .client_id(KAKAO_CLIENT_ID)
                .redirect_uri(redirectUri)
                .grant_type(DEFAULT_GRANT_TYPE)
                .code(code)
                .build();

        return WebClient.create()
                .post()
                .uri(KAKAO_TOKEN_URI + "?" + params)
                .header("Content-type", DEFAULT_CONTENT_TYPE)
                .retrieve()
                .bodyToMono(LoginDto.KakaoTokenRes.class)
                .block();
    }

    private LoginDto.KakaoUserInfo getUserInfo(LoginDto.KakaoTokenRes tokenResponse) {
        return WebClient.create()
                .post()
                .uri(KAKAO_USER_INFO_URI)
                .headers(h -> {
                    h.add("Authorization", "Bearer " + tokenResponse.getAccess_token());
                    h.add("Content-type", DEFAULT_CONTENT_TYPE);
                })
                .retrieve()
                .bodyToMono(LoginDto.KakaoUserInfo.class)
                .block();
    }

    @Getter @Setter @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    private static class TokenRequest {
        private String client_id;
        private String redirect_uri;
        private String grant_type;
        private String code;
    }
}
