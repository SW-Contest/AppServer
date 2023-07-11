package com.artfolio.artfolio.user.service;

import com.artfolio.artfolio.global.exception.AccessTokenInvalidException;
import com.artfolio.artfolio.user.entity.User;
import com.artfolio.artfolio.user.repository.UserRepository;
import com.artfolio.artfolio.global.exception.UserNotFoundException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Getter
@RequiredArgsConstructor
@Service
public class JwtService {
    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer ";

    private final UserRepository userRepository;

    /* Access-Token을 생성하는 빌더 반환 메서드 */
    public String createAccessToken(String email) {
        Date now = new Date();

        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT) // JWT의 subject 지정
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))   // 토큰 만료시간
                .withClaim(EMAIL_CLAIM, email) // 클레임 추가시 withClaim 으로 설정 (여러개 가능, 여기서는 이메일만 사용)
                .sign(Algorithm.HMAC512(secretKey));  // HMAC512 알고리즘 사용 (secret key로 암호화)
    }

    /* Refresh-Token을 생성하는 빌더 반환 메서드 */
    public String createRefreshToken() {
        Date now = new Date();

        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() +refreshTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    /* Access-Token 헤더에 담아 보내기 */
    public void sendAccessToken(HttpServletResponse res, String accessToken) {
        res.setStatus(HttpServletResponse.SC_OK);
        setAccessTokenHeader(res, accessToken);
        log.info("발급된 Access Token : {}", accessToken);
    }

    /* Access-Token, RefreshToken 헤더에 담아 보내기 */
    public void sendAccessAndRefreshToken(HttpServletResponse res, String accessToken, String refreshToken) {
        res.setStatus(HttpServletResponse.SC_OK);
        setAccessTokenHeader(res, accessToken);
        setRefreshTokenHeader(res, refreshToken);
    }

    /* 요청 헤더에서 Access-Token을 추출하는 메서드 */
    public Optional<String> extractAccessToken(HttpServletRequest req) {
        return Optional.ofNullable(req.getHeader(accessHeader))
                .filter(acc -> acc.startsWith(BEARER))
                .map(acc -> acc.replace(BEARER, ""));
    }

    /* 요청 헤더에서 Refresh-Token을 추출하는 메서드 */
    public Optional<String> extractRefreshToken(HttpServletRequest req) {
        return Optional.ofNullable(req.getHeader(refreshHeader))
                .filter(ref -> ref.startsWith(BEARER))
                .map(ref -> ref.replace(BEARER, ""));
    }

    /* Access-Token에서 email 추출 */
    public Optional<String> extractEmail(String accessToken) {
        try {
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(accessToken)   // access token을 검증하고 유효하지 않다면 예외 발생
                    .getClaim(EMAIL_CLAIM) // 이메일 클레임 가져오기
                    .asString()
            );
        } catch (Exception e) {
            log.error("유효하지 않은 엑세스 토큰");
            throw new AccessTokenInvalidException(accessToken);
        }
    }

    /* Access-Token 헤더 설정 */
    public void setAccessTokenHeader(HttpServletResponse res, String accessToken) {
        res.setHeader(accessHeader, accessToken);
    }

    /* Refresh-Token 헤더 설정 */
    public void setRefreshTokenHeader(HttpServletResponse res, String refreshToken) {
        res.setHeader(refreshHeader, refreshToken);
    }

    /* RefreshToken DB 저장 & 업데이트 */
    public void updateRefreshToken(String email, String refreshToken) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        user.updateRefreshToken(refreshToken);
        userRepository.saveAndFlush(user);
    }

    /* 토큰 유효성 검사 */
    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
            throw new AccessTokenInvalidException(token);
        }
    }
}
