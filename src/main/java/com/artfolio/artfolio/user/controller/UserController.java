package com.artfolio.artfolio.user.controller;

import com.artfolio.artfolio.user.dto.UserSignUpDto;
import com.artfolio.artfolio.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /* 추가 정보를 입력받을 경우 사용 */
    @PostMapping("/sign-up")
    public Long signUp(@RequestBody UserSignUpDto userSignUpDto) throws Exception {
        userService.signUp(userSignUpDto);
        return 1L;
    }

    /* 닉네임 변경 */
    @PostMapping("/nickname")
    public Long changeNickname(@RequestBody Map<String, Object> map) {
        Object nickname = map.get("nickname");

        if (!(nickname instanceof String)) {
            return 0L;
        }

        userService.changeNickname(nickname);
        return 1L;
    }

    /* 소개글 변경 */
    @PostMapping("/content")
    public Long changeContent(@RequestBody Map<String, Object> map) {
        Object content = map.get("content");

        if (!(content instanceof String)) {
            return 0L;
        }

        userService.changeContent(content);
        return 1L;
    }
}