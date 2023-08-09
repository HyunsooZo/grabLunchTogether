package com.grablunchtogether.controller;

import com.grablunchtogether.common.results.responseResult.ResponseResult;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.dto.user.UserDto;
import com.grablunchtogether.service.mustEatPlace.MustEatPlaceService;
import com.grablunchtogether.service.user.UserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/must-eat-place")
@Api(tags = "Must-Eat Place API", description = "지역별 맛집과 관련된 API")
@RestController
public class MustEatPlaceController {
    private final UserService userService;
    private final MustEatPlaceService mustEatPlaceService;

    @GetMapping("/list/{city}")
    public ResponseEntity<?> findMustEatPlaces(
            @PathVariable String city,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDto user = userService.tokenValidation(token);

        ServiceResult result = mustEatPlaceService.mustEatPlaceList(city);

        return ResponseResult.result(result);
    }
}
