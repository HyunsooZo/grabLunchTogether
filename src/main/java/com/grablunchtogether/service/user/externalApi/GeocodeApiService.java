package com.grablunchtogether.service.user.externalApi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grablunchtogether.dto.geocode.GeocodeDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;

@Service
public class GeocodeApiService {
    @Value("${myapp.defaultX}")
    private Double defaultX;

    @Value("${myapp.defaultY}")
    private Double defaultY;

    @Value("${geocode.apiKey}")
    private String apiKey;

    @Value("${geocode.url}")
    private String urlFormat;

    //고객의 도로명 주소를 받아 좌표값을 받아내어 GeocodeDto 로 반환
    @Transactional
    public GeocodeDto getCoordinate(String streetName, String streetNumber) {

        String addressCombined = streetName + "+" + streetNumber;

        URI uri = URI.create(String.format(urlFormat, addressCombined, apiKey));
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        String apiResult = restTemplate.getForObject(uri, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;

        try {
            jsonNode = objectMapper.readTree(apiResult);
            JsonNode responseNode = jsonNode.get("response");
            JsonNode resultNode = responseNode.get("result");
            JsonNode pointNode = resultNode.get("point");

            return GeocodeDto.builder()
                    .latitude(pointNode.get("x").asDouble())
                    .longitude(pointNode.get("y").asDouble())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();

            // 예외 발생 시 또는 정확한 값을 받아오지 못했을 경우 기본값(서울역 좌표) 입력, 고객에게 수정 유도
            return GeocodeDto.builder()
                    .latitude(defaultX)
                    .longitude(defaultY)
                    .build();
        }
    }
}
