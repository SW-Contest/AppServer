package com.artfolio.artfolio.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RealTimeAuctionPreviewRes(
        Boolean isLast,
        Integer pageSize,
        Integer pageNumber,
        Integer dataSize,
        List<PreviewInfo> data
) {
    public static RealTimeAuctionPreviewRes of(Integer pageSize, Integer pageNumber, List<PreviewInfo> infos) {
        int size = infos.size();

        return new RealTimeAuctionPreviewRes(
                size == 0,
                pageSize,
                pageNumber,
                size,
                infos
        );
    }

    public record PreviewInfo(
            String auctionId,
            Long like,
            Long currentPrice,
            String auctionTitle,
            String artPieceTitle,
            LocalDateTime createdAt,
            String thumbnailPath
    ) {
        public static PreviewInfo of(RealTimeAuctionInfo info) {
            return new PreviewInfo(
                    info.getId(),
                    info.getLike(),
                    info.getAuctionCurrentPrice(),
                    info.getAuctionTitle(),
                    info.getArtPieceTitle(),
                    info.getCreatedAt(),
                    info.getPhotoPaths().size() == 0 ? "null" : info.getPhotoPaths().get(0)
            );
        }
    }
}
