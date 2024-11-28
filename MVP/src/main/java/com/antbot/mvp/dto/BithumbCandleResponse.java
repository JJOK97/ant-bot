package com.antbot.mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BithumbCandleResponse {
    private String status;
    private List<List<String>> data;  // 배열 형태의 캔들스틱 데이터

    // 데이터 배열의 각 인덱스가 의미하는 바를 명시
    public static final int TIMESTAMP = 0;    // 시간
    public static final int OPEN_PRICE = 1;   // 시가
    public static final int CLOSE_PRICE = 2;  // 종가
    public static final int HIGH_PRICE = 3;   // 고가
    public static final int LOW_PRICE = 4;    // 저가
    public static final int VOLUME = 5;       // 거래량
}