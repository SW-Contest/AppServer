package com.artfolio.artfolio.user.service;

import com.artfolio.artfolio.user.dto.CustomOAuth2User;
import com.artfolio.artfolio.user.entity.User;
import com.artfolio.artfolio.user.dto.SocialType;
import com.artfolio.artfolio.user.dto.OAuthAttributes;
import com.artfolio.artfolio.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;

    private static final String NAVER = "naver";
    private static final String KAKAO = "kakao";

    /* [DefaultOAuth2User 객체]
    * 소셜 로그인 API의 사용자 정보 제공 URI로 요청을 보내 사용자 정보를 얻은 후
    * 이를 통해 DefaultOAuth2User 객체 생성 후 반환
    * */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService.loadUser() 실행");

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = getSocialType(registrationId);
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // socialType에 따라 유저 정보를 통해 OAuthAttributes 객체 생성
        OAuthAttributes extractAttributes = OAuthAttributes.of(socialType, userNameAttributeName, attributes);

        User createdUser = getUser(extractAttributes, socialType);

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(createdUser.getRole().getKey())),
                attributes,
                extractAttributes.getNameAttributeKey(),
                createdUser.getEmail(),
                createdUser.getRole()
        );
    }

    private SocialType getSocialType(String registrationId) {
        if (NAVER.equals(registrationId)) return SocialType.NAVER;
        if (KAKAO.equals(registrationId)) return SocialType.KAKAO;
        return SocialType.GOOGLE;
    }

    private User getUser(OAuthAttributes attributes, SocialType socialType) {
        User findUser = userRepository.findBySocialTypeAndSocialId(socialType, attributes.getOAuth2UserInfo().getId()).orElse(null);
        if (findUser == null) return saveUser(attributes, socialType);
        return findUser;
    }

    private User saveUser(OAuthAttributes attributes, SocialType socialType) {
        User createdUser = attributes.toEntity(socialType, attributes.getOAuth2UserInfo());
        return userRepository.save(createdUser);
    }
}
