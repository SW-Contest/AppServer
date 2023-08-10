package com.artfolio.artfolio.user.service;

import com.artfolio.artfolio.user.dto.LoginDto;
import com.artfolio.artfolio.user.entity.User;
import com.artfolio.artfolio.user.dto.SocialType;
import com.artfolio.artfolio.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    private static final String NAVER = "naver";
    private static final String KAKAO = "kakao";
    private static final String DEFAULT_CONTENT_TYPE = "application/x-www.form-urlencoded;charset=utf-8";

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

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String KAKAO_TOKEN_URI;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info.uri}")
    private String KAKAO_USER_INFO_URI;


    public LoginDto.UserInfo loadUser(HttpServletResponse res, String provider, String code, String state) {
        log.info("CustomOAuth2UserService.loadUser() 실행");

        SocialType socialType = getSocialType(provider);
        LoginDto.TokenRes tokenResponse = getToken(socialType, code, state);

        log.info("token 정보 획득 : {}", tokenResponse.toString());

        LoginDto.UserInfo userInfo = getUserInfo(socialType, tokenResponse);

        String email, refreshToken;

        // 카카오 로그인인 경우
        if (userInfo instanceof LoginDto.KakaoUserInfo kakaoUserInfo) {
            email = kakaoUserInfo.getKakaoAccount().getEmail();
            Optional<User> userOp = userRepository.findByEmail(email);

            refreshToken = jwtService.createRefreshToken();

            if (userOp.isEmpty()) {
                User user = userRepository.save(kakaoUserInfo.toEntity(socialType, refreshToken));
                res.setHeader("UserId", String.valueOf(user.getId()));
            }

            else {
                res.setHeader("UserId", String.valueOf(userOp.get().getId()));
            }
        }

        // 네이버 로그인인 경우
        else {
            LoginDto.NaverUserInfo naverUserInfo = (LoginDto.NaverUserInfo) userInfo;
            email = naverUserInfo.getResponse().getEmail();
            Optional<User> userOp = userRepository.findByEmail(email);

            refreshToken = jwtService.createRefreshToken();

            if (userOp.isEmpty()) {
                User user = userRepository.save(naverUserInfo.toEntity(socialType, refreshToken));
                res.setHeader("UserId", String.valueOf(user.getId()));
            }

            else {
                res.setHeader("UserId", String.valueOf(userOp.get().getId()));
            }
        }

        String accessToken = jwtService.createAccessToken(email);
        jwtService.sendAccessAndRefreshToken(res, accessToken, refreshToken);

        return userInfo;
    }

    private SocialType getSocialType(String registrationId) {
        if (NAVER.equals(registrationId)) return SocialType.NAVER;
        else if (KAKAO.equals(registrationId)) return SocialType.KAKAO;
        return null;
    }

    private LoginDto.UserInfo getUserInfo(SocialType socialType, LoginDto.TokenRes tokenResponse) {
        if (socialType.equals(SocialType.NAVER)) return getNaverUserInfo((LoginDto.NaverTokenRes) tokenResponse);
        else if (socialType.equals(SocialType.KAKAO)) return getKakaoUserInfo((LoginDto.KakaoTokenRes) tokenResponse);
        return null;
    }

    private LoginDto.UserInfo getNaverUserInfo(LoginDto.NaverTokenRes tokenResponse) {
        return WebClient.create()
                .get()
                .uri(NAVER_USER_INFO_URI)
                .header("Authorization", "Bearer " + tokenResponse.getAccess_token())
                .retrieve()
                .bodyToMono(LoginDto.NaverUserInfo.class)
                .block();
    }

    private LoginDto.UserInfo getKakaoUserInfo(LoginDto.KakaoTokenRes tokenResponse) {
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

    private LoginDto.TokenRes getToken(SocialType socialType, String code, String state) {
        if (socialType.equals(SocialType.KAKAO)) return getTokenFromKakao(socialType, code, state);
        else if (socialType.equals(SocialType.NAVER)) return getTokenFromNaver(socialType, code, state);
        return null;
    }

    private LoginDto.NaverTokenRes getTokenFromNaver(SocialType socialType, String code, String state) {
        return WebClient.create()
                .post()
                .uri(NAVER_TOKEN_URI)
                .bodyValue(tokenRequest(socialType, code, state))
                .retrieve()
                .bodyToMono(LoginDto.NaverTokenRes.class)
                .block();
    }

    private LoginDto.KakaoTokenRes getTokenFromKakao(SocialType socialType, String code, String state) {
        return WebClient.create()
                .post()
                .uri(KAKAO_TOKEN_URI)
                .header("Content-type", DEFAULT_CONTENT_TYPE)
                .bodyValue(tokenRequest(socialType, code, state))
                .retrieve()
                .bodyToMono(LoginDto.KakaoTokenRes.class)
                .block();
    }

    private MultiValueMap<String, String> tokenRequest(SocialType socialType, String code, String state) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", DEFAULT_GRANT_TYPE);

        if (socialType.equals(SocialType.NAVER)) {
            formData.add("client_id", NAVER_CLIENT_ID);
            formData.add("client_secret", NAVER_CLIENT_SECRET);
            formData.add("code", code);
            formData.add("state", state);
        }

        else if (socialType.equals(SocialType.KAKAO)) {
            formData.add("client_id", KAKAO_CLIENT_ID);
            formData.add("redirect_uri", KAKAO_REDIRECT_URI);
            formData.add("code", code);
        }

        return formData;
    }
}
