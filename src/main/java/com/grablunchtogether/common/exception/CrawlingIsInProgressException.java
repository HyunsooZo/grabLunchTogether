package com.grablunchtogether.common.exception;

public class CrawlingIsInProgressException extends RuntimeException {
    public CrawlingIsInProgressException(String message) {
        super(message);
    }
}
