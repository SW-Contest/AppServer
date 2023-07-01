package com.artfolio.artfolio.user.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class ChatGptDto {

    @Getter @Setter @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Req implements Serializable {
        private String model;
        private String prompt;
        private Integer maxTokens;
        private Double temperature;
        private Double topP;
    }

    @Builder
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Res implements Serializable {
        private String id;
        private String object;
        private LocalDateTime createdAt;
        private String model;
        private List<Choice> choices;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter
    public static class QuestionReq implements Serializable {
        private String question;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    private static class Choice implements Serializable {
        private String text;
        private Integer index;
        private String finishReason;
    }
}
