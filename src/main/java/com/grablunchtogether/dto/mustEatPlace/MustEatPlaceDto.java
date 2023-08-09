package com.grablunchtogether.dto.mustEatPlace;

import com.grablunchtogether.domain.MustEatPlace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MustEatPlaceDto {
    private Long id;
    private String restaurant;
    private String menu;
    private String address;
    private String operationHour;
    private String city;
    private String rate;

    public static MustEatPlaceDto of(MustEatPlace mustEatPlace){
        return MustEatPlaceDto.builder()
                .id(mustEatPlace.getId())
                .restaurant(mustEatPlace.getRestaurant())
                .menu(mustEatPlace.getMenu())
                .address(mustEatPlace.getAddress())
                .operationHour(mustEatPlace.getOperationHour())
                .city(mustEatPlace.getCity())
                .rate(mustEatPlace.getRate())
                .build();
    }
}
