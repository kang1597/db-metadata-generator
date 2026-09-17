package com.portfolio.dbmetadatagenerator.common.exception;

public class DbConnectionFailedException extends RuntimeException {
    public DbConnectionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
