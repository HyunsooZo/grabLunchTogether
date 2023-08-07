package com.grablunchtogether.service.user;

import com.grablunchtogether.common.exception.UserSignUpException;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.geocode.GeocodeDto;
import com.grablunchtogether.dto.user.UserSignUpInput;
import com.grablunchtogether.repository.UserRepository;
import com.grablunchtogether.utility.PasswordUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

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
}
