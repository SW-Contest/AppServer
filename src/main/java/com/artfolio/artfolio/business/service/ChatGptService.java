package com.artfolio.artfolio.business.service;

import com.amazonaws.services.rekognition.model.Label;
import com.artfolio.artfolio.business.domain.AIInfo;
import com.artfolio.artfolio.business.repository.AIRedisRepository;
import com.artfolio.artfolio.global.config.ChatGptConfig;
import com.artfolio.artfolio.business.dto.ChatGptDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatGptService {
    @Value("${chatgpt.api-key}")
    private String OPENAI_KEY;
    private final AIRedisRepository aiRedisRepository;

    protected String createDesc(Long artPieceId, List<Label> labels) {
        Optional<AIInfo> op = aiRedisRepository.findById(artPieceId);

        if (op.isEmpty()) {
            String question = "이건 어떠한 그림을 태그로 분석한 결과입니다.\n" +
                    "이 태그를 통하여 이미지를 적절한 글로써 500자 내외로 나타내주세요";
            ChatGptDto.Message message = new ChatGptDto.Message("user", question + labels.toString());

            ChatGptDto.Req req = ChatGptDto.Req.builder()
                    .model(ChatGptConfig.MODEL)
                    .messages(List.of(message))
                    .build();

            ChatGptDto.Res res = WebClient.create()
                    .post()
                    .uri(ChatGptConfig.URL)
                    .headers(h -> {
                        h.add(ChatGptConfig.AUTHORIZATION, ChatGptConfig.BEARER + OPENAI_KEY);
                        h.add("Content-Type", "application/json");
                    })
                    .bodyValue(req)
                    .retrieve()
                    .bodyToMono(ChatGptDto.Res.class)
                    .block();

           return getGptContent(res.getChoices());
        }

        return op.get().getContent();
    }

    private String getGptContent(List<ChatGptDto.Choice> choices) {
        StringBuffer sb = new StringBuffer();
        choices.forEach(choice -> sb.append(choice.getMessage().getContent()));
        return sb.toString();
    }
}
