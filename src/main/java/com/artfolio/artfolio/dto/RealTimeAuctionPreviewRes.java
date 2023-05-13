package com.artfolio.artfolio.dto;

import java.util.List;

public record RealTimeAuctionPreviewRes(
        Boolean isLast,
        Integer pageSize,
        Integer pageNumber,
        Integer dataSize,
        List<PreviewInfo> data
) {
    public static RealTimeAuctionPreviewRes of(Integer pageSize, Integer pageNumber, List<PreviewInfo> data) {
        int size = data.size();

        return new RealTimeAuctionPreviewRes(
                size == 0,
                pageSize,
                pageNumber,
                size,
                data
        );
    }

    public record PreviewInfo(
            String auctionId,
            Long like,
            Long currentPrice,
            String auctionTitle,
            String artPieceTitle,
            String thumbnailPath
    ) {
        public static PreviewInfo of(RealTimeAuctionInfo info, String thumbnailPath) {
            return new PreviewInfo(
                    info.getId(),
                    info.getLike(),
                    info.getAuctionCurrentPrice(),
                    info.getAuctionTitle(),
                    info.getArtPieceTitle(),
                    thumbnailPath
            );
        }
    }
}
