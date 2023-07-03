package com.artfolio.artfolio.user.dto;

import com.artfolio.artfolio.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

/* 각 소셜에서 받아오는 데이터가 다르기 때문에 소셜별로 받는 데이터를 분기 처리하는 DTO 클래스 */
@Getter
public class OAuthAttributes {
    private final String nameAttributeKey;
    private final OAuth2UserInfo oAuth2UserInfo;

    @Builder
    public OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oAuth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oAuth2UserInfo = oAuth2UserInfo;
    }

    /* SocialType에 맞는 메서드를 호출하여 OAuthAttributes 객체 반환
    * - userAttributeName -> OAuth2 로그인시 키(PK)값
    * - attributes -> OAuth 서비스의 유저 정보드
    * */
    public static OAuthAttributes of(SocialType socialType, String userAttributeName, Map<String, Object> attributes) {
        if (socialType == SocialType.NAVER) {
            return ofNaver(userAttributeName, attributes);
        }

        if (socialType == SocialType.KAKAO) {
            return ofKakao(userAttributeName, attributes);
        }

        return ofGoogle(userAttributeName, attributes);
    }

    private static OAuthAttributes ofKakao(String userAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userAttributeName)
                .oAuth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    private static OAuthAttributes ofNaver(String userAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userAttributeName)
                .oAuth2UserInfo(new NaverOAuth2UserInfo(attributes))
                .build();
    }

    private static OAuthAttributes ofGoogle(String userAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userAttributeName)
                .oAuth2UserInfo(new GoogleOAuth2UserInfo(attributes))
                .build();
    }

    public User toEntity(SocialType socialType, OAuth2UserInfo oAuth2UserInfo) {
        return User.builder()
                .email(UUID.randomUUID() + "@socialUser.com")
                .password("socialPassword")
                .nickname(oAuth2UserInfo.getNickname() == null ? "No Nickname" : oAuth2UserInfo.getNickname())
                .profilePhoto(oAuth2UserInfo.getImageUrl())
                .role(Role.USER)
                .socialType(socialType)
                .socialId(oAuth2UserInfo.getId())
                .like(0)
                .content("No Content")
                .build();
    }
}
