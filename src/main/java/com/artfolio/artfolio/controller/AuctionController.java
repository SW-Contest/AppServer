package com.artfolio.artfolio.controller;

import com.artfolio.artfolio.dto.DetailPageInfoRes;
import com.artfolio.artfolio.dto.PageInfoRes;
import com.artfolio.artfolio.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /* 옥션 리스트를 n개씩 보내주는 메서드 */
    @GetMapping
    public Page<PageInfoRes> getPageInfo(
            @PageableDefault(size = 10, sort = { "like", "id" }, direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return auctionService.getPageInfo(pageable);
    }

    /* 옥션 삭제 메서드 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteAuction(@PathVariable("id") Long auctionId, @RequestParam("memberId") Long memberId) {
        return ResponseEntity.ok(auctionService.deleteAuction(auctionId, memberId));
    }
}
