package com.artfolio.artfolio.app.controller;

import com.artfolio.artfolio.app.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auction")
@RequiredArgsConstructor
@RestController
public class AuctionController {
    private final AuctionService auctionService;

}
