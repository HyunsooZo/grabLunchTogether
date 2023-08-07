package com.grablunchtogether.common.results.responseResult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseMessage {
    private ResponseMessageHeader header;
    private Object body;

    public static ResponseMessage fail(String message) {
        return ResponseMessage.builder()
                .header(ResponseMessageHeader.builder()
                        .result(false)
                        .resultCode("")
                        .message(message)
                        .status(HttpStatus.BAD_REQUEST.value())
                        .build())
                .body(null)
                .build();
    }

    public static ResponseMessage success(String message, Object object) {
        return ResponseMessage.builder()
                .header(ResponseMessageHeader.builder()
                        .result(true)
                        .resultCode("")
                        .message(message)
                        .status(HttpStatus.OK.value())
                        .build())
                .body(object)
                .build();
    }
}
