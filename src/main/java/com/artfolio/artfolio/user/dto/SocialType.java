package com.artfolio.artfolio.user.dto;

import lombok.Getter;

@Getter
public enum SocialType {
    TEST("test"), NAVER("naver"), KAKAO("kakao"), GOOGLE("google");

    private final String type;

    SocialType(String type) {
        this.type = type;
    }
}
