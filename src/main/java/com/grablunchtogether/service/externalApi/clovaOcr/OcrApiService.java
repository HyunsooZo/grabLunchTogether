package com.grablunchtogether.service.externalApi.clovaOcr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grablunchtogether.dto.clovaOcr.NameCard;
import com.grablunchtogether.dto.clovaOcr.OcrApiDto;
import com.grablunchtogether.dto.clovaOcr.OcrImageElements;
import com.grablunchtogether.dto.clovaOcr.OcrInput;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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

        OcrInput ocrInput = createOcrinput(encodedData, imageFormat);
        String body = convertToJsonString(ocrInput);
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
            JsonNode resultNode = jsonNode.path("result");

            NameCard nameCard = objectMapper.treeToValue(resultNode, NameCard.class);

            String name = nameCard.getName().replace(" ", "");
            String company = nameCard.getCompany();

            JsonNode addressNode = resultNode.path("address").get(0);
            String address = addressNode.path("text").asText();
            String[] addressArr = address.split(" ");
            String streetNumber = addressArr[addressArr.length - 1].replaceAll("\\D", "");

            JsonNode mobileNode = resultNode.path("mobile").get(0);
            String mobile = mobileNode.path("text").asText().replaceAll("\\D", "");

            JsonNode emailNode = resultNode.path("email").get(0);
            String email = emailNode.path("text").asText();

            return OcrApiDto.builder()
                    .email(email)
                    .name(name)
                    .company(company)
                    .mobile(mobile)
                    .address(address)
                    .streetNumber(streetNumber)
                    .build();

        }catch (Exception e){
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
    private OcrInput createOcrinput(String encodedData, String imageFormat) {
        OcrImageElements imageElements =
                OcrImageElements.builder()
                        .name("ocrImage")
                        .data(encodedData)
                        .format(imageFormat)
                        .build();
        List<OcrImageElements> ocrImageElementsList = new ArrayList<>();
        ocrImageElementsList.add(imageElements);

        return OcrInput.builder()
                .images(ocrImageElementsList)
                .lang("ko")
                .requestId("string")
                .resultType("string")
                .version("V2")
                .timestamp(System.currentTimeMillis())
                .build();
    }

    // 포맷을 Json String으로 변환
    private String convertToJsonString(OcrInput ocrInput) {
        ObjectMapper objectMapper = new ObjectMapper();
        String result = null;
        try {
            result = objectMapper.writeValueAsString(ocrInput);
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
}
