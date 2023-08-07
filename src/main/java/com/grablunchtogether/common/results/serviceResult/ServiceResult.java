package com.grablunchtogether.common.results.serviceResult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceResult {
    private boolean result;
    private String message;
    private Object object;

    public static ServiceResult success(String message) {
        return ServiceResult.builder()
                .message(message)
                .result(true)
                .object(null)
                .build();
    }

    public static ServiceResult success(String message, Object object) {
        return ServiceResult.builder()
                .message(message)
                .result(true)
                .object(object)
                .build();
    }

    public static ServiceResult fail(String message) {
        return ServiceResult.builder()
                .message(message)
                .result(false)
                .object(null)
                .build();
    }
}
