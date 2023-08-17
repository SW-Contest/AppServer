package com.artfolio.artfolio.user.util;

import com.artfolio.artfolio.user.dto.LoginDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

public class NaverLogin implements SocialLogin {
    @Value("${spring.security.oauth2.client.registration.naver.authorization-grant-type}")
    private String DEFAULT_GRANT_TYPE;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String NAVER_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String NAVER_CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.provider.naver.token_uri}")
    private String NAVER_TOKEN_URI;

    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
    private String NAVER_USER_INFO_URI;

    private final String code, state;
    private static LoginDto.NaverUserInfo userInfo;

    public NaverLogin(String code, String state) {
        this.code = code;
        this.state = state;
        userInfo = null;
    }

    @Override
    public LoginDto.NaverUserInfo login() {
        if (userInfo != null) return userInfo;

        LoginDto.NaverTokenRes token = getToken();
        userInfo = getUserInfo(token);

        return userInfo;
    }

    @Override
    public String getEmail() {
        if (userInfo != null) {
            return userInfo.getResponse().getEmail();
        }

        return null;
    }

    private LoginDto.NaverTokenRes getToken() {
        return WebClient.create()
                .post()
                .uri(NAVER_TOKEN_URI)
                .bodyValue(tokenRequest())
                .retrieve()
                .bodyToMono(LoginDto.NaverTokenRes.class)
                .block();
    }

    private LoginDto.NaverUserInfo getUserInfo(LoginDto.NaverTokenRes tokenResponse) {
        return WebClient.create()
                .get()
                .uri(NAVER_USER_INFO_URI)
                .header("Authorization", "Bearer " + tokenResponse.getAccess_token())
                .retrieve()
                .bodyToMono(LoginDto.NaverUserInfo.class)
                .block();
    }

    private MultiValueMap<String, String> tokenRequest() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add("grant_type", DEFAULT_GRANT_TYPE);
        formData.add("client_id", NAVER_CLIENT_ID);
        formData.add("client_secret", NAVER_CLIENT_SECRET);
        formData.add("code", code);
        formData.add("state", state);

        return formData;
    }
}
