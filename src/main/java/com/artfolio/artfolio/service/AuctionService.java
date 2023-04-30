package com.artfolio.artfolio.service;

import com.artfolio.artfolio.domain.ArtPiece;
import com.artfolio.artfolio.domain.Auction;
import com.artfolio.artfolio.domain.Member;
import com.artfolio.artfolio.dto.DetailPageInfoRes;
import com.artfolio.artfolio.dto.PageInfoRes;
import com.artfolio.artfolio.exception.AuctionAlreadyFinishedException;
import com.artfolio.artfolio.exception.AuctionNotFoundException;
import com.artfolio.artfolio.exception.DeleteAuthorityException;
import com.artfolio.artfolio.exception.MemberNotFoundException;
import com.artfolio.artfolio.repository.AuctionRepository;
import com.artfolio.artfolio.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;

@Transactional
@RequiredArgsConstructor
@Service
public class AuctionService {
    private final AuctionRepository auctionRepository;
    private final MemberRepository memberRepository;

    /* Todo: Auction 테이블 변경 필요 */
    @Transactional(readOnly = true)
    public Page<PageInfoRes> getPageInfo(Pageable pageable) {
        // 1. 전체 옥션 리스트를 가져와서 artPiece id 별로 그룹을 짓는다
        // 2. 그룹내에서 isSold가 true인 로우가 있으면 이미 낙찰된 경매이므로 해당 그룹 전체를 필터링한다
        Map<ArtPiece, List<Auction>> map = auctionRepository.findAll(pageable)
                .stream()
                .collect(groupingBy(Auction::getArtPiece));

        Set<ArtPiece> keySet = map.keySet();
        List<PageInfoRes> result = new ArrayList<>();

        for (ArtPiece artPiece : keySet) {
            List<Auction> auctions = map.get(artPiece);
            boolean isSold = false;

            System.out.println("---------------------------------");
            System.out.println("artPieceId : " + artPiece.getId());

            for (Auction auction : auctions) {
                System.out.println(" ---> auctionId : " + auction.getId());

                if (auction.getIsSold() || auction.getBidder() != null) {
                    System.out.println("---> * 해당 auctionId는 이미 낙찰된 경매건입니다." + auction.getId());
                    isSold = true;
                    break;
                }
            }

            System.out.println("-----------------------------------");

            // 3. 리스트의 제일 마지막 경매만 결과에 넣는다. (이전 로우는 유찰된 기록임)
            if (!isSold) {
                result.add(PageInfoRes.of(auctions.get(auctions.size() - 1)));
            }
        }

        return new PageImpl<>(result);
    }

    @Transactional(readOnly = true)
    public DetailPageInfoRes getDetailPageInfo(Long auctionId) {
        Auction auction = auctionRepository.findAuctionById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(auctionId));

        /* 이미 낙찰된 경매건인지 검사 */
        if (auction.getIsSold()) {
            throw new AuctionAlreadyFinishedException(auctionId);
        }

        return DetailPageInfoRes.of(auction);
    }

    public Long deleteAuction(Long auctionId, Long memberId) {
        // 1. memberId로 해당 경매가 이 멤버가 생성한 것인지 검증한다
        // 2. 맞으면 삭제, 다르면 거부
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        boolean isRight = false;

        /* Todo: member, auction 간 다대다 매핑 후 리팩터링 */
        for (ArtPiece piece : member.getArtPieces()) {
            for (Auction auction : piece.getAuctions()) {
                if (Objects.equals(auction.getId(), auctionId)) {
                    isRight = true;
                    break;
                }
            }

            if (isRight) break;
        }

        if (!isRight) {
            throw new DeleteAuthorityException(memberId);
        }

        auctionRepository.deleteById(auctionId);
        return 1L;
    }
}
