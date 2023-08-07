package com.artfolio.artfolio.user.dto;

import com.artfolio.artfolio.user.entity.User;
import jdk.jfr.consumer.RecordedFrame;
import lombok.*;

public class LoginDto {

    @Getter @Setter @ToString @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TokenRes {
        private String access_token;
        private String refresh_token;
        private String token_type;
        private Integer expires_in;
        private String error;
        private String error_description;
    }

    @Getter @Setter @ToString @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfoRes {
        private String resultCode;
        private String message;
        private Response response;

        public User toEntity(SocialType socialType, String refreshToken) {
            return User.builder()
                    .email(response.getEmail())
                    .profilePhoto(response.profile_image)
                    .content("..")
                    .socialType(socialType)
                    .nickname(response.nickname)
                    .password("...")
                    .role(Role.USER)
                    .refreshToken(refreshToken)
                    .socialId(response.id)
                    .build();
        }

        @Getter @Setter @ToString @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Response {
            private String id;
            private String nickname;
            private String name;
            private String email;
            private String gender;
            private String age;
            private String birthday;
            private String profile_image;
            private String birthyear;
            private String mobile;
            private String accessToken;
            private String refreshToken;
        }
    }
}
