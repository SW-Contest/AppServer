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

    private static final String QUESTION = "당신은 그림의 태그들을 보고 그림에 대하여 다른 사람에게 설명하는 역할을 맡았습니다. " +
            "다음 태그들을 보고 그림에 대하여 실제로 보고 있는 것처럼 확신을 가지고 설명해주세요. " +
            "태그를 명시하면서 설명하기보다는 전체적인 느낌으로 설명해주세요. " +
            "답변은 '이 작품은'으로 시작하며 500자 정도로 설명해주세요.\n" ;

    protected String createDesc(Long artPieceId, List<Label> labels) {
        Optional<AIInfo> op = aiRedisRepository.findById(artPieceId);

        if (op.isEmpty()) {
            ChatGptDto.Message message = new ChatGptDto.Message("user", QUESTION + labels.toString());

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
