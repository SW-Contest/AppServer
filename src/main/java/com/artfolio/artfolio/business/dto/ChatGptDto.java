package com.artfolio.artfolio.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

public class ChatGptDto {

    @Getter @Setter @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Req implements Serializable {
        private String model;
        private List<Message> messages;
    }

    @Getter @Setter @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message implements Serializable {
        private String role;
        private String content;
    }

    @Builder
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Res implements Serializable {
        private String id;
        private String object;
        private Long created;
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
    public static class Choice implements Serializable {
        private Message message;
        private Integer index;
        @JsonProperty("finish_reason")
        private String finishReason;
    }
}
