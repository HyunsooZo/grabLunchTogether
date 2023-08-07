package com.grablunchtogether.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDistanceDto {
    private String userEmail;
    private String userName;
    private double userRate;
    private String company;
    private String distance;

    public static UserDistanceDto of(Object[] object) {
        return UserDistanceDto.builder()
                .userName(object[0].toString())
                .userEmail(object[1].toString())
                .userRate(Double.parseDouble(object[2].toString()))
                .company(object[3].toString())
                .distance(String.format("%.1f", (Double) object[4]))
                .build();
    }
}