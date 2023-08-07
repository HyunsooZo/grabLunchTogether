package com.grablunchtogether.dto.user;

import com.grablunchtogether.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String userEmail;
    private String userName;
    private String userPhoneNumber;
    private double userRate;
    private String company;
    private double latitude;
    private double longitude;

    public static UserDto of(User user) {
        return UserDto.builder()
                .id(user.getId())
                .userEmail(user.getUserEmail())
                .userName(user.getUserName())
                .userPhoneNumber(user.getUserPhoneNumber())
                .userRate(user.getUserRate())
                .company(user.getCompany())
                .latitude(user.getLatitude())
                .longitude(user.getLongitude())
                .build();
    }
}