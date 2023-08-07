package com.grablunchtogether.controller;

import com.grablunchtogether.common.results.responseResult.ResponseError;
import com.grablunchtogether.common.results.responseResult.ResponseResult;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.dto.geocode.GeocodeDto;
import com.grablunchtogether.dto.user.*;
import com.grablunchtogether.service.user.UserService;
import com.grablunchtogether.service.user.externalApi.GeocodeApiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@Api(tags = "User API", description = "사용자와 관련된 API")
@RestController
public class UserController {

    private final UserService userService;
    private final GeocodeApiService geocodeApiService;

    @PostMapping("/signup")
    @Transactional
    @ApiOperation(value = "사용자 회원가입", notes = "입력된 정보로 회원가입을 진행합니다.")
    public ResponseEntity<?> userSignUp(
            @Valid @RequestBody UserSignUpInput userSignUpInput,
            Errors errors) {

        ResponseEntity<?> responseErrorList = errorValidation(errors);
        if (responseErrorList != null) {
            return responseErrorList;
        }

        //고객 좌표 가져오는 외부 api호출
        GeocodeDto userCoordinate = geocodeApiService.getCoordinate(
                userSignUpInput.getStreetName(), userSignUpInput.getStreetNumber());

        ServiceResult result =
                userService.userSignUp(userSignUpInput, userCoordinate);

        return ResponseResult.result(result);
    }

    @PostMapping("/login")
    @ApiOperation(value = "사용자 로그인", notes = "입력된 사용자 ID/PW로 로그인을 진행합니다.")
    public ResponseEntity<?> login(
            @Valid @RequestBody UserLoginInput userLoginInput, Errors errors) {

        ResponseEntity<?> responseErrorList = errorValidation(errors);
        if (responseErrorList != null) {
            return responseErrorList;
        }

        ServiceResult result = userService.login(userLoginInput);

        return ResponseResult.result(result);
    }

    @PatchMapping("/edit/information")
    @ApiOperation(value = "사용자 정보 수정", notes = "입력된 정보로 기존사용자의 정보를 수정합니다.")
    public ResponseEntity<?> editUserInformation(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody UserInformationEditInput userInformationEditInput,
            Errors errors) {

        ResponseEntity<?> responseErrorList = errorValidation(errors);
        if (responseErrorList != null) {
            return responseErrorList;
        }

        UserDto userDto = userService.tokenValidation(token);

        //수정된 주소의 좌표 다시 가져오기
        GeocodeDto coordinate = geocodeApiService.getCoordinate(
                userInformationEditInput.getAddress(), userInformationEditInput.getStreetNumber()
        );

        ServiceResult result = userService.editUserInformation(
                userDto.getId(), userInformationEditInput, coordinate
        );

        return ResponseResult.result(result);
    }

    @PatchMapping("/change/password")
    @ApiOperation(value = "사용자 비밀번호 변경", notes = "기존 비밀번호를 입력된 비밀번호로 변경합니다.")
    public ResponseEntity<?> changePassword(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody UserChangePasswordInput userChangePasswordInput,
            Errors errors) {

        ResponseEntity<?> responseErrorList = errorValidation(errors);
        if (responseErrorList != null) {
            return responseErrorList;
        }

        UserDto userDto = userService.tokenValidation(token);

        ServiceResult result =
                userService.changeUserPassword(userDto.getId(), userChangePasswordInput);

        return ResponseResult.result(result);
    }

    private ResponseEntity<?> errorValidation(Errors errors) {
        List<ResponseError> responseErrorList = new ArrayList<>();
        if (errors.hasErrors()) {
            errors.getAllErrors().forEach(error -> {
                responseErrorList.add(ResponseError.of((FieldError) error));
            });
            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }
        return null;
    }
}
