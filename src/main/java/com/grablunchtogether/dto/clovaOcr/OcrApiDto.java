package com.grablunchtogether.dto.clovaOcr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OcrApiDto {
    private String name;
    private String company;
    private String address;
    private String streetNumber;
    private String mobile;
    private String email;
}
