package com.antbot.mvp.service;

import com.antbot.mvp.domain.MarketPrice;
import com.antbot.mvp.dto.BithumbTickerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class BithumbService {
    private final WebClient bithumbWebClient;

    public MarketPrice getCurrentPrice(String market) {
        log.info("현재가 조회 시작 - 마켓: {}", market);

        return bithumbWebClient.get()
                .uri("/public/ticker/{market}", market)
                .retrieve()
                .bodyToMono(BithumbTickerResponse.class)
                .map(response -> convertToMarketPrice(response, market))
                .block();
    }

    private MarketPrice convertToMarketPrice(BithumbTickerResponse response, String market) {
        BithumbTickerResponse.TickerData data = response.getData();
        return MarketPrice.builder()
                .market(market)
                .currentPrice(Double.parseDouble(data.getClosing_price()))
                .openingPrice(Double.parseDouble(data.getOpening_price()))
                .highPrice(Double.parseDouble(data.getMax_price()))
                .lowPrice(Double.parseDouble(data.getMin_price()))
                .volume(Double.parseDouble(data.getUnits_traded()))
                .changeAmount(Double.parseDouble(data.getFluctate_24H()))
                .changeRate(Double.parseDouble(data.getFluctate_rate_24H()))
                .timestamp(LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(Long.parseLong(data.getDate())),
                        ZoneId.systemDefault()))
                .build();
    }
}