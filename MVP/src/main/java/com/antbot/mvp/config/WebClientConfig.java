package com.antbot.mvp.config;

import com.antbot.mvp.exception.BithumbApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.concurrent.TimeUnit;

/**
 * Bithumb API 호출을 위한 WebClient 설정
 */
@Slf4j
@Configuration
public class WebClientConfig {

    @Value("${bithumb.api.url}")
    private String bithumbApiUrl;

    @Value("${bithumb.api.api-key}")
    private String apiKey;

    @Value("${bithumb.api.secret-key}")
    private String secretKey;

    @Value("${bithumb.api.connect-timeout:5000}")
    private Integer connectTimeout;

    @Value("${bithumb.api.read-timeout:10000}")
    private Integer readTimeout;

    /**
     * Bithumb API 호출을 위한 WebClient Bean 생성
     * @return WebClient 인스턴스
     */
    @Bean
    public WebClient bithumbWebClient() {
        return WebClient.builder()
                .baseUrl(bithumbApiUrl)
                .filter(logRequest())
                .filter(logResponse())
                .filter(addApiKeyHeader())
                .filter(handleErrors())  // 에러 처리 필터 추가
                .defaultHeaders(this::setDefaultHeaders)
                .codecs(this::configureCodecs)
                .clientConnector(generateHttpConnector())  // Connection Pool 설정 추가
                .build();
    }

    /**
     * Connection Pool 설정을 위한 HTTP Connector 생성
     */
    private ReactorClientHttpConnector generateHttpConnector() {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("bithumb-connection-pool")
                .maxConnections(50)  // 최대 커넥션 수
                .maxIdleTime(Duration.ofMinutes(5))  // 커넥션 유휴 시간
                .maxLifeTime(Duration.ofMinutes(30))  // 커넥션 최대 수명
                .pendingAcquireTimeout(Duration.ofSeconds(60))  // 커넥션 획득 타임아웃
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .responseTimeout(Duration.ofMillis(readTimeout))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(connectTimeout, TimeUnit.MILLISECONDS)));

        return new ReactorClientHttpConnector(httpClient);
    }

    /**
     * Codec 설정
     */
    private void configureCodecs(ClientCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024); // 2MB
        configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(
                objectMapper(), MediaType.APPLICATION_JSON));
        configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(
                objectMapper(), MediaType.APPLICATION_JSON));
    }

    /**
     * ObjectMapper 설정
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 에러 처리 필터
     */
    private ExchangeFilterFunction handleErrors() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().is5xxServerError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new BithumbApiException(
                                clientResponse.statusCode(), errorBody)));
            }
            return Mono.just(clientResponse);
        });
    }

    /**
     * 기본 헤더 설정
     * @param headers HTTP 헤더
     */
    private void setDefaultHeaders(HttpHeaders headers) {
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");
    }

    /**
     * API 인증을 위한 헤더 추가
     * @return ExchangeFilterFunction API 키와 서명을 추가하는 필터
     */
    private ExchangeFilterFunction addApiKeyHeader() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            // private API 호출시에만 인증 헤더 추가
            if (clientRequest.url().getPath().startsWith("/private")) {
                long nonce = System.currentTimeMillis();

                return Mono.just(ClientRequest.from(clientRequest)
                        .header("Api-Key", apiKey)
                        .header("Api-Sign", generateApiSign(clientRequest))
                        .header("Api-Nonce", String.valueOf(nonce))
                        .build());
            }
            return Mono.just(clientRequest);
        });
    }

    /**
     * API 요청 서명 생성
     * @param request ClientRequest 객체
     * @return 생성된 API 서명
     */
    private String generateApiSign(ClientRequest request) {
        try {
            String endpoint = request.url().getPath();
            String queryString = request.url().getQuery();
            long nonce = System.currentTimeMillis();

            // 빗썸 API 서명 규칙에 따른 문자열 생성
            String strForSign = endpoint +
                    (queryString != null ? "?" + queryString : "") +
                    String.valueOf(nonce);

            // HMAC-SHA512 해시 생성
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    secretKey.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA512"
            );

            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(secretKeySpec);

            byte[] macData = mac.doFinal(strForSign.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(macData);

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("API 서명 생성 중 에러 발생: ", e);
            throw new RuntimeException("API 서명 생성 실패", e);
        }
    }

    /**
     * 요청 로깅을 위한 필터
     * @return ExchangeFilterFunction 요청 로깅 필터
     */
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            if (log.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder("Request: \n");
                sb.append("URL: ").append(clientRequest.url()).append("\n");
                sb.append("Method: ").append(clientRequest.method()).append("\n");
                clientRequest.headers().forEach((name, values) ->
                        values.forEach(value -> sb.append(name).append(": ").append(value).append("\n")));
                log.debug(sb.toString());
            }
            return Mono.just(clientRequest);
        });
    }

    /**
     * 응답 로깅을 위한 필터
     * @return ExchangeFilterFunction 응답 로깅 필터
     */
    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (log.isDebugEnabled()) {
                log.debug("Response Status: {}", clientResponse.statusCode());
            }
            return Mono.just(clientResponse);
        });
    }
}