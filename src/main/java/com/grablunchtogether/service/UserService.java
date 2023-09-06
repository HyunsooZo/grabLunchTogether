package com.grablunchtogether.service;

import com.grablunchtogether.config.JwtTokenProvider;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.OtpDto;
import com.grablunchtogether.dto.geocode.GeocodeDto;
import com.grablunchtogether.dto.naverSms.NaverSmsDto;
import com.grablunchtogether.dto.token.TokenDto;
import com.grablunchtogether.dto.user.UserDistanceDto;
import com.grablunchtogether.dto.user.UserDto;
import com.grablunchtogether.enums.UserRole;
import com.grablunchtogether.enums.UserStatus;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.exception.ErrorCode;
import com.grablunchtogether.repository.UserOtpRedisRepository;
import com.grablunchtogether.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.grablunchtogether.enums.UserStatus.*;
import static com.grablunchtogether.enums.UserStatus.NOT_VERIFIED;
import static com.grablunchtogether.exception.ErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserOtpRedisRepository userOtpRedisRepository;

    public NaverSmsDto.SmsContent userSignUp(UserDto.SignUpRequest signUpRequest,
                                             GeocodeDto userCoordinate) throws Exception {

        //기존회원 조회
        userRepository.findByUserEmail(signUpRequest.getUserEmail())
                .ifPresent(user -> {
                    throw new CustomException(USER_REVIEW_ALREADY_EXISTS);
                });

        //비밀번호 암호화
        String encryptedPassword =
                passwordEncoder.encode(signUpRequest.getUserPassword());

        //입력된 휴대폰 번호에 특수문자 또는 공백이 포함되어있을 수 있으므로 숫자제외한 문자 삭제
        String userPhoneNumber =
                signUpRequest.getUserPhoneNumber().replaceAll("\\D", "");

        //회원정보 저장
        userRepository.save(User.builder()
                .userEmail(signUpRequest.getUserEmail())
                .userName(signUpRequest.getUserName())
                .userPassword(encryptedPassword)
                .userPhoneNumber(userPhoneNumber)
                .userRate(0.0)
                .company(signUpRequest.getCompany())
                .latitude(userCoordinate.getLatitude())
                .longitude(userCoordinate.getLongitude())
                .userRole(UserRole.ROLE_USER)
                .userStatus(NOT_VERIFIED)
                .build());

        // OTP 생성 및 SMS 메세지 내용 작성
        String otp = String.valueOf(
                ThreadLocalRandom
                        .current()
                        .nextInt(100000, 1000000)
        );

        String SMS_MESSAGE = "[GrabLunchTogether] 인증번호 : %s\n인증번호를 입력해주세요.";

        userOtpRedisRepository.save(otp, signUpRequest.getUserEmail());

        return NaverSmsDto.SmsContent.builder()
                .to(userPhoneNumber)
                .content(String.format(SMS_MESSAGE, otp))
                .build();
    }

    //유저 로그인
    @Transactional(readOnly = true)
    public TokenDto.Dto login(UserDto.LoginRequest loginRequest) {

        String email = loginRequest.getUserEmail();
        String password = loginRequest.getUserPassword();

        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new CustomException(USER_SIGN_IN_FAIL));

        if (!passwordEncoder.matches(password, user.getUserPassword())) {
            throw new CustomException(USER_SIGN_IN_FAIL);
        }

        String accessToken = jwtTokenProvider.issuingAccessToken(TokenDto.TokenIssuanceDto.from(user));
        String refreshToken = null;

        return TokenDto.Dto.from(user, accessToken, refreshToken);
    }

    @Transactional
    public void editUserInformation(Long userId,
                                    UserDto.InfoEditRequest infoEditRequest,
                                    GeocodeDto coordinate) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        String existingPassword = infoEditRequest.getUserPassword();

        if (!passwordEncoder.matches(existingPassword, user.getUserPassword())) {
            throw new CustomException(USER_SIGN_IN_FAIL);
        }

        //입력된 휴대폰 번호에 특수문자 또는 공백이 포함되어있을 수 있으므로 숫자제외한 문자 삭제
        String userPhoneNumber =
                infoEditRequest.getUserPhoneNumber().replaceAll("\\D", "");

        user.update(userPhoneNumber, infoEditRequest.getCompany(),
                coordinate.getLatitude(), coordinate.getLongitude());

        userRepository.save(user);
    }

    @Transactional
    public void changeUserPassword(Long userId,
                                   UserDto.PasswordChangeRequest passwordChangeRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        String existingPassword = passwordChangeRequest.getUserExistingPassword();

        if (!passwordEncoder.matches(existingPassword, user.getUserPassword())) {
            throw new CustomException(USER_SIGN_IN_FAIL);
        }

        //새로 입력된 비밀번호 암호화
        String encryptedPassword =
                passwordEncoder.encode(passwordChangeRequest.getUserNewPassword());

        user.setPassword(encryptedPassword);

        userRepository.save(user);
    }

    //설정한 거리 이내 회원 목록 가져오기
    @Transactional(readOnly = true)
    public List<UserDistanceDto.Dto> findUserAround(double latitude, double longitude, double kilometer) {
        List<Object[]> users =
                userRepository.getUserListByDistance(latitude, longitude, kilometer);

        return users.stream()
                .map(UserDistanceDto.Dto::of)
                .collect(Collectors.toList());
    }

    public UserDto.Dto getUserById(Long userId) {
        return UserDto.Dto.of(
                userRepository.findById(userId)
                        .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND)));
    }

    public void verifyOtp(OtpDto.Request otpDtoRequest) {

        String savedEmail = userOtpRedisRepository.check(otpDtoRequest.getOtp());

        if (savedEmail == null || !savedEmail.equals(otpDtoRequest.getEmail())) {
            throw new CustomException(ErrorCode.OTP_IS_INVALID);
        }

        User user = userRepository.findByUserEmail(otpDtoRequest.getEmail())
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        user.setStatus(VERIFIED);

        userRepository.save(user);
    }
}
