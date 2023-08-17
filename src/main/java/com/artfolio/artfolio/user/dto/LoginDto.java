package com.artfolio.artfolio.user.dto;

import com.artfolio.artfolio.user.entity.User;
import lombok.*;

public class LoginDto {
    @Getter @Setter @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static abstract class TokenRes {
        private String token_type;
        private String access_token;
        private String refresh_token;
        private Integer expires_in;
    }

    @Getter @Setter @ToString @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NaverTokenRes extends TokenRes {
        private String error;
        private String error_description;
    }

    @Getter @Setter @ToString @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KakaoTokenRes extends TokenRes {
        private Integer refresh_token_expires_in;
    }

    public interface UserInfo {
        User toEntity(SocialType socialType, String refreshToken);
    }

    @Getter @Setter @ToString @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KakaoUserInfo implements UserInfo {
        private Long id;
        private KakaoAccount kakao_account;

        @Override
        public User toEntity(SocialType socialType, String refreshToken) {
            return User.builder()
                    .email(kakao_account.getEmail())
                    .profilePhoto(kakao_account.profile.profile_image_url)
                    .content("..")
                    .socialType(socialType)
                    .nickname(kakao_account.profile.nickname)
                    .role(Role.USER)
                    .refreshToken(refreshToken)
                    .build();
        }
    }

    @Getter @Setter @ToString @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KakaoAccount {
        private String email;
        private Profile profile;
    }

    @Getter @Setter @ToString @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Profile {
        private String nickname;
        private String thumbnail_image_url;
        private String profile_image_url;
    }

    @Getter @Setter @ToString @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NaverUserInfo implements UserInfo {
        private String resultCode;
        private String message;
        private Response response;

        @Override
        public User toEntity(SocialType socialType, String refreshToken) {
            return User.builder()
                    .email(response.getEmail())
                    .profilePhoto(response.profile_image)
                    .content("..")
                    .socialType(socialType)
                    .nickname(response.nickname)
                    .role(Role.USER)
                    .refreshToken(refreshToken)
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
