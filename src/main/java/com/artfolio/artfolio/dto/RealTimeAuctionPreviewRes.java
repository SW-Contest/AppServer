package com.artfolio.artfolio.dto;

public record RealTimeAuctionPreviewRes(
        String auctionId,
        Long like,
        Long currentPrice,
        String auctionTitle,
        String artPieceTitle,
        String thumbnailPath
) {
    public static RealTimeAuctionPreviewRes of(RealTimeAuctionInfo info) {
        return new RealTimeAuctionPreviewRes(
                info.getId(),
                info.getLike(),
                info.getAuctionCurrentPrice(),
                info.getAuctionTitle(),
                info.getArtPieceTitle(),
                info.getPhotoPaths().get(0) == null ? "null" : info.getPhotoPaths().get(0)
        );
    }
}
