package com.grablunchtogether.dto.naverSms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SMSApiRequest {
    String type;
    String contentType;
    String countryCode;
    String from;
    String content;
    List<SMSInput> messages;
}
