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
public class MessageContentInput {
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
