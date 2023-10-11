package com.grablunchtogether.dto;

import com.grablunchtogether.domain.BookmarkSpot;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class BookmarkSpotDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("BookMarkSpot 요청")
    public static class Request {
        private String restaurant;
        private String menu;
        private String address;
        private String operationHour;
        private String rate;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("BookMarkSpot Dto")
    public static class Dto {
        private Long id;
        private String restaurant;
        private String menu;
        private String address;
        private String operationHour;
        private String rate;
        private LocalDateTime registeredAt;
        private LocalDateTime updatedAt;

        public static Dto of(BookmarkSpot bookmarkSpot) {
            return Dto.builder()
                    .id(bookmarkSpot.getId())
                    .restaurant(bookmarkSpot.getRestaurant())
                    .menu(bookmarkSpot.getMenu())
                    .address(bookmarkSpot.getAddress())
                    .operationHour(bookmarkSpot.getOperationHour())
                    .rate(bookmarkSpot.getRate())
                    .registeredAt(bookmarkSpot.getRegisteredAt())
                    .updatedAt(bookmarkSpot.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("BookMarkSpot 응답")
    public static class Response {
        private List<Dto> bookMarkSpots;

        public static Response of(List<Dto> bookmarkSpotDtos) {
            return Response.builder()
                    .bookMarkSpots(bookmarkSpotDtos)
                    .build();
        }
    }
}
