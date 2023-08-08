package com.grablunchtogether.dto.clovaOcr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OcrImageElements {
    private String format;
    private String name;
    private String data;
}
