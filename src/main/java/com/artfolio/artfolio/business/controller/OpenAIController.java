package com.artfolio.artfolio.business.controller;

import com.amazonaws.services.rekognition.model.Label;
import com.artfolio.artfolio.business.dto.ChatGptDto;
import com.artfolio.artfolio.business.service.ChatGptService;
import com.artfolio.artfolio.business.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/chat-gpt")
@RestController
public class OpenAIController {
    private final ChatGptService chatGptService;
    private final ImageService imageService;

    @PostMapping("/question")
    public ResponseEntity<ChatGptDto.Res> sendQuestion(@RequestBody ChatGptDto.QuestionReq req) {
        return ResponseEntity.ok(chatGptService.ask(req));
    }

    @GetMapping("/test")
    public List<Label> test() {
        return imageService.analyzeImage();
    }
}
