package com.grablunchtogether.dto.naverSms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SMSApiResponse {
    String requestId;
    LocalDateTime requestTime;
    String statusCode;
    String statusName;
}

