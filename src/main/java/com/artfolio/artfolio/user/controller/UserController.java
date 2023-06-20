package com.artfolio.artfolio.user.controller;

import com.artfolio.artfolio.user.dto.UserSignUpDto;
import com.artfolio.artfolio.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/sign-up")
    public Long signUp(@RequestBody UserSignUpDto userSignUpDto) throws Exception {
        userService.signUp(userSignUpDto);
        return 1L;
    }

    @GetMapping("/jwt-test")
    public Long jwtTest() {
        return 1L;
    }
}