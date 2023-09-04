package com.grablunchtogether.service.user;

import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.configuration.springSecurity.JwtTokenProvider;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.geocode.GeocodeDto;
import com.grablunchtogether.dto.token.TokenDto;
import com.grablunchtogether.dto.user.*;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.repository.UserRepository;
import com.grablunchtogether.utility.PasswordUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.grablunchtogether.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public ServiceResult userSignUp(UserSignUpInput userSignUpInput,
                                    GeocodeDto userCoordinate) {

        //기존회원 조회
        userRepository.findByUserEmail(userSignUpInput.getUserEmail())
                .ifPresent(user -> {
                    throw new CustomException(USER_REVIEW_ALREADY_EXISTS);
                });

        //비밀번호 암호화
        String encryptedPassword =
                PasswordUtility.getEncryptPassword(userSignUpInput.getUserPassword());

        //입력된 휴대폰 번호에 특수문자 또는 공백이 포함되어있을 수 있으므로 숫자제외한 문자 삭제
        String userPhoneNumber =
                userSignUpInput.getUserPhoneNumber().replaceAll("\\D", "");

        //회원정보 저장
        userRepository.save(User.builder()
                .userEmail(userSignUpInput.getUserEmail())
                .userName(userSignUpInput.getUserName())
                .userPassword(encryptedPassword)
                .userPhoneNumber(userPhoneNumber)
                .userRate(0.0)
                .company(userSignUpInput.getCompany())
                .latitude(userCoordinate.getLatitude())
                .longitude(userCoordinate.getLongitude())
                .build());

        return ServiceResult.success("회원가입 완료");
    }

    //유저 로그인
    @Transactional(readOnly = true)
    public ServiceResult login(UserLoginInput userLoginInput) {

        String email = userLoginInput.getUserEmail();
        String password = userLoginInput.getUserPassword();

        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new CustomException(USER_SIGN_IN_FAIL));

        if (!PasswordUtility.isPasswordMatch(password, user.getUserPassword())) {
            throw new CustomException(USER_SIGN_IN_FAIL);
        }

        return ServiceResult.success(jwtTokenProvider.issuingToken(
                TokenDto.builder()
                        .claim(user.getId())
                        .subject(user.getUserName())
                        .issuer(user.getUserEmail())
                        .build()));
    }

    @Transactional
    public ServiceResult editUserInformation(Long userId,
                                             UserInformationEditInput userInformationEditInput,
                                             GeocodeDto coordinate) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        String existingPassword = userInformationEditInput.getUserPassword();

        if (!PasswordUtility.isPasswordMatch(existingPassword, user.getUserPassword())) {
            throw new CustomException(USER_SIGN_IN_FAIL);
        }

        //입력된 휴대폰 번호에 특수문자 또는 공백이 포함되어있을 수 있으므로 숫자제외한 문자 삭제
        String userPhoneNumber =
                userInformationEditInput.getUserPhoneNumber().replaceAll("\\D", "");

        user.update(userPhoneNumber, userInformationEditInput.getCompany(),
                coordinate.getLatitude(), coordinate.getLongitude());

        userRepository.save(user);

        return ServiceResult.success("고객정보 수정 완료");
    }

    @Transactional
    public ServiceResult changeUserPassword(Long userId,
                                            UserChangePasswordInput userChangePasswordInput) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        String existingPassword = userChangePasswordInput.getUserExistingPassword();

        if (!PasswordUtility.isPasswordMatch(existingPassword, user.getUserPassword())) {
            throw new CustomException(USER_SIGN_IN_FAIL);
        }

        //새로 입력된 비밀번호 암호화
        String encryptedPassword =
                PasswordUtility.getEncryptPassword(userChangePasswordInput.getUserNewPassword());

        user.changePassword(encryptedPassword);

        userRepository.save(user);

        return ServiceResult.success("고객 정보 수정 완료");
    }

    @Transactional(readOnly = true)
    public UserDto tokenValidation(String token) {
        try {
            // issuer 가져오기
            String email = jwtTokenProvider.getIssuer(token);

            // 추출한 issuer 로 사용자 조회
            User user = userRepository.findByUserEmail(email)
                    .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

            return UserDto.of(user);

        } catch (Exception e) {
            // 토큰 검증 또는 사용자 조회 중 예외 발생 시
            throw new CustomException(TOKEN_IS_INVALID);
        }
    }

    //설정한 거리 이내 회원 목록 가져오기
    @Transactional(readOnly = true)
    public ServiceResult findUserAround(double latitude, double longitude, double kilometer) {
        List<Object[]> userList =
                userRepository.getUserListByDistance(latitude, longitude, kilometer);

        if (userList == null || userList.isEmpty()) {
            throw new CustomException(CONTENT_NOT_FOUND);
        }

        List<UserDistanceDto> userListResponses = new ArrayList<>();

        userList.forEach(objects -> {
            userListResponses.add(UserDistanceDto.of(objects));
        });

        return ServiceResult.success("주변회원 불러오기 성공", userListResponses);
    }
}
