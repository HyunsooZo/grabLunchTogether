package com.grablunchtogether.dto.clovaOcr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OcrInput {
    private List<OcrImageElements> images;
    private String lang;
    private String requestId;
    private String resultType;
    private Long timestamp;
    private String version;
}
