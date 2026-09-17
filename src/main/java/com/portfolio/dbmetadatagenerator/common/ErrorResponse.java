package com.portfolio.dbmetadatagenerator.common;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {
    private final LocalDateTime timestamp;
    private final int status;
    private final String errorCode;
    private final String message;

    public ErrorResponse(int status, String errorCode, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }
}
