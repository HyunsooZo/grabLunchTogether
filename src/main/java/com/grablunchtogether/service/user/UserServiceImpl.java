package com.grablunchtogether.service.user;

import com.grablunchtogether.common.exception.InvalidLoginException;
import com.grablunchtogether.common.exception.InvalidTokenException;
import com.grablunchtogether.common.exception.UserSignUpException;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.configuration.springSecurity.JwtTokenProvider;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.geocode.GeocodeDto;
import com.grablunchtogether.dto.token.TokenDto;
import com.grablunchtogether.dto.user.UserDto;
import com.grablunchtogether.dto.user.UserLoginInput;
import com.grablunchtogether.dto.user.UserSignUpInput;
import com.grablunchtogether.repository.UserRepository;
import com.grablunchtogether.utility.PasswordUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
}
