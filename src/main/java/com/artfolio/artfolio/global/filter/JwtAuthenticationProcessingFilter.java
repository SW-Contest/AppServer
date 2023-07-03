package com.artfolio.artfolio.global.filter;

import com.artfolio.artfolio.global.exception.UserNotFoundException;
import com.artfolio.artfolio.user.entity.User;
import com.artfolio.artfolio.user.repository.UserRepository;
import com.artfolio.artfolio.user.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Jwt 인증 필터
 * "/login" 이외의 URI 요청이 왔을 때 처리하는 필터
 *
 * 기본적으로 사용자는 요청 헤더에 AccessToken만 담아서 요청
 * AccessToken 만료 시에만 RefreshToken을 요청 헤더에 AccessToken과 함께 요청
 *
 * 1. RefreshToken이 없고, AccessToken이 유효한 경우 -> 인증 성공 처리, RefreshToken을 재발급하지는 않는다.
 * 2. RefreshToken이 없고, AccessToken이 없거나 유효하지 않은 경우 -> 인증 실패 처리, 403 FORBBIDEN ERROR
 * 3. RefreshToken이 있는 경우 -> DB의 RefreshToken과 비교하여 일치하면 AccessToken, RefreshToken 재발급 (RTR 방식)
 *                              인증 성공 처리는 하지 않고 실패 처리
 *
 */

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {
    private static final String NO_CHECK_URL = "/login";
    private static final String SOCIAL_USER_TEMP_PASSWORD = "temp_password";
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // "/login" 요청이 들어오면 다음 필터로 넘긴다
        if (request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 사용자 요청 헤더에서 RefrshToken 추출
        // --> 1. RefrshToken이 존재: AccessToken이 만료되어 요청한 경우에 해당
        // --> 2. RefrshToken이 없음: null 반환
        String refreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        if (refreshToken != null) {
            // AccessToken이 만료된 경우에 해당
            // DB의 RefreshToken과 일치하는지 검사후 일치하면 AccessToken과 RefreshToken을 둘 다 재발급
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            return;
        }

        else {
            // RefreshToken이 없거나 유효하지 않은 경우에 해당
            // AccessToken을 검사하고 인증을 처리하는 로직
            // --> AccessToken이 유효: 인증 객체가 담긴 상태로 다음 필터로 이동 -> 인증 성공
            // --> AccessToken이 유효X: 인증 객체가 담기지 않은 상태로 다음 필터로 이동 -> 403
            checkAccessTokenAndAuthentication(request, response, filterChain);
        }
    }

    /* refresh-Token으로 유저 정보를 찾고, Access/Refresh Token 재발급 */
    public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new UserNotFoundException(refreshToken));
        String reIssuedRefreshToken = reIssueRefreshToken(user);
        jwtService.sendAccessAndRefreshToken(response, jwtService.createAccessToken(user.getEmail()), reIssuedRefreshToken);
        userRepository.saveAndFlush(user);
    }

    /* Access-Token 체크 & 인증 처리 메서드 */
    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // access-token이 유효하면 토큰에서 이메일을 추출하고, 이메일 정보로 DB에서 유저를 찾아 인증처리 후 인가 (SecurityContext에 담기)
        jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .ifPresent(acc -> jwtService.extractEmail(acc)
                        .ifPresent(e -> userRepository.findByEmail(e)
                                .ifPresent(this::saveAuthentication)));

        filterChain.doFilter(request, response);
    }

    /* Refresh-Token 재발급 & DB에 업데이트 */
    private String reIssueRefreshToken(User user) {
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        user.updateRefreshToken(reIssuedRefreshToken);
        userRepository.saveAndFlush(user);
        return reIssuedRefreshToken;
    }

    /* 인증 허가 메서드 */
    public void saveAuthentication(User m) {
        String password = m.getPassword();     // 소셜 로그인 유저의 비밀번호 임의 설정
        if (password == null) {
            password = SOCIAL_USER_TEMP_PASSWORD;
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(m.getEmail())
                .password(password)
                .roles(m.getRole().name())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                authoritiesMapper.mapAuthorities(userDetails.getAuthorities())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
