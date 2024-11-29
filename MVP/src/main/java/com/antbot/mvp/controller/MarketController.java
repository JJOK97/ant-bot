package com.antbot.mvp.controller;

import com.antbot.mvp.domain.MarketPrice;
import com.antbot.mvp.service.BithumbService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/market")
@RequiredArgsConstructor
public class MarketController {

    private final BithumbService bithumbService;

    @GetMapping("/price/{market}")
    public MarketPrice getCurrentPrice(@PathVariable String market) {
        return bithumbService.getCurrentPrice(market);
    }
}