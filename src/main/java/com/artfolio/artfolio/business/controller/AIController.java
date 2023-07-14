package com.artfolio.artfolio.business.controller;

import com.amazonaws.services.rekognition.model.Label;
import com.artfolio.artfolio.business.dto.ChatGptDto;
import com.artfolio.artfolio.business.service.ChatGptService;
import com.artfolio.artfolio.business.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/ai")
@RestController
public class AIController {
    private final ChatGptService chatGptService;
    private final ImageService imageService;

    @PostMapping("/question")
    public ResponseEntity<ChatGptDto.Res> sendQuestion(@RequestBody ChatGptDto.QuestionReq req) {
        return ResponseEntity.ok(chatGptService.ask(req));
    }

    @PostMapping("/test2")
    public List<Label> test2(@RequestParam("file") MultipartFile file) {
        return imageService.analyzeLocalImage(file);
    }
}
