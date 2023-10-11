package com.grablunchtogether.controller;

import com.grablunchtogether.config.JwtTokenProvider;
import com.grablunchtogether.dto.PlanDto;
import com.grablunchtogether.dto.UserDistanceDto;
import com.grablunchtogether.dto.UserDto;
import com.grablunchtogether.service.PlanHistoryService;
import com.grablunchtogether.service.PlanService;
import com.grablunchtogether.service.SMSApiService;
import com.grablunchtogether.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RequestMapping("/api/plans")
@Api(tags = "Plan API", description = "점심약속과 관련된 API")
@RestController
public class PlanController {
    private final UserService userService;
    private final PlanService planService;
    private final PlanHistoryService planHistoryService;
    private final SMSApiService smsApiService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/distance/{kilometer}")
    @ApiOperation(value = "주변회원 찾기", notes = "입력된 거리 내 회원목록을 가져옵니다.")
    public ResponseEntity<UserDistanceDto.Response> getUserList(
            @PathVariable Double kilometer,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        Long userId = jwtTokenProvider.getIdFromToken(token);
        UserDto.Dto userDto = userService.getUserById(userId);

        List<UserDistanceDto.Dto> userAround =
                userService.findUserAround(userDto.getLatitude(), userDto.getLongitude(), kilometer);

        return ResponseEntity.status(OK).body(UserDistanceDto.Response.of(userAround));
    }

    @PostMapping("/user/{accepterId}")
    @ApiOperation(value = "점심약속 생성하기", notes = "상대방에게 입력된 정보로 점심약속을 신청합니다.")
    public ResponseEntity<Void> createAPlan(
            @PathVariable Long accepterId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody PlanDto.Request planCreationRequest) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        planService.createPlan(userId, accepterId, planCreationRequest);

        smsApiService.sendSmsToAccepter(userId, accepterId, planCreationRequest);

        return ResponseEntity.status(CREATED).build();
    }

    // 나에게 신청된 점심약속 리스트 조회
    @GetMapping("/received")
    @ApiOperation(value = "받은 점심약속요청 조회하기", notes = "신청받은 모든 약속요청리스트를 조회합니다.")
    public ResponseEntity<?> getPlanListReceived(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        List<PlanDto.Dto> plans = planService.getPlanListReceived(userId);

        return ResponseEntity.status(OK).body(PlanDto.Response.of(plans));
    }

    // 내가 신청한 점심약속 리스트 조회
    @GetMapping("/requested")
    @ApiOperation(value = "요청한 점심약속 조회하기", notes = "내가 요청한 모든 약속리스트 조회하기")
    public ResponseEntity<?> getPlanListIRequested(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        List<PlanDto.Dto> plans =
                planService.getPlanListIRequested(userId);

        return ResponseEntity.status(OK).body(PlanDto.Response.of(plans));
    }

    // 점심약속 업데이트(1) - 거절 / 승낙 (상태업데이트)
    @PatchMapping("/{planId}/accept/{acceptCode}")
    @ApiOperation(value = "점심약속 수락/거절 하기", notes = "내가 받은 점심약속 수락/거절하기")
    public ResponseEntity<?> approvePlanRequest(
            @PathVariable Long planId,
            @PathVariable Character acceptCode,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        Long userId = jwtTokenProvider.getIdFromToken(token);
        planService.approvePlan(userId, planId, acceptCode);

        return ResponseEntity.status(NO_CONTENT).build();
    }

    // 점심약속 업데이트(2) - 취소(상태업데이트)
    @PatchMapping("/{planId}/cancel")
    @ApiOperation(value = "점심약속 취소 하기", notes = "수락된 점심약속 취소하기")
    public ResponseEntity<?> cancelPlanRequest(
            @PathVariable Long planId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        PlanDto.Dto planDto = planService.cancelPlan(userId, planId);

        planHistoryService.registerHistory(planDto.getId());

        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PatchMapping("/{planId}/edit")
    @ApiOperation(value = "점심약속 수정 하기", notes = "수락된 점심약속 취소하기")
    public ResponseEntity<?> editPlanRequest(
            @PathVariable Long planId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody PlanDto.Request planModificationRequest) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        planService.editPlanRequest(userId, planId, planModificationRequest);

        return ResponseEntity.status(NO_CONTENT).build();
    }
    @PatchMapping("/{planId}/complete")
    @ApiOperation(value = "점심약속 완료처리 하기", notes = "성사된 점심약속 완료처리")
    public ResponseEntity<?> completePlan(
            @PathVariable Long planId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        planService.completePlan(userId,planId);

        return ResponseEntity.status(NO_CONTENT).build();
    }

    // 점심약속 삭제
    @DeleteMapping("/{planId}")
    @ApiOperation(value = "점심약속 삭제 하기", notes = "요청상태의 점심약속 삭제하기")
    public ResponseEntity<?> deletePlanRequest(
            @PathVariable Long planId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        planService.planDeletion(userId, planId);

        return ResponseEntity.status(NO_CONTENT).build();
    }
}
