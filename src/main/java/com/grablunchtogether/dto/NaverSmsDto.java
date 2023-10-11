package com.grablunchtogether.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class NaverSmsDto {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("NaverSms 메세지 내용")
    public static class MessageContent {
        private String requesterName;
        private String requesterCompany;
        private String planMenu;
        private String planRestaurant;
        private LocalDateTime planTime;

        public String getMessageContent() {
            String time = planTime.toString()
                    .replace("T", " ")
                    .substring(0, 16);

            return String.format("%s(%s)님의 식사요청 도착\n일시 : %s\n메뉴 : %s(%s)",
                    requesterName,
                    requesterCompany,
                    time,
                    planRestaurant, planMenu);
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("NaverSms 메세지 api 요청")
    public static class SmsApiRequest {
        String type;
        String contentType;
        String countryCode;
        String from;
        String content;
        List<SmsContent> messages;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("NaverSms 메세지 api 응답")
    public static class SmsApiResponse {
        String requestId;
        LocalDateTime requestTime;
        String statusCode;
        String statusName;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("NaverSms 대상 , 내용")
    public static class SmsContent {
        String to;
        String content;
    }
}
