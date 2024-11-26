package com.antbot.mvp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class BithumbApiException extends RuntimeException {
    private final HttpStatusCode status;
    private final String errorBody;

    public BithumbApiException(HttpStatusCode status, String errorBody) {
        super(String.format("API 호출 실패 [%s]: %s", status, errorBody));
        this.status = status;
        this.errorBody = errorBody;
    }
}