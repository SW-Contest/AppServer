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

    @Value("${spring.security.oauth2.client.registration.naver.authorization-grant-type}")
    private String DEFAULT_GRANT_TYPE;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String NAVER_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String NAVER_CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.provider.naver.token_uri}")
    private String NAVER_TOKEN_URL;

    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
    private String NAVER_USER_INFO_URI;


    public LoginDto.UserInfoRes loadUser(HttpServletResponse res, String provider, String code, String state) {
        log.info("CustomOAuth2UserService.loadUser() 실행");

        SocialType socialType = getSocialType(provider);
        LoginDto.TokenRes tokenResponse = getToken(socialType, code, state);

        log.info("token 정보 획득 : {}", tokenResponse.toString());

        LoginDto.UserInfoRes userInfo = getUserInfo(socialType, tokenResponse);

        String email = userInfo.getResponse().getEmail();
        Optional<User> userOp = userRepository.findByEmail(email);

        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken();

        // 이전 로그인 기록이 없는 경우 유저 정보를 저장
        if (userOp.isEmpty()) {
            User user = userRepository.save(userInfo.toEntity(socialType, refreshToken));
            res.setHeader("UserId", String.valueOf(user.getId()));
        }
        else {
            res.setHeader("UserId", String.valueOf(userOp.get().getId()));
        }

        jwtService.sendAccessAndRefreshToken(res, accessToken, refreshToken);
        userInfo.getResponse().setAccessToken(accessToken);
        userInfo.getResponse().setRefreshToken(refreshToken);

        return userInfo;
    }

    private SocialType getSocialType(String registrationId) {
        if (NAVER.equals(registrationId)) return SocialType.NAVER;
        if (KAKAO.equals(registrationId)) return SocialType.KAKAO;
        return SocialType.GOOGLE;
    }

    private LoginDto.UserInfoRes getUserInfo(SocialType socialType, LoginDto.TokenRes tokenResponse) {
        if (socialType.equals(SocialType.NAVER)) return getNaverUserInfo(tokenResponse);
        else if (socialType.equals(SocialType.KAKAO)) return getKakaoUserInfo(tokenResponse);
        return null;
    }

    private LoginDto.UserInfoRes getNaverUserInfo(LoginDto.TokenRes tokenResponse) {
        return WebClient.create()
                .get()
                .uri(NAVER_USER_INFO_URI)
                .header("Authorization", "Bearer " + tokenResponse.getAccess_token())
                .retrieve()
                .bodyToMono(LoginDto.UserInfoRes.class)
                .block();
    }

    private LoginDto.UserInfoRes getKakaoUserInfo(LoginDto.TokenRes tokenResponse) {
        return null;
    }

    private LoginDto.TokenRes getToken(SocialType socialType, String code, String state) {
        if (socialType.equals(SocialType.KAKAO)) return getTokenFromKakao(socialType, code, state);
        else if (socialType.equals(SocialType.NAVER)) return getTokenFromNaver(socialType, code, state);
        return null;
    }

    private LoginDto.TokenRes getTokenFromNaver(SocialType socialType, String code, String state) {
        return WebClient.create()
                .post()
                .uri(NAVER_TOKEN_URL)
                .bodyValue(tokenRequest(socialType, code, state))
                .retrieve()
                .bodyToMono(LoginDto.TokenRes.class)
                .block();
    }

    private LoginDto.TokenRes getTokenFromKakao(SocialType socialType, String code, String state) {
        return null;
    }

    private MultiValueMap<String, String> tokenRequest(SocialType socialType, String code, String state) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        if (socialType.equals(SocialType.NAVER)) {
            formData.add("client_id", NAVER_CLIENT_ID);
            formData.add("client_secret", NAVER_CLIENT_SECRET);
            formData.add("grant_type", DEFAULT_GRANT_TYPE);
            formData.add("code", code);
            formData.add("state", state);
        }

        else if (socialType.equals(SocialType.KAKAO)) {

        }

        return formData;
    }
}
