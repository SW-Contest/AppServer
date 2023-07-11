package com.artfolio.artfolio.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class UserSignUpDto {
    private String email;
    private String password;
    private String nickname;
    private String profilePhoto;
    private Role role;
    private SocialType socialType;
    private String socialId;
    private String content;
}
