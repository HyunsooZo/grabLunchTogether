package com.grablunchtogether.controller;

import com.grablunchtogether.common.results.responseResult.ResponseError;
import com.grablunchtogether.common.results.responseResult.ResponseResult;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.dto.plan.PlanCreationInput;
import com.grablunchtogether.dto.user.UserDto;
import com.grablunchtogether.service.externalApi.naverSms.SMSApiService;
import com.grablunchtogether.service.plan.PlanService;
import com.grablunchtogether.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/plan")
@Api(tags = "Plan API", description = "점심약속과 관련된 API")
@RestController
public class PlanController {
    private final UserService userService;
    private final PlanService planService;
    private final SMSApiService smsApiService;

    @GetMapping("/search/list/{kilometer}")
    @ApiOperation(value = "주변회원 찾기", notes = "입력된 거리 내 회원목록을 가져옵니다.")
    public ResponseEntity<?> getUserList(
            @PathVariable Double kilometer,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDto userDto = userService.tokenValidation(token);

        ServiceResult result = userService.findUserAround(
                userDto.getLatitude(), userDto.getLongitude(), kilometer
        );

        return ResponseResult.result(result);
    }

    @PostMapping("/create/{accepterId}")
    @ApiOperation(value = "점심약속 생성하기", notes = "상대방에게 입력된 정보로 점심약속을 신청합니다.")
    public ResponseEntity<?> createAPlan(
            @PathVariable Long accepterId,
            @Valid @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody PlanCreationInput planCreationInput,
            Errors errors) {

        ResponseEntity<?> responseErrorList = errorValidation(errors);
        if (responseErrorList != null) {
            return responseErrorList;
        }

        UserDto userDto = userService.tokenValidation(token);

        ServiceResult result =
                planService.createPlan(userDto.getId(), accepterId, planCreationInput);

        smsApiService.sendSmsToAccepter(userDto.getId(), accepterId, planCreationInput);

        return ResponseResult.result(result);
    }

    // 나에게 신청된 점심약속 리스트 조회
    @GetMapping("/received")
    @ApiOperation(value = "받은 점심약속요청 조회하기", notes = "신청받은 모든 약속요청리스트를 조회합니다.")
    public ResponseEntity<?> getPlanListReceived(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDto userDto = userService.tokenValidation(token);

        ServiceResult result = planService.getPlanListReceived(userDto.getId());

        return ResponseResult.result(result);
    }

    // 내가 신청한 점심약속 리스트 조회
    @GetMapping("/api/plan/list/requested")
    @ApiOperation(value = "요청한 점심약속 조회하기" , notes = "내가 요청한 모든 약속리스트 조회하기")
    public ResponseEntity<?> getPlanListIRequested(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDto userDto = userService.tokenValidation(token);

        ServiceResult result = planService.getPlanListIRequested(userDto.getId());

        return ResponseResult.result(result);
    }

    // 점심약속 업데이트(1) - 거절 / 승낙 (상태업데이트)
    @PatchMapping("/api/plan/accept/{planId}/{acceptCode}")
    @ApiOperation(value = "점심약속 수락/거절 하기" , notes = "내가 받은 점심약속 수락/거절하기")
    public ResponseEntity<?> approvePlanRequest(
            @PathVariable Long planId,
            @PathVariable Character acceptCode,
            @RequestHeader("Authorization") String token) {

        UserDto userDto = userService.tokenValidation(token);
        ServiceResult result = planService.approvePlan(userDto.getId(), planId, acceptCode);

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
