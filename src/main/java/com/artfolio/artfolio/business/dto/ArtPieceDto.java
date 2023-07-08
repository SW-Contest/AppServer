package com.artfolio.artfolio.business.dto;

import lombok.*;

public class ArtPieceDto {
    @Getter @Setter @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreationReq {
        private String title;
        private String content;
        private Long artistId;
    }

    @Getter @Setter @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeletionReq {
        private Long artistId;
        private Long artPieceId;
    }

    @Getter @Setter @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateTitleReq {
        private Long artistId;
        private Long artPieceId;
        private String title;
    }

    @Getter @Setter @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateContentReq {
        private Long artistId;
        private Long artPieceId;
        private String content;
    }

    @Getter @Setter @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateLikeReq {
        private Long artistId;
        private Long artPieceId;
    }
}
