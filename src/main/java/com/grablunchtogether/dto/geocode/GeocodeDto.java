package com.grablunchtogether.dto.geocode;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("Geocode Dto")
public class GeocodeDto {
    private Double latitude;
    private Double longitude;
}
