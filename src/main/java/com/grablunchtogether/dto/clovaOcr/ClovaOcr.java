package com.grablunchtogether.dto.clovaOcr;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ClovaOcr {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("명함 필드")
    public static class NameCard {
        @JsonProperty("name")
        private String name;

        @JsonProperty("company")
        private String company;

        @JsonProperty("address")
        private String address;

        @JsonProperty("streetNumber")
        private String streetNumber;

        @JsonProperty("mobile")
        private String mobile;

        @JsonProperty("email")
        private String email;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("네이버 OCR API Dto")
    public static class OcrApiDto {
        private String name;
        private String company;
        private String address;
        private String streetNumber;
        private String mobile;
        private String email;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("OCR 요소")
    public static class OcrImageElements {
        private String format;
        private String name;
        private String data;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("OCR 요청")
    public static class OcrRequest {
        private List<OcrImageElements> images;
        private String lang;
        private String requestId;
        private String resultType;
        private Long timestamp;
        private String version;
    }
}
