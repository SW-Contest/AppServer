package com.artfolio.artfolio.controller;

import com.artfolio.artfolio.dto.DetailPageInfoRes;
import com.artfolio.artfolio.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auction")
@RequiredArgsConstructor
@RestController
public class AuctionController {
    private final AuctionService auctionService;

    /* 옥션 상세 정보를 응답해주는 메서드 */
    @GetMapping("/{id}")
    public ResponseEntity<DetailPageInfoRes> getDetailPageInfo(@PathVariable("id") Long auctionId) {
        return ResponseEntity.ok(auctionService.getDetailPageInfo(auctionId));
    }
}
