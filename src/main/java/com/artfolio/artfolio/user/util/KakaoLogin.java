package com.artfolio.artfolio.user.util;

import com.artfolio.artfolio.user.dto.LoginDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

public class KakaoLogin implements SocialLogin {
    @Value("${spring.security.oauth2.client.registration.naver.authorization-grant-type}")
    private String DEFAULT_GRANT_TYPE;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String KAKAO_TOKEN_URI;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info.uri}")
    private String KAKAO_USER_INFO_URI;

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
            return userInfo.getKakaoAccount().getEmail();
        }

        return null;
    }

    private LoginDto.KakaoTokenRes getToken() {
        return WebClient.create()
                .post()
                .uri(KAKAO_TOKEN_URI)
                .header("Content-type", DEFAULT_CONTENT_TYPE)
                .bodyValue(tokenRequest())
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

    private MultiValueMap<String, String> tokenRequest() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add("grant_type", DEFAULT_GRANT_TYPE);
        formData.add("client_id", KAKAO_CLIENT_ID);
        formData.add("redirect_uri", redirectUri);
        formData.add("code", code);

        return formData;
    }
}
