package com.grablunchtogether.dto.mustEatPlace;

import com.grablunchtogether.domain.MustEatPlace;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.List;

public class MustEatPlaceDto {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("MustEatPlace Dto")
    public static class Dto {
        private Long id;
        private String restaurant;
        private String menu;
        private String address;
        private String operationHour;
        private String city;
        private String rate;

        public static Dto from(MustEatPlace mustEatPlace) {
            return Dto.builder()
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
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("MustEatPlace 응답")
    public static class Response{
        private List<Dto> mustEatPlaces;
        public static Response from(List<Dto> mustEatPlaceList){
            return Response.builder()
                    .mustEatPlaces(mustEatPlaceList)
                    .build();
        }
    }
}
