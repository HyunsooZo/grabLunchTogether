package com.grablunchtogether.dto;


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
    @ApiModel("Otp재발급 요청")
    public static class OtpRequest {
      private String phone;
    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("Otp검증 요청")
    public static class VerificationRequest {
      private String otp;
      private String phone;
    }
}
