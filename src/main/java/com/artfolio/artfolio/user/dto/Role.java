package com.artfolio.artfolio.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    TEST("ROLE_TEST"),
    GUEST("ROLE_GUEST"),
    USER("ROLE_USER"),
    ARTIST("ROLE_ARTIST");

    private final String key;
}
