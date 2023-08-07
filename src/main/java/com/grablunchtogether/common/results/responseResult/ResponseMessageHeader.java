package com.grablunchtogether.common.results.responseResult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseMessageHeader {
    private boolean result;
    private String resultCode;
    private String message;
    private int status;
}
