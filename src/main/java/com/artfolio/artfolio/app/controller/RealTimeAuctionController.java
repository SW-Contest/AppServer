package com.artfolio.artfolio.app.controller;

import com.artfolio.artfolio.app.dto.AuctionDetails;
import com.artfolio.artfolio.app.dto.CreateAuction;
import com.artfolio.artfolio.app.dto.RealTimeAuctionPreview;
import com.artfolio.artfolio.app.service.RealTimeAuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** 실시간 경매 정보 처리를 위한 컨트롤러 (redis 연동) */
@RequestMapping("/rt_auction")
@RequiredArgsConstructor
@RestController
public class RealTimeAuctionController {
    private final RealTimeAuctionService redisService;

    /* 경매 생성 메서드 */
    @PostMapping("/create")
    public ResponseEntity<CreateAuction.Res> createAuction(@RequestBody CreateAuction.Req req) {
        CreateAuction.Res res = redisService.createAuction(req);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    /* 단일 경매 정보를 불러오는 메서드 */
    @GetMapping("/{id}")
    public ResponseEntity<AuctionDetails.Res> getAuction(@PathVariable("id") String auctionKey) {
        AuctionDetails.Res res = redisService.getAuction(auctionKey);
        return ResponseEntity.ok(res);
    }

    /* 진행중인 경매 리스트를 페이징 처리 후 내보내는 메서드 */
    @GetMapping("/list")
    public RealTimeAuctionPreview.Res getAuctionList(
            @PageableDefault(sort = "createdAt", size = 10, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return redisService.getAuctionList(pageable);
    }

    /* 경매 삭제 메서드 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteAuction(
            @PathVariable("id") String auctionKey,
            @RequestParam("artistId") Long artistId
    ) {
        Long res = redisService.deleteAuction(auctionKey, artistId);
        return ResponseEntity.ok(res);
    }


    /* 경매 종료 메서드 */
    @PostMapping("/finish")
    public ResponseEntity<Long> finishAuction(
            @RequestParam("auctionId") String auctionKey,
            @RequestParam("isSold") Boolean isSold
    ) {
        Long result = redisService.finishAuction(auctionKey, isSold);
        return ResponseEntity.ok(result);
    }

    /* 경매 낙찰 메서드 */
    /*@PostMapping("/bid")
    public ResponseEntity<Long> finishAuctionWithBidder(
            @RequestParam("auctionId") String auctionKey,
            @RequestParam("bidderId") Long bidderId,
            @RequestParam("finalPrice") Long finalPrice
    ) {
        Long result = redisService.finishAuctionWithBidder(auctionKey, bidderId, finalPrice);
        return ResponseEntity.ok(result);
    }
*/
    /* 경매 좋아요 +-1 메서드 */
    @PostMapping("/like")
    public ResponseEntity<Integer> updateLike(
            @RequestParam("auctionId") String auctionId,
            @RequestParam("memberId") Long memberId
    ) {
        Integer likes = redisService.updateLike(auctionId, memberId);
        return ResponseEntity.ok(likes);
    }
}
