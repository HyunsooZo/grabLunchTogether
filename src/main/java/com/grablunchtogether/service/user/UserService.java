package com.grablunchtogether.service.user;

import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.dto.geocode.GeocodeDto;
import com.grablunchtogether.dto.user.UserSignUpInput;

public interface UserService {

    //회원가입
    ServiceResult userSignUp(UserSignUpInput userSignUpInput, GeocodeDto userCoordinate);
}
