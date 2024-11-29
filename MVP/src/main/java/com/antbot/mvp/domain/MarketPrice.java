package com.antbot.mvp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketPrice {
    private String market;           // 마켓 코드 (예: BTC_KRW)
    private Double currentPrice;     // 현재가 (종가)
    private Double openingPrice;     // 시가
    private Double highPrice;        // 고가
    private Double lowPrice;         // 저가
    private Double volume;           // 거래량
    private Double changeAmount;     // 변동금액
    private Double changeRate;       // 변동률
    private LocalDateTime timestamp; // 타임스탬프
}