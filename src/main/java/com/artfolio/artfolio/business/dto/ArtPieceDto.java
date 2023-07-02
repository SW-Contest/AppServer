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
}
