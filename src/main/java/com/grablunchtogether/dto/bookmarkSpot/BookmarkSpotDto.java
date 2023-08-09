package com.grablunchtogether.dto.bookmarkSpot;

import com.grablunchtogether.domain.BookmarkSpot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookmarkSpotDto {
    private Long id;
    private String restaurant;
    private String menu;
    private String address;
    private String operationHour;
    private String rate;
    private LocalDateTime registeredAt;

    public static BookmarkSpotDto of(BookmarkSpot bookmarkSpot) {
        return BookmarkSpotDto.builder()
                .id(bookmarkSpot.getId())
                .restaurant(bookmarkSpot.getRestaurant())
                .menu(bookmarkSpot.getMenu())
                .address(bookmarkSpot.getAddress())
                .operationHour(bookmarkSpot.getOperationHour())
                .rate(bookmarkSpot.getRate())
                .registeredAt(bookmarkSpot.getRegisteredAt())
                .build();
    }
}
