package com.artfolio.artfolio.business.controller;

import com.artfolio.artfolio.user.dto.ChatGptDto;
import com.artfolio.artfolio.user.service.ChatGptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/chat-gpt")
@RestController
public class OpenAIController {
    private final ChatGptService chatGptService;

    @PostMapping("/question")
    public ResponseEntity<ChatGptDto.Res> sendQuestion(@RequestBody ChatGptDto.QuestionReq req) {
        return ResponseEntity.ok(chatGptService.ask(req));
    }
}
