package com.antbot.mvp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradingDecision {
    private String market;          // 마켓 (예: BTC_KRW)
    private String decision;        // 매수/매도/홀드 결정
    private String reasoning;       // 판단 근거
    private Double confidence;      // 신뢰도 (0~1)
    private Double suggestedPrice;  // 제안 가격
}