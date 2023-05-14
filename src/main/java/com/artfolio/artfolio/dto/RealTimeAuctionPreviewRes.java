package com.artfolio.artfolio.dto;

import org.springframework.data.domain.Slice;

import java.util.List;

public record RealTimeAuctionPreviewRes(
        Boolean hasNext,
        Boolean isLast,
        Integer pageSize,
        Integer pageNumber,
        Integer dataSize,
        List<PreviewInfo> data
) {
    public static RealTimeAuctionPreviewRes of(Slice<RealTimeAuctionInfo> infos, List<PreviewInfo> data) {
        int size = data.size();

        return new RealTimeAuctionPreviewRes(
                infos.hasNext(),
                infos.isLast(),
                infos.getSize(),
                infos.getNumber(),
                size,
                data
        );
    }

    public record PreviewInfo(
            MemberInfo artistInfo,
            String auctionId,
            Long like,
            Long currentPrice,
            String auctionTitle,
            String artPieceTitle,
            String thumbnailPath
    ) {
        public static PreviewInfo of(MemberInfo artistInfo, RealTimeAuctionInfo info, String thumbnailPath) {
            return new PreviewInfo(
                    artistInfo,
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
