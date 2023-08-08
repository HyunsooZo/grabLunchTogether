package com.grablunchtogether.controller;

import com.grablunchtogether.common.results.responseResult.ResponseResult;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.dto.user.UserDto;
import com.grablunchtogether.service.planHistory.PlanHistoryService;
import com.grablunchtogether.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/planhistory")
@Api(tags = "Plan History API", description = "사용자의 약속 히스토리와 관련된 API")
@RestController
public class PlanHistoryController {
    private final PlanHistoryService planHistoryService;
    private final UserService userService;

    @GetMapping("/list")
    @ApiOperation(value = "사용자 점심약속 히스토리 조회", notes = "사용자가 완료한 약속목록을 조회합니다.")
    public ResponseEntity<?> getMyHistoryList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDto userDto = userService.tokenValidation(token);

        ServiceResult result = planHistoryService.listMyHistory(userDto.getId());

        return ResponseResult.result(result);
    }
}
