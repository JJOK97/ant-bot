package com.antbot.mvp.controller;

import com.antbot.mvp.domain.MarketPrice;
import com.antbot.mvp.domain.TradingDecision;
import com.antbot.mvp.service.AITradingService;
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
    private final AITradingService aiTradingService;

    @GetMapping("/price/{market}")
    public MarketPrice getCurrentPrice(@PathVariable String market) {
        return bithumbService.getCurrentPrice(market);
    }

    @GetMapping("/analysis/{market}")
    public TradingDecision analyzeMarket(@PathVariable String market) {
        // 현재 시장 데이터 조회
        MarketPrice marketPrice = bithumbService.getCurrentPrice(market);
        // AI 분석 요청
        return aiTradingService.analyzeTradingDecision(marketPrice);
    }
}