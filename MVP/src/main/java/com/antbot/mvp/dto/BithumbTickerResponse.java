package com.antbot.mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BithumbTickerResponse {
    private String status;
    private TickerData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TickerData {
        private String opening_price;        // 시가
        private String closing_price;        // 종가(현재가)
        private String min_price;            // 저가
        private String max_price;            // 고가
        private String units_traded;         // 거래량
        private String acc_trade_value;      // 거래금액
        private String fluctate_24H;         // 24시간 변동금액
        private String fluctate_rate_24H;    // 24시간 변동률
        private String date;                 // 타임스탬프
    }
}