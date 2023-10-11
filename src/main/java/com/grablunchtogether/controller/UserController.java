package com.grablunchtogether.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.grablunchtogether.config.JwtTokenProvider;
import com.grablunchtogether.dto.ClovaOcr;
import com.grablunchtogether.dto.GeocodeDto;
import com.grablunchtogether.dto.ImageDto;
import com.grablunchtogether.dto.NaverSmsDto;
import com.grablunchtogether.dto.TokenDto;
import com.grablunchtogether.dto.OtpDto;
import com.grablunchtogether.dto.UserDto;
import com.grablunchtogether.enums.ImageDirectory;
import com.grablunchtogether.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
    private final RefreshTokenService refreshTokenService;
    private final S3BucketService s3BucketService;

    @PostMapping("/signup")
    @ApiOperation(value = "사용자 회원가입", notes = "입력된 정보로 회원가입을 진행합니다.")
    public ResponseEntity<Void> userSignUp(
            @Valid @RequestBody UserDto.SignUpRequest signUpRequest) {

        //고객 좌표 가져오는 외부 api 호출
        GeocodeDto userCoordinate = geocodeApiService.getCoordinate(
                signUpRequest.getStreetName(), signUpRequest.getStreetNumber());

        userService.userSignUp(signUpRequest, userCoordinate);

        return ResponseEntity.status(OK).build();
    }

    @PostMapping("/login")
    @ApiOperation(value = "사용자 로그인", notes = "입력된 사용자 ID/PW로 로그인을 진행합니다.")
    public ResponseEntity<TokenDto.Response> login(@Valid @RequestBody UserDto.LoginRequest loginRequest) {

        TokenDto.Dto tokenDto = userService.login(loginRequest);

        return ResponseEntity.status(OK).body(TokenDto.Response.from(tokenDto));
    }

    @PostMapping("/logout")
    @ApiOperation(value = "사용자 로그아웃", notes = "사용자 로그아웃을 진행합니다.")
    public ResponseEntity<Void> logout(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        String userEmail = jwtTokenProvider.getEmailFromToken(token);

        refreshTokenService.deleteRefreshToken(userEmail);

        return ResponseEntity.status(NO_CONTENT).build();
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
                .nameCardUrl(ocrSignUpRequest.getImageName())
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

    @PostMapping("/otp/request")
    @ApiOperation(value = "SMS OTP 요청/재요청", notes = "회원가입 이전 휴대번호 본인확인 겸 OTP 요청/재요청")
    public ResponseEntity<Void> otpGeneration(
            @RequestBody OtpDto.OtpRequest otpDtoRequest) throws UnsupportedEncodingException,
            NoSuchAlgorithmException, URISyntaxException, InvalidKeyException, JsonProcessingException {

        NaverSmsDto.SmsContent smsContent = userService.otpResend(otpDtoRequest);

        smsApiService.sendSMS(smsContent);

        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PostMapping("/otp/verification")
    @ApiOperation(value = "SMS OTP 인증", notes = "문자메세지로 전송된 OTP를 인증합니다.")
    public ResponseEntity<Void> otpVerification(
            @RequestBody OtpDto.VerificationRequest otpDtoRequest) {

        userService.verifyOtp(otpDtoRequest);

        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PostMapping("/image")
    @ApiOperation(value = "이미지 호스팅", notes = "사진을 업로드하고 호스팅된 주소를 받습니다." +
            "(프로필사진일 경우 ?directory=PROFILE , 명함사진일 경우 ?directory=NAMECARD")
    public ResponseEntity<ImageDto.Response> getImageUrl(
            @RequestParam("directory") ImageDirectory imageDirectory,
            @RequestBody MultipartFile multipartFile) throws IOException {

        ImageDto.Dto dto = s3BucketService.saveFile(multipartFile, imageDirectory);

        return ResponseEntity.status(OK).body(ImageDto.Response.of(dto));
    }

    @PatchMapping("/image")
    @ApiOperation(value = "프로필 이미지 수정", notes = "프로필 이미지를 수정합니다.")
    public ResponseEntity<ImageDto.Response> getImageUrl(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody ImageDto.Request imageRequest) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        String previousUrl =
                userService.updateProfilePicture(userId, imageRequest.getImageUrl());

        //기존 이미지 존재할 경우 기존 이미지는 S3버켓에서 제거
        s3BucketService.deleteFile(previousUrl);

        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PatchMapping("/withdrawal")
    @ApiOperation(value = "회원 탈퇴", notes = "회원계정을 탈퇴합니다.")
    public ResponseEntity<Void> userWithdrawal(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody UserDto.WithdrawalRequest withdrawalRequest) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        UserDto.Dto userDto = userService.getUserById(userId);

        userService.withdrawUser(userId, withdrawalRequest);

        s3BucketService.deleteFile(userDto.getProfileUrl());
        s3BucketService.deleteFile(userDto.getNameCardUrl());

        return ResponseEntity.status(NO_CONTENT).build();
    }
}
