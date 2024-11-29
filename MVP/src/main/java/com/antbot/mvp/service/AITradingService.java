package com.antbot.mvp.service;

import com.antbot.mvp.domain.MarketPrice;
import com.antbot.mvp.domain.TradingDecision;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AITradingService {

    public TradingDecision analyzeTradingDecision(MarketPrice marketPrice) {
        log.info("AI 트레이딩 분석 시작 - 마켓: {}", marketPrice.getMarket());

        // 여기에 Claude API 호출 로직 구현
        // 1. MarketPrice 데이터를 기반으로 프롬프트 생성
        // 2. Claude API 호출
        // 3. 응답을 TradingDecision으로 변환

        return null;  // 임시 리턴
    }
}