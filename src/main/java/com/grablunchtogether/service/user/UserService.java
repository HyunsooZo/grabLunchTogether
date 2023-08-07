package com.grablunchtogether.service.user;

import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.dto.geocode.GeocodeDto;
import com.grablunchtogether.dto.user.*;

public interface UserService {

    //회원가입
    ServiceResult userSignUp(UserSignUpInput userSignUpInput, GeocodeDto userCoordinate);

    //유저 로그인
    ServiceResult login(UserLoginInput userLoginInput);

    //유저 정보 수정
    ServiceResult editUserInformation(Long id,
                                      UserInformationEditInput userInformationEditInput,
                                      GeocodeDto coordinate);

    //유저 비밀번호 변경
    ServiceResult changeUserPassword(Long userId,
                                     UserChangePasswordInput userChangePasswordInput);

    //토큰넘겨서 UserDto 가져오기
    UserDto tokenValidation(String token);
}
