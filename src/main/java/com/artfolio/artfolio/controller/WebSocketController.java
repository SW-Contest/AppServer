package com.artfolio.artfolio.controller;

import com.artfolio.artfolio.service.RealTimeAuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
public class WebSocketController {
    private final SimpMessageSendingOperations simp;
    private final RealTimeAuctionService realTimeAuctionService;

    /* 가격 실시간 갱신 */
    // 구독 경로 : /sub/channel/{auctionId}
    // 발행 경로 : /pub/price
    @MessageMapping("/price")
    public void updateAuctionPrice(Map<String, Long> map) {
        Long auctionKey = map.get("auctionId");
        Long bidderId = map.get("bidderId");
        Long price = map.get("price");
        Long result = realTimeAuctionService.updatePrice(auctionKey, bidderId, price);

        if (result == 1L) simp.convertAndSend("/sub/channel/" + auctionKey, price);
        else simp.convertAndSend("/sub/channel/" + auctionKey, -1);
    }

    /* 경매 좋아요 +-1 메서드 */
    @MessageMapping("/like")
    public void updateLike(Map<String, Long> map) {
        Long auctionKey = map.get("auctionId");
        Long memberId = map.get("memberId");
        Long result = realTimeAuctionService.updateLike(auctionKey, memberId);

        simp.convertAndSend("/sub/channel/" + auctionKey, result);
    }
}
