package com.antbot.mvp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BithumbCandleResponse {

    @JsonProperty("market")
    private String market;

    @JsonProperty("timestamp")
    private Long timestamp;

    @JsonProperty("opening_price")
    private Double openPrice;

    @JsonProperty("closing_price")
    private Double closePrice;

    @JsonProperty("high_price")
    private Double highPrice;

    @JsonProperty("low_price")
    private Double lowPrice;

    @JsonProperty("units_traded")
    private Double volume;

    @JsonProperty("acc_trade_value")
    private Double amount;

    @JsonProperty("prev_closing_price")
    private Double prevClosingPrice;

    @JsonProperty("units_traded_24H")
    private Double volume24H;

    @JsonProperty("acc_trade_value_24H")
    private Double amount24H;

    @JsonProperty("fluctate_24H")
    private Double priceChange24H;

    @JsonProperty("fluctate_rate_24H")
    private Double priceChangePercent24H;
}