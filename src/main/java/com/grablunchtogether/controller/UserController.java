package com.grablunchtogether.controller;

import com.grablunchtogether.config.JwtTokenProvider;
import com.grablunchtogether.dto.OtpDto;
import com.grablunchtogether.dto.clovaOcr.ClovaOcr;
import com.grablunchtogether.dto.geocode.GeocodeDto;
import com.grablunchtogether.dto.naverSms.NaverSmsDto;
import com.grablunchtogether.dto.token.TokenDto;
import com.grablunchtogether.dto.user.UserDto;
import com.grablunchtogether.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@Api(tags = "User API", description = "사용자와 관련된 API")
@RestController
public class UserController {

    private final UserService userService;
    private final GeocodeApiService geocodeApiService;
    private final OcrApiService ocrApiService;
    private final MailSenderService mailSenderService;
    private final JwtTokenProvider jwtTokenProvider;
    private final SMSApiService smsApiService;

    @PostMapping("/signup")
    @Transactional
    @ApiOperation(value = "사용자 회원가입", notes = "입력된 정보로 회원가입을 진행합니다.")
    public ResponseEntity<Void> userSignUp(
            @Valid @RequestBody UserDto.SignUpRequest signUpRequest) throws Exception {

        //고객 좌표 가져오는 외부 api 호출
        GeocodeDto userCoordinate = geocodeApiService.getCoordinate(
                signUpRequest.getStreetName(), signUpRequest.getStreetNumber());

        NaverSmsDto.SmsContent smsContent =
                userService.userSignUp(signUpRequest, userCoordinate);

        smsApiService.sendSMS(smsContent);

        return ResponseEntity.status(OK).build();
    }

    @PostMapping("/login")
    @ApiOperation(value = "사용자 로그인", notes = "입력된 사용자 ID/PW로 로그인을 진행합니다.")
    public ResponseEntity<TokenDto.Response> login(@Valid @RequestBody UserDto.LoginRequest loginRequest) {

        TokenDto.Dto tokenDto = userService.login(loginRequest);

        return ResponseEntity.status(OK).body(TokenDto.Response.from(tokenDto));
    }

    @PatchMapping("/edit")
    @ApiOperation(value = "사용자 정보 수정", notes = "입력된 정보로 기존사용자의 정보를 수정합니다.")
    public ResponseEntity<Void> editUserInformation(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody UserDto.InfoEditRequest infoEditRequest) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        //수정된 주소의 좌표 다시 가져오기
        GeocodeDto coordinate = geocodeApiService.getCoordinate(
                infoEditRequest.getAddress(), infoEditRequest.getStreetNumber()
        );

        userService.editUserInformation(userId, infoEditRequest, coordinate);

        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PatchMapping("/password/change")
    @ApiOperation(value = "사용자 비밀번호 변경", notes = "기존 비밀번호를 입력된 비밀번호로 변경합니다.")
    public ResponseEntity<Void> changePassword(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody UserDto.PasswordChangeRequest passwordChangeRequest) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        userService.changeUserPassword(userId, passwordChangeRequest);

        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PostMapping("/signup/ocr")
    @ApiOperation(value = "명함 OCR 회원가입", notes = "명함이미지를 받아 간편회원가입을 진행합니다.")
    public ResponseEntity<Void> ocrSignUp(
            @Valid @RequestBody UserDto.OcrSignUpRequest ocrSignUpRequest) throws Exception {

        ClovaOcr.OcrApiDto ocrData = ocrApiService.getUserInfoFromNameCard(ocrSignUpRequest.getImageName());

        GeocodeDto geocodeApiResponse =
                geocodeApiService.getCoordinate(ocrData.getAddress(), ocrData.getStreetNumber());

        UserDto.SignUpRequest signUpRequest = UserDto.SignUpRequest.builder()
                .userEmail(ocrData.getEmail())
                .userName(ocrData.getName())
                .userPassword(ocrSignUpRequest.getPassword())
                .userPhoneNumber(ocrData.getMobile())
                .company(ocrData.getCompany())
                .build();

        userService.userSignUp(signUpRequest, geocodeApiResponse);

        return ResponseEntity.status(CREATED).build();
    }

    @PostMapping("/password/reset")
    @ApiOperation(value = "비밀번호 초기화", notes = "비밀번호를 초기화 하고 이메일로 전송합니다.")
    public ResponseEntity<Void> ocrSignUp(
            @RequestBody UserDto.PasswordResetRequest passwordResetRequest) {

        mailSenderService.resetPassword(passwordResetRequest);
        return ResponseEntity.status(NO_CONTENT).build();
    }
    @PostMapping("/otp/verification")
    @ApiOperation(value = "SMS OTP 인증", notes = "문자메세지로 전송된 OTP를 인증합니다.")
    public ResponseEntity<Void> otpVerification(
            @RequestBody OtpDto.Request otpDtoRequest) {

        userService.verifyOtp(otpDtoRequest);

        return ResponseEntity.status(NO_CONTENT).build();
    }
}
