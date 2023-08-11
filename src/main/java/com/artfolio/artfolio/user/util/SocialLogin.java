package com.artfolio.artfolio.user.util;

import com.artfolio.artfolio.user.dto.LoginDto;

public interface SocialLogin {
    LoginDto.UserInfo login();
    String getEmail();
}
