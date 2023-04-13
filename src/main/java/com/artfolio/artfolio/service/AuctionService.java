package com.artfolio.artfolio.service;

import com.artfolio.artfolio.domain.Auction;
import com.artfolio.artfolio.dto.DetailPageInfoRes;
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
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(auctionId));

        return DetailPageInfoRes.of(auction);
    }
}
