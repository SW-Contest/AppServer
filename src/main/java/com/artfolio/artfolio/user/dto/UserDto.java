package com.artfolio.artfolio.user.dto;

import com.artfolio.artfolio.user.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.*;
import org.springframework.security.oauth2.core.user.OAuth2User;


public class UserDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class SignUpReq {
        private String email;
        private String password;
        private String nickname;
        private String profilePhoto;
        private Role role;
        private SocialType socialType;
        private String socialId;
        private String content;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class OAuth2LoginInfoRes {
        private Long userId;
        private String socialId;
        private String profileImage;
        private String email;
        private String name;
        private String socialType;
        private String role;

        public static OAuth2LoginInfoRes of(User user) {
            return OAuth2LoginInfoRes
                    .builder()
                    .userId(user.getId())
                    .socialId(user.getSocialId())
                    .socialType(user.getSocialType().name())
                    .email(user.getEmail())
                    .name(user.getNickname())
                    .profileImage(user.getProfilePhoto())
                    .role(user.getRole().name())
                    .build();
        }
    }
}
