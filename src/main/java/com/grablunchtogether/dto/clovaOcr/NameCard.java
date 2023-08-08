package com.grablunchtogether.dto.clovaOcr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NameCard {
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
