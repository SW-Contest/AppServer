package com.artfolio.artfolio.user.controller;

import com.artfolio.artfolio.user.dto.UserSignUpDto;
import com.artfolio.artfolio.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /* 추가 정보를 입력받을 경우 사용 */
    @PostMapping("/sign-up")
    public ResponseEntity<Long> signUp(@RequestBody UserSignUpDto userSignUpDto) throws Exception {
        return ResponseEntity.ok(userService.signUp(userSignUpDto));
    }
}