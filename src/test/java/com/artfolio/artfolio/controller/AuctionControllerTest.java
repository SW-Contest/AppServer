package com.artfolio.artfolio.controller;

import com.artfolio.artfolio.config.SecurityConfig;
import com.artfolio.artfolio.exception.AuctionNotFoundException;
import com.artfolio.artfolio.service.AuctionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@WebMvcTest(AuctionController.class)
class AuctionControllerTest {
    @Autowired MockMvc mvc;
    @MockBean AuctionService auctionService;

    @DisplayName("존재하지 않는 경매번호를 넘기면 예외를 발생시킨다")
    @Test
    void givenNonExistAuctionNumber_whenFindingAuctionDetailInfo_thenThrowException() throws Exception {
        // given
        Long nonExistsAuctionId = 100000L;
        given(auctionService.getDetailPageInfo(nonExistsAuctionId))
                .willThrow(AuctionNotFoundException.class);

        // when & then
        mvc.perform(
                get("/auction/" + nonExistsAuctionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("code").value("AUCTION_NOT_FOUND"))
                .andExpect(jsonPath("message").value("해당 경매를 찾을 수 없습니다."))
                .andExpect(jsonPath("status").value(400))
                .andDo(print()
        );

        then(auctionService).should().getDetailPageInfo(nonExistsAuctionId);
    }

    @DisplayName("존재하는 경매번호를 넘기면 경매 페이지에 대한 정보를 응답한다")
    @Test
    void givenExistAuctionId_whenFindingAuctionDetailInfo_thenReturnDetailInfos() throws Exception {
        // given
        Long auctionId = 1L;
        given(auctionService.getDetailPageInfo(auctionId)).willReturn(any());

        // when & then
        mvc.perform(
                get("/auction/" + auctionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andDo(print());

        then(auctionService).should().getDetailPageInfo(auctionId);
    }
}