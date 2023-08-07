package com.grablunchtogether.controller;

import com.grablunchtogether.common.results.responseResult.ResponseResult;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.dto.user.UserDto;
import com.grablunchtogether.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/plan")
@Api(tags = "Plan API", description = "점심약속과 관련된 API")
@RestController
public class PlanController {
    private final UserService userService;

    @GetMapping("/search/list/{kilometer}")
    @ApiOperation(value = "주변회원 찾기", notes = "입력된 거리 내 회원목록을 가져옵니다.")
    public ResponseEntity<?> getUserList(
            @PathVariable Double kilometer,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDto userDto = userService.tokenValidation(token);

        ServiceResult result =
                userService.findUserAround(userDto.getLatitude(), userDto.getLongitude(), kilometer);

        return ResponseResult.result(result);
    }
}
