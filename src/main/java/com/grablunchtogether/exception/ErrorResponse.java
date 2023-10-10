package com.grablunchtogether.exception;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ErrorResponse {
    private ErrorCode errorCode;
    private String message;
}
