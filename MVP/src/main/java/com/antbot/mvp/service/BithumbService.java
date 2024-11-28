package com.antbot.mvp.service;

import com.antbot.mvp.domain.CandleStick;
import com.antbot.mvp.dto.BithumbCandleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BithumbService {

    private final WebClient bithumbWebClient;

    /**
     * 특정 마켓의 캔들스틱 데이터를 조회합니다.
     * @param market 마켓 심볼 (예: BTC_KRW)
     * @param interval 봉 간격 (분 단위)
     * @return 캔들스틱 리스트
     */
    public List<CandleStick> getCandleSticks(String market, int interval) {
        log.info("캔들스틱 데이터 조회 시작 - 마켓: {}, 간격: {}분", market, interval);

        return bithumbWebClient.get()
                .uri("/public/candlestick/{market}/{interval}m", market, interval)
                .retrieve()
                .bodyToMono(BithumbCandleResponse.class)
                .map(response -> {
                    log.info("빗썸 API 응답 상태: {}", response.getStatus());
                    return response.getData().stream()
                            .map(this::convertToCandleStick)
                            .collect(Collectors.toList());
                })
                .block();
    }

    /**
     * API 응답 데이터를 CandleStick 객체로 변환합니다.
     */
    private CandleStick convertToCandleStick(List<String> data) {
        return CandleStick.builder()
                .timestamp(LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(Long.parseLong(data.get(BithumbCandleResponse.TIMESTAMP))),
                        ZoneId.systemDefault()))
                .openPrice(Double.parseDouble(data.get(BithumbCandleResponse.OPEN_PRICE)))
                .closePrice(Double.parseDouble(data.get(BithumbCandleResponse.CLOSE_PRICE)))
                .highPrice(Double.parseDouble(data.get(BithumbCandleResponse.HIGH_PRICE)))
                .lowPrice(Double.parseDouble(data.get(BithumbCandleResponse.LOW_PRICE)))
                .volume(Double.parseDouble(data.get(BithumbCandleResponse.VOLUME)))
                .build();
    }
}