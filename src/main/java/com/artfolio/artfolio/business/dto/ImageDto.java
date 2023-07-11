package com.artfolio.artfolio.business.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ImageDto {
    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeleteReq {
        private Long artistId;
        private Long artPieceId;
        private String fileName;
    }
}
