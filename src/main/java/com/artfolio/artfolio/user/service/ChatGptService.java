package com.artfolio.artfolio.user.service;

import com.artfolio.artfolio.global.config.ChatGptConfig;
import com.artfolio.artfolio.user.dto.ChatGptDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatGptService {
    private static final RestTemplate restTemplate = new RestTemplate();

    private HttpEntity<ChatGptDto.Req> buildHttpEntity(ChatGptDto.Req req) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(ChatGptConfig.MEDIA_TYPE));
        headers.add(ChatGptConfig.AUTHORIZATION, ChatGptConfig.BEARER + ChatGptConfig.API_KEY);
        return new HttpEntity<>(req, headers);
    }

    private ChatGptDto.Res getResponse(HttpEntity<ChatGptDto.Req> req) {
        ResponseEntity<ChatGptDto.Res> res = restTemplate.postForEntity(
                ChatGptConfig.URL,
                req,
                ChatGptDto.Res.class
        );

        return res.getBody();
    }

    public ChatGptDto.Res ask(ChatGptDto.QuestionReq questionReq) {
        ChatGptDto.Req dto = new ChatGptDto.Req(
                ChatGptConfig.MODEL,
                questionReq.getQuestion(),
                ChatGptConfig.MAX_TOKEN,
                ChatGptConfig.Temperature,
                ChatGptConfig.TOP_P
        );

        return getResponse(buildHttpEntity(dto));
    }
}
