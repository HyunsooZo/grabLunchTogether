package com.grablunchtogether.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grablunchtogether.dto.ClovaOcr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import static com.grablunchtogether.dto.ClovaOcr.*;

@Service
public class OcrApiService {

    @Value("${naver.ocr.apiKey}")
    private String ocrApiKey;

    @Value("${naver.ocr.apigwUrl}")
    private String apigwUrl;

    // 명함이미지 경로를 받아 OCR 호출 진행
    @Transactional
    public OcrApiDto getUserInfoFromNameCard(String imageName) {

        String encodedData = imageToBase64(imageName);
        String imageFormat = findImageFormat(imageName);

        OcrRequest ocrRequest = createOcrinput(encodedData, imageFormat);
        String body = convertToJsonString(ocrRequest);
        HttpEntity<String> httpBody = createHttpEntity(body);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        String response = null;

        try {
            response = restTemplate.postForObject(new URI(apigwUrl), httpBody, String.class);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode imageArray = jsonNode.path("images");
            JsonNode firstImage = imageArray.get(0);
            JsonNode nameCardObject = firstImage.path("nameCard");

            String name = extractName(nameCardObject);
            String company = extractCompany(nameCardObject);
            String address = extractAddress(nameCardObject);
            String streetNumber = extractStreetNumber(nameCardObject);
            String mobile = extractMobile(nameCardObject);
            String email = extractEmail(nameCardObject);

            // Create OcrApiDto object using extracted values
            OcrApiDto ocrApiDto = OcrApiDto.builder()
                    .email(email)
                    .name(name)
                    .company(company)
                    .mobile(mobile)
                    .address(address)
                    .streetNumber(streetNumber)
                    .build();

            return ocrApiDto;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new OcrApiDto();
    }

    // api 호출을 위한 HttpEntity생성
    private HttpEntity<String> createHttpEntity(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-OCR-SECRET", ocrApiKey);
        return new HttpEntity<>(body, headers);
    }

    // ocr api호출을 위한 Json 포맷 입력 만들기
    private OcrRequest createOcrinput(String encodedData, String imageFormat) {
        OcrImageElements imageElements =
                OcrImageElements.builder()
                        .name("ocrImage")
                        .data(encodedData)
                        .format(imageFormat)
                        .build();
        List<ClovaOcr.OcrImageElements> ocrImageElementsList =
                Collections.singletonList(imageElements);

        return ClovaOcr.OcrRequest.builder()
                .images(ocrImageElementsList)
                .lang("ko")
                .requestId("string")
                .resultType("string")
                .version("V2")
                .timestamp(System.currentTimeMillis())
                .build();
    }

    // 포맷을 Json String으로 변환
    private String convertToJsonString(OcrRequest ocrRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        String result = null;
        try {
            result = objectMapper.writeValueAsString(ocrRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    // 이미지 경로를 통해 이미지 포맷 가져오기
    private String findImageFormat(String url) {
        String[] imageSplit = url.split("\\.");
        return imageSplit[imageSplit.length - 1];
    }

    // 이미지 정보 base64 암호화
    private String imageToBase64(String imagePathInput) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.exchange(imagePathInput, HttpMethod.GET, null, byte[].class);
        byte[] imageBytes = response.getBody();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    // JsonNode에서 이름추출
    private String extractName(JsonNode nameCardObject) {
        JsonNode nameArray = nameCardObject.path("result").path("name");
        return nameArray.get(0).get("text").asText().replace(" ", "");
    }

    // JsonNode에서 회사명추출
    private String extractCompany(JsonNode nameCardObject) {
        JsonNode companyArray = nameCardObject.path("result").path("company");
        return companyArray.get(0).get("text").asText();
    }

    // JsonNode에서 주소추출
    private String extractAddress(JsonNode nameCardObject) {
        JsonNode addressArray = nameCardObject.path("result").path("address");
        String[] addressArr = addressArray.get(0).get("text").asText().split(" ");
        return addressArr[addressArr.length - 2];
    }

    // JsonNode에서 주소번호 추출
    private String extractStreetNumber(JsonNode nameCardObject) {
        JsonNode resultNode = nameCardObject.path("result");
        JsonNode addressArray = resultNode.path("address");
        String[] addressArr = addressArray.get(0).get("text").asText().split(" ");
        return addressArr[addressArr.length - 1].replaceAll("\\D", "");
    }

    // JsonNode에서 휴대폰번호 추출
    private String extractMobile(JsonNode nameCardObject) {
        JsonNode mobileArray = nameCardObject.path("result").path("mobile");
        return mobileArray.get(0).get("text").asText().replaceAll("\\D", "");
    }

    // JsonNode에서 이메일 추출
    private String extractEmail(JsonNode nameCardObject) {
        JsonNode emailArray = nameCardObject.path("result").path("email");
        return emailArray.get(0).get("text").asText();
    }
}
