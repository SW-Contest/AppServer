package com.artfolio.artfolio.business.controller;

import com.artfolio.artfolio.business.dto.AuctionBid;
import com.artfolio.artfolio.global.error.ErrorCode;
import com.artfolio.artfolio.global.error.ErrorResponse;
import com.artfolio.artfolio.global.exception.InvalidBidPriceException;
import com.artfolio.artfolio.business.service.AuctionService;
import com.artfolio.artfolio.global.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static com.artfolio.artfolio.global.util.ErrorBuildFactory.buildError;

@Slf4j
@RequiredArgsConstructor
@RestController
public class WebSocketController {
    private final SimpMessageSendingOperations simp;
    private final AuctionService realTimeAuctionService;

    /* 가격 실시간 갱신 */
    // 구독 경로 : /sub/channel/{auctionId}
    // 발행 경로 : /pub/price
    @MessageMapping("/price")
    public void updateAuctionPrice(@AuthenticationPrincipal Principal principal, @Payload AuctionBid.Req req) {
        AuctionBid.Res res = realTimeAuctionService.updatePrice(principal, req);
        simp.convertAndSend("/topic/channel/" + req.getAuctionId(), res);
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

    @MessageExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected void handleBidderNotFoundException(UserNotFoundException e) {
        log.error("존재하지 않는 입찰자 번호입니다.");

        ErrorResponse errorResponse = buildError(ErrorCode.BIDDER_NOT_FOUND);
        simp.convertAndSendToUser(e.getPrincipal().getName(), "/queue/errors", errorResponse);
    }
}
