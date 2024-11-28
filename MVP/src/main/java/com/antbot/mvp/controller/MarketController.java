package com.antbot.mvp.controller;

import com.antbot.mvp.domain.CandleStick;
import com.antbot.mvp.service.BithumbService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/market")
@RequiredArgsConstructor
public class MarketController {

    private final BithumbService bithumbService;

    @GetMapping("/candles/{market}/{interval}")
    public List<CandleStick> getCandleSticks(
            @PathVariable String market,
            @PathVariable int interval) {
        return bithumbService.getCandleSticks(market, interval);
    }
}