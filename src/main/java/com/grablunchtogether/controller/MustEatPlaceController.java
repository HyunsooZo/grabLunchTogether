package com.grablunchtogether.controller;

import com.grablunchtogether.dto.MustEatPlaceDto;
import com.grablunchtogether.service.MustEatPlaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@RequestMapping("/api/must-eat-places")
@Api(tags = "Must-Eat Place API", description = "지역별 맛집과 관련된 API")
@RestController
public class MustEatPlaceController {
    private final MustEatPlaceService mustEatPlaceService;

    @GetMapping("/{city}")
    @ApiOperation(value = "맛집목록 불러오기", notes = "도시를 입력하여 해당지역의 맛집목록을 조회합니다.")
    public ResponseEntity<?> findMustEatPlaces(
            @PathVariable String city,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        List<MustEatPlaceDto.Dto> mustEatPlaces = mustEatPlaceService.mustEatPlaceList(city);

        return ResponseEntity.status(OK).body(MustEatPlaceDto.Response.from(mustEatPlaces));
    }
}
