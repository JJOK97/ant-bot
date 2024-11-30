package com.antbot.mvp.service;

import com.antbot.mvp.domain.MarketPrice;
import com.antbot.mvp.domain.TradingDecision;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AITradingService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private static final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";

    @Value("${spring.ai.claude.api-key}")
    private String apiKey;

    public TradingDecision analyzeTradingDecision(MarketPrice marketPrice) {
        log.info("AI 트레이딩 분석 시작 - 마켓: {}", marketPrice.getMarket());

        // Map.of는 불변 맵을 생성하므로 HashMap 사용
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "claude-3-sonnet-20240229");
        requestBody.put("max_tokens", 1000);
        requestBody.put("messages", List.of(
                Map.of("role", "user", "content", createPrompt(marketPrice))
        ));
        requestBody.put("system", "You are a cryptocurrency trading expert. Analyze the market data and make a buy/sell/hold decision.");

        log.debug("API 요청 본문: {}", requestBody);  // 요청 내용 로깅 추가

        return webClient.post()
                .uri(CLAUDE_API_URL)
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnError(error -> {
                    log.error("API 호출 실패: {}", error.getMessage());
                    if (error instanceof WebClientResponseException) {
                        log.error("응답 본문: {}", ((WebClientResponseException) error).getResponseBodyAsString());
                    }
                })
                .map(this::parseResponse)
                .block();
    }

    private String createPrompt(MarketPrice marketPrice) {
        return String.format("""
            다음 비트코인 시장 데이터를 분석하여 매수/매도/홀드 결정을 해주세요:
            
            현재가: %,.0f원
            시가: %,.0f원
            고가: %,.0f원
            저가: %,.0f원
            거래량: %.2f BTC
            24시간 변동금액: %,.0f원
            24시간 변동률: %.2f%%
            
            결정을 JSON 형식으로 제공해주세요:
            {
                "decision": "매수/매도/홀드",
                "reasoning": "판단 근거",
                "confidence": 신뢰도(0~1),
                "suggestedPrice": 제안가격
            }
            """,
                marketPrice.getCurrentPrice(),
                marketPrice.getOpeningPrice(),
                marketPrice.getHighPrice(),
                marketPrice.getLowPrice(),
                marketPrice.getVolume(),
                marketPrice.getChangeAmount(),
                marketPrice.getChangeRate()
        );
    }

    private TradingDecision parseResponse(JsonNode response) {
        try {
            JsonNode content = response.get("content").get(0).get("text");
            JsonNode decision = objectMapper.readTree(content.asText());

            return TradingDecision.builder()
                    .decision(decision.get("decision").asText())
                    .reasoning(decision.get("reasoning").asText())
                    .confidence(decision.get("confidence").asDouble())
                    .suggestedPrice(decision.get("suggestedPrice").asDouble())
                    .build();
        } catch (Exception e) {
            log.error("AI 응답 파싱 실패", e);
            throw new RuntimeException("AI 응답 파싱 실패", e);
        }
    }
}