package com.artfolio.artfolio.business.controller;

import com.artfolio.artfolio.business.domain.OrderType;
import com.artfolio.artfolio.business.domain.SearchType;
import com.artfolio.artfolio.business.dto.AuctionDto;
import com.artfolio.artfolio.business.dto.CreateAuction;
import com.artfolio.artfolio.business.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** 실시간 경매 정보 처리를 위한 컨트롤러 (redis 연동) */
@RequestMapping("/rt_auction")
@RequiredArgsConstructor
@RestController
public class RealTimeAuctionController {
    private final AuctionService auctionService;

    /* 경매 생성 메서드 */
    @PostMapping("/create")
    public ResponseEntity<CreateAuction.Res> createAuction(@RequestBody CreateAuction.Req req) {
        CreateAuction.Res res = auctionService.createAuction(req);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    /* 단일 경매 정보를 불러오는 메서드 */
    @GetMapping("/{id}")
    public ResponseEntity<AuctionDto.DetailInfoRes> getAuction(@PathVariable("id") String auctionKey) {
        AuctionDto.DetailInfoRes res = auctionService.getAuction(auctionKey);
        return ResponseEntity.ok(res);
    }

    /* 진행중인 경매 리스트를 페이징 처리 후 내보내는 메서드 */
    @GetMapping("/list")
    public ResponseEntity<AuctionDto.PreviewInfoRes> getAuctionList(
            @RequestParam(value = "searchType", required = false) SearchType searchType,
            @RequestParam(value = "orderType", required = false) OrderType orderType,
            Pageable pageable
    ) {
        return ResponseEntity.ok(auctionService.getAuctionList(searchType, orderType, pageable));
    }

    /* 경매 삭제 메서드 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteAuction(
            @PathVariable("id") String auctionKey,
            @RequestParam("artistId") Long artistId
    ) {
        Long res = auctionService.deleteAuction(auctionKey, artistId);
        return ResponseEntity.ok(res);
    }

    /* 경매 종료 메서드 */
    /*
    @PostMapping("/finish")
    public ResponseEntity<Long> finishAuction(
            @RequestParam("auctionId") String auctionKey,
            @RequestParam("isSold") Boolean isSold
    ) {
        Long result = auctionService.finishAuction(auctionKey, isSold);
        return ResponseEntity.ok(result);
    }
     */

    /* 경매 낙찰 메서드 */
    /*
    @PostMapping("/bid")
    public ResponseEntity<Long> finishAuctionWithBidder(
            @RequestParam("auctionId") String auctionKey,
            @RequestParam("bidderId") Long bidderId,
            @RequestParam("finalPrice") Long finalPrice
    ) {
        Long result = auctionService.finishAuctionWithBidder(auctionKey, bidderId, finalPrice);
        return ResponseEntity.ok(result);
    }

     */

    /* 경매 좋아요 +-1 메서드 */
    @PostMapping("/like")
    public ResponseEntity<Integer> updateLike(
            @RequestParam("auctionId") String auctionKey,
            @RequestParam("userId") Long userId
    ) {
        Integer likes = auctionService.updateLike(auctionKey, userId);
        return ResponseEntity.ok(likes);
    }
}
