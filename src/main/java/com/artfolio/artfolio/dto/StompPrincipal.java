package com.artfolio.artfolio.dto;

import lombok.Getter;

import java.security.Principal;

/* 웹 소켓 세션 주체의 정보를 담는 클래스
* - 특정 유저 개인에게만 메세지를 전달하기 위해서 웹 소켓 세션 정보가 필요하다.
* */

@Getter
public class StompPrincipal implements Principal {
    private final String name;

    public StompPrincipal(String name) {
        this.name = name;
    }
}
