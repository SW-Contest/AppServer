package com.artfolio.artfolio.business.service;

import com.artfolio.artfolio.user.entity.User;
import com.artfolio.artfolio.business.domain.redis.RealTimeAuctionInfo;
import com.artfolio.artfolio.business.repository.ArtPieceRepository;
import com.artfolio.artfolio.business.repository.AuctionRepository;
import com.artfolio.artfolio.user.repository.UserRepository;
import com.artfolio.artfolio.business.domain.ArtPiece;
import com.artfolio.artfolio.business.domain.Auction;
import com.artfolio.artfolio.global.exception.ArtPieceNotFoundException;
import com.artfolio.artfolio.global.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Transactional
@RequiredArgsConstructor
@Service
public class AuctionService {
    private final AuctionRepository auctionRepository;
    private final UserRepository memberRepository;
    private final ArtPieceRepository artPieceRepository;
}
