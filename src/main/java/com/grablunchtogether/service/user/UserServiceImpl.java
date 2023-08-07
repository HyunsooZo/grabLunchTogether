package com.grablunchtogether.service.user;

import com.grablunchtogether.common.exception.*;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.configuration.springSecurity.JwtTokenProvider;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.geocode.GeocodeDto;
import com.grablunchtogether.dto.token.TokenDto;
import com.grablunchtogether.dto.user.*;
import com.grablunchtogether.repository.UserRepository;
import com.grablunchtogether.utility.PasswordUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public ServiceResult userSignUp(UserSignUpInput userSignUpInput,
                                    GeocodeDto userCoordinate) {

        //기존회원 조회
        userRepository.findByUserEmail(userSignUpInput.getUserEmail())
                .ifPresent(user -> {
                    throw new UserSignUpException("이미 존재하는 회원입니다.");
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
                .registeredAt(LocalDateTime.now())
                .build());

        return ServiceResult.success("회원가입 완료");
    }

    //유저 로그인
    @Override
    @Transactional(readOnly = true)
    public ServiceResult login(UserLoginInput userLoginInput) {

        String email = userLoginInput.getUserEmail();
        String password = userLoginInput.getUserPassword();

        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new InvalidLoginException("존재하지 않는 아이디 입니다."));

        if (!PasswordUtility.isPasswordMatch(password, user.getUserPassword())) {
            throw new InvalidLoginException("비밀번호가 일치하지 않습니다.");
        }

        return ServiceResult.success(jwtTokenProvider.issuingToken(
                TokenDto.builder()
                        .claim(user.getId())
                        .subject(user.getUserName())
                        .issuer(user.getUserEmail())
                        .build()));
    }

    @Override
    @Transactional
    public ServiceResult editUserInformation(Long userId,
                                             UserInformationEditInput userInformationEditInput,
                                             GeocodeDto coordinate) {

        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserInfoNotFoundException("고객 정보를 찾을 수 없습니다. 다시 시도해 주세요.")
        );

        String existingPassword = userInformationEditInput.getUserPassword();

        if (!PasswordUtility.isPasswordMatch(existingPassword, user.getUserPassword())) {
            throw new InvalidLoginException("기존 비밀번호가 일치하지 않습니다.");
        }

        //입력된 휴대폰 번호에 특수문자 또는 공백이 포함되어있을 수 있으므로 숫자제외한 문자 삭제
        String userPhoneNumber =
                userInformationEditInput.getUserPhoneNumber().replaceAll("\\D", "");

        user.setUserPhoneNumber(userPhoneNumber);
        user.setCompany(userInformationEditInput.getCompany());
        user.setLatitude(coordinate.getLatitude());
        user.setLongitude(coordinate.getLongitude());

        userRepository.save(user);

        return ServiceResult.success("고객정보 수정 완료");
    }

    @Override
    @Transactional
    public ServiceResult changeUserPassword(Long userId,
                                            UserChangePasswordInput userChangePasswordInput) {

        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserInfoNotFoundException("고객 정보를 찾을 수 없습니다. 다시 시도해 주세요.")
        );

        String existingPassword = userChangePasswordInput.getUserExistingPassword();

        if (!PasswordUtility.isPasswordMatch(existingPassword, user.getUserPassword())) {
            throw new InvalidLoginException("기존 비밀번호가 일치하지 않습니다.");
        }

        //새로 입력된 비밀번호 암호화
        String encryptedPassword =
                PasswordUtility.getEncryptPassword(userChangePasswordInput.getUserNewPassword());

        user.setUserPassword(encryptedPassword);

        userRepository.save(user);

        return ServiceResult.success("고객 정보 수정 완료");
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto tokenValidation(String token) {
        try {
            // issuer 가져오기
            String email = jwtTokenProvider.getIssuer(token);

            // 추출한 issuer 로 사용자 조회
            User user = userRepository.findByUserEmail(email).orElseThrow(() ->
                    new InvalidTokenException("사용자가 존재하지 않습니다.")
            );

            return UserDto.of(user);

        } catch (Exception e) {
            // 토큰 검증 또는 사용자 조회 중 예외 발생 시
            throw new InvalidTokenException("토큰이 만료되었거나 유효하지 않습니다. 다시 로그인해주세요.");
        }
    }

    //설정한 거리 이내 회원 목록 가져오기
    @Override
    @Transactional(readOnly = true)
    public ServiceResult findUserAround(double latitude, double longitude, double kilometer) {
        List<Object[]> userList =
                userRepository.getUserListByDistance(latitude, longitude, kilometer);

        if (userList == null || userList.isEmpty()) {
            throw new ContentNotFoundException("해당 조건으로 조회되는 주변회원이 없습니다.");
        }

        List<UserDistanceDto> userListResponses = new ArrayList<>();

        userList.forEach(objects -> {
            userListResponses.add(UserDistanceDto.of(objects));
        });

        return ServiceResult.success("주변회원 불러오기 성공", userListResponses);
    }
}
