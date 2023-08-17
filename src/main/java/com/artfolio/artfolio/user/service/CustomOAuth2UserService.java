package com.artfolio.artfolio.user.service;

import com.artfolio.artfolio.user.dto.LoginDto;
import com.artfolio.artfolio.user.dto.SocialType;
import com.artfolio.artfolio.user.entity.User;
import com.artfolio.artfolio.user.repository.UserRepository;
import com.artfolio.artfolio.user.util.KakaoLogin;
import com.artfolio.artfolio.user.util.NaverLogin;
import com.artfolio.artfolio.user.util.SocialLogin;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public void login(HttpServletResponse res, String provider, String code, String state, String redirectUri) {
        log.info("CustomOAuth2UserService.loadUser() 실행");

        log.info("provider=" + provider + " code=" + code + " state=" + state + " redirectUri=" + redirectUri);

        SocialType socialType = getSocialType(provider);
        SocialLogin socialLogin = getLoginObject(socialType, code, state, redirectUri);

        if (socialLogin == null) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        LoginDto.UserInfo userInfo = socialLogin.login();

        String email = socialLogin.getEmail();
        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken();

        Optional<User> userOp = userRepository.findByEmail(email);

        if (userOp.isEmpty()) {
            User user = userRepository.save(userInfo.toEntity(socialType, refreshToken));
            jwtService.sendAccessAndRefreshAndUserId(res, user.getId(), accessToken, refreshToken);
        } else {
            Long userId = userOp.get().getId();
            jwtService.sendAccessAndRefreshAndUserId(res, userId, accessToken, refreshToken);
        }
    }

    private SocialType getSocialType(String provider) {
        if (provider.equals(SocialType.NAVER.getType())) return SocialType.NAVER;
        else if (provider.equals(SocialType.KAKAO.getType())) return SocialType.KAKAO;
        return null;
    }

    private SocialLogin getLoginObject(SocialType socialType, String code, String state, String redirectUri) {
        if (SocialType.NAVER.equals(socialType)) return new NaverLogin(code, state);
        else if (SocialType.KAKAO.equals(socialType)) return new KakaoLogin(code, redirectUri);
        return null;
    }
}
