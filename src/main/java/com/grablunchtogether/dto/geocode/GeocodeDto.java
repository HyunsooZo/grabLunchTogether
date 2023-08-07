package com.grablunchtogether.dto.geocode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeocodeDto {
    private Double latitude;
    private Double longitude;
}
