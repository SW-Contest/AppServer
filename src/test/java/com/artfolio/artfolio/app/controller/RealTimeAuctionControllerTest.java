package com.artfolio.artfolio.app.controller;

import com.artfolio.artfolio.app.dto.AuctionDetails;
import com.artfolio.artfolio.app.dto.CreateAuction;
import com.artfolio.artfolio.app.repository.ArtPiecePhotoRepository;
import com.artfolio.artfolio.app.repository.BidRedisRepository;
import com.artfolio.artfolio.app.repository.MemberRepository;
import com.artfolio.artfolio.app.repository.RealTimeAuctionRedisRepository;
import com.artfolio.artfolio.app.service.AuctionService;
import com.artfolio.artfolio.app.service.RealTimeAuctionService;
import com.artfolio.artfolio.global.config.SecurityConfig;
import com.artfolio.artfolio.global.exception.AuctionAlreadyExistsException;
import com.artfolio.artfolio.global.exception.AuctionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@WebMvcTest(RealTimeAuctionController.class)
class RealTimeAuctionControllerTest {
    @Autowired MockMvc mvc;
    @MockBean RealTimeAuctionService sut;
    @MockBean MemberRepository memberRepository;
    @MockBean AuctionService auctionService;
    @MockBean ArtPiecePhotoRepository artPiecePhotoRepository;
    @MockBean RealTimeAuctionRedisRepository realTimeAuctionRedisRepository;
    @MockBean BidRedisRepository bidRedisRepository;
    @MockBean SimpMessageSendingOperations simp;

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("경매 생성 테스트")
    void createAuction() throws Exception {
        // Given
        Long artistId = 1L;
        Long artPieceId = 1L;
        Long auctionStartPrice = 100000L;
        String artPieceTitle = "test artpiece title";
        String auctionTitle = "test auction title";
        String auctionContent = "test auction content";

        CreateAuction.Req req = new CreateAuction.Req(artistId, artPieceId, artPieceTitle, auctionTitle, auctionContent, auctionStartPrice);

        given(sut.createAuction(eq(req)))
                .willThrow(new AuctionAlreadyExistsException(artPieceId));

        // When
        mvc.perform(post("/rt_auction/create"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // Then
        then(sut).should().createAuction(req);
    }

    @Test
    void getAuction() {
        given(sut.getAuction(anyString()))
                .willThrow(AuctionNotFoundException.class);

        then(sut).should().getAuction(anyString());
    }

    @Test
    void getAuctionList() {
    }

    @Test
    void deleteAuction() {
    }

    @Test
    void finishAuction() {
    }

    @Test
    void finishAuctionWithBidder() {
    }

    @Test
    void updateLike() {
    }
}