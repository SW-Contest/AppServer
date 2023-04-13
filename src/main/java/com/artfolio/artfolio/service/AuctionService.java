package com.artfolio.artfolio.service;

import com.artfolio.artfolio.domain.Auction;
import com.artfolio.artfolio.dto.DetailPageInfoRes;
import com.artfolio.artfolio.exception.AuctionAlreadyFinishedException;
import com.artfolio.artfolio.exception.AuctionNotFoundException;
import com.artfolio.artfolio.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class AuctionService {
    private final AuctionRepository auctionRepository;

    @Transactional(readOnly = true)
    public DetailPageInfoRes getDetailPageInfo(Long auctionId) {
        Auction auction = auctionRepository.findByAuctionIdWithFetch(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(auctionId));

        /* 이미 낙찰된 경매건인지 검사 */
        if (auction.getIsSold()) {
            throw new AuctionAlreadyFinishedException(auctionId);
        }

        return DetailPageInfoRes.of(auction);
    }
}
