package com.portfolio.dbmetadatagenerator.common.exception;

public class ConnectionNotFoundException extends RuntimeException {
    public ConnectionNotFoundException(Long id) {
        super("연결 정보를 찾을 수 없습니다. id=" + id);
    }
}
