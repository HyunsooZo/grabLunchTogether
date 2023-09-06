package com.grablunchtogether.dto.user;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class OtpDto {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("Otp검증 요청")
    public static class Request {
      private String otp;
      private String email;
    }
}
