package com.artfolio.artfolio.controller;

import com.artfolio.artfolio.dto.AuctionBidInfoRes;
import com.artfolio.artfolio.error.ErrorCode;
import com.artfolio.artfolio.error.ErrorResponse;
import com.artfolio.artfolio.exception.InvalidBidPriceException;
import com.artfolio.artfolio.service.RealTimeAuctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

import static com.artfolio.artfolio.util.ErrorBuildFactory.buildError;

@Slf4j
@RequiredArgsConstructor
@RestController
public class WebSocketController {
    private final SimpMessageSendingOperations simp;
    private final RealTimeAuctionService realTimeAuctionService;

    /* 가격 실시간 갱신 */
    // 구독 경로 : /sub/channel/{auctionId}
    // 발행 경로 : /pub/price
    @MessageMapping("/price")
    public void updateAuctionPrice(Principal principal, @Payload Map<String, String> map) {
        String auctionKey = map.get("auctionId");
        Long bidderId = Long.parseLong(map.get("bidderId"));
        Long price = Long.parseLong(map.get("price"));

        AuctionBidInfoRes auctionBidInfoRes = realTimeAuctionService.updatePrice(principal, auctionKey, bidderId, price);
        simp.convertAndSend("/topic/channel/" + auctionKey, auctionBidInfoRes);
    }


    /* 현재가보다 낮은 입찰가가 들어온 경우 예외 처리 */
    @MessageExceptionHandler(InvalidBidPriceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected void handleInvalidBidPriceException(InvalidBidPriceException e) {
        log.error("현재가보다 낮은 입찰가가 입력되었습니다.");
        log.error("current price : " + e.getCurrentPrice());
        log.error("bid price : " + e.getBidPrice());

        /* 예외시 브로드캐스팅이 아닌 입찰한 유저에게만 응답을 전송해야 한다. */
        ErrorResponse errorResponse = buildError(ErrorCode.INVALID_BID_PRICE);
        simp.convertAndSendToUser(e.getPrincipal().getName(), "/queue/errors", errorResponse);
    }
}
