package com.grablunchtogether.dto.bookmarkSpot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookmarkSpotInput {
    private String restaurant;
    private String menu;
    private String address;
    private String operationHour;
    private String rate;
}
