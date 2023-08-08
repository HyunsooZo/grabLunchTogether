package com.grablunchtogether.service.externalApi.naverSms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grablunchtogether.common.exception.ContentNotFoundException;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.naverSms.MessageContentInput;
import com.grablunchtogether.dto.naverSms.SMSApiRequest;
import com.grablunchtogether.dto.naverSms.SMSApiResponse;
import com.grablunchtogether.dto.naverSms.SMSInput;
import com.grablunchtogether.dto.plan.PlanCreationInput;
import com.grablunchtogether.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SMSApiService {
    @Value("${naver.sms.ServiceId}")
    private String serviceId;
    @Value("${naver.sms.accessKey}")
    private String accessKey;
    @Value("${naver.sms.secretKey}")
    private String secretKey;
    @Value("${naver.sms.senderPhone}")
    private String senderPhone;
    @Value("${naver.sms.headerTime}")
    private String headerTime;
    @Value("${naver.sms.headerKey}")
    private String headerKey;
    @Value("${naver.sms.headerSign}")
    private String headerSign;


    private final UserRepository userRepository;

    //SMS 전송
    @Transactional
    public void sendSmsToAccepter(Long requesterId,
                                  Long accepterId,
                                  PlanCreationInput planCreationInput) {

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ContentNotFoundException("존재하지 않는 회원입니다."));
        User accepter = userRepository.findById(accepterId)
                .orElseThrow(() -> new ContentNotFoundException("존재하지 않는 회원입니다."));

        MessageContentInput messageContentInput = MessageContentInput.builder()
                .requesterName(requester.getUserName())
                .requesterCompany(requester.getCompany())
                .planRestaurant(planCreationInput.getPlanRestaurant())
                .planMenu(planCreationInput.getPlanMenu())
                .planTime(planCreationInput.getPlanTime())
                .build();

        try {
            sendSMS(SMSInput.builder()
                    .to(accepter.getUserPhoneNumber())
                    .content(messageContentInput.getMessageContent())
                    .build());
        } catch (Exception e) {
            log.error("SMS 전송 중 오류가 발생했습니다.", e);
        }
    }

    //sendSmsToAccepter 내부에서 호출하는 실제로 SMS보내는 메서드
    private ServiceResult sendSMS(SMSInput smsInput) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException, URISyntaxException {
        Long time = System.currentTimeMillis();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(headerTime, time.toString());
        headers.set(headerKey, accessKey);
        headers.set(headerSign, makeSignature(time));

        List<SMSInput> messages = new ArrayList<>();
        messages.add(smsInput);

        SMSApiRequest request = SMSApiRequest.builder()
                .type("SMS")
                .contentType("COMM")
                .countryCode("82")
                .from(senderPhone)
                .content(smsInput.getContent())
                .messages(messages)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(request);
        HttpEntity<String> httpBody = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        SMSApiResponse result = restTemplate.postForObject(
                new URI("https://sens.apigw.ntruss.com/sms/v2/services/" + serviceId + "/messages"),
                httpBody,
                SMSApiResponse.class
        );

        log.info(String.valueOf(result));
        return ServiceResult.success("문자전송완료", result);
    }

    //네이버에서 제공하는 자바 시그니처 생성 메서드
    public String makeSignature(Long time) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = "/sms/v2/services/" + this.serviceId + "/messages";
        String timestamp = time.toString();
        String accessKey = this.accessKey;
        String secretKey = this.secretKey;

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);

        return encodeBase64String;
    }
}
