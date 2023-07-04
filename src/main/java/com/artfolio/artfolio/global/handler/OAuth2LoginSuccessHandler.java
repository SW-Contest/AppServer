package com.artfolio.artfolio.global.handler;

import com.artfolio.artfolio.user.dto.CustomOAuth2User;
import com.artfolio.artfolio.user.entity.User;
import com.artfolio.artfolio.user.dto.Role;
import com.artfolio.artfolio.user.repository.UserRepository;
import com.artfolio.artfolio.user.service.JwtService;
import com.artfolio.artfolio.global.exception.UserNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공");

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // User의 Role이 GUEST -> 처음 요청한 회원이므로 추가 정보를 받는 페이지로 리다이렉트
        if (oAuth2User.getRole() == Role.GUEST) {
            String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
            response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
            response.sendRedirect("user/sign-up");  // 프론트의 회원가입 추가 정보 입력 폼으로 리다이렉션

            jwtService.sendAccessAndRefreshToken(response, accessToken, null);

            // Role GUEST -> USER
            User findUser = userRepository.findByEmail(oAuth2User.getEmail())
                    .orElseThrow(() -> new UserNotFoundException(oAuth2User.getEmail()));

            findUser.authorizeUser();
        }

        else {
            // 로그인에 성공한 경우: access, refresh 토큰 생성
            User findUser = userRepository.findByEmail(oAuth2User.getEmail())
                    .orElseThrow(() -> new UserNotFoundException(oAuth2User.getEmail()));

            loginSuccess(response, oAuth2User, findUser);
        }

    }

    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User, User findUser) throws IOException {
        String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
        String refreshToken = jwtService.createRefreshToken();

        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);
        response.addHeader("UserId", String.valueOf(findUser.getId()));

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);
    }
}
