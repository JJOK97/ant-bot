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
public class CandleStick {
    private String market;           // 마켓 코드 (예: BTC_KRW)
    private LocalDateTime timestamp; // 캔들 시각
    private Double openPrice;        // 시가
    private Double closePrice;       // 종가
    private Double highPrice;        // 고가
    private Double lowPrice;         // 저가
    private Double volume;           // 거래량
    private Double amount;           // 거래대금

    // AI 분석용 추가 데이터
    private Double btcDominance;     // 해당 시점의 BTC 도미넌스
    private Double marketCapRank;    // 시가총액 순위
    private Double volumeRank;       // 거래량 순위
    private Double priceChangePercent; // 가격 변동률

    // 호가창 정보
    private Double askVolume;        // 매도 호가 총량
    private Double bidVolume;        // 매수 호가 총량
    private Double volumeImbalance;  // 매수-매도 불균형

    private Double marketSentiment;    // 시장 심리도 (-1 ~ 1)
    private Double correlationWithBTC; // BTC와의 상관관계
    private Integer largeTransactions; // 대량 거래 건수
    private Double whaleActivity;      // 고래 지갑 활동 지표
    private Double fundingRate;        // 선물 자금조달률
    private Double openInterest;       // 미체결약정
    private Boolean isKimpChecked;     // 김프 여부
    private Double kimpRate;           // 김프 프리미엄
}