package com.grablunchtogether.controller;

import com.grablunchtogether.config.JwtTokenProvider;
import com.grablunchtogether.dto.PlanDto;
import com.grablunchtogether.service.PlanHistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@RequestMapping("/api/planhistorys")
@Api(tags = "Plan History API", description = "사용자의 약속 히스토리와 관련된 API")
@RestController
public class PlanHistoryController {
    private final PlanHistoryService planHistoryService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    @ApiOperation(value = "사용자 점심약속 히스토리 조회", notes = "사용자가 완료한 약속목록을 조회합니다.")
    public ResponseEntity<?> getMyHistoryList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        List<PlanDto.Dto> plans = planHistoryService.listMyHistory(userId);

        return ResponseEntity.status(OK).body(PlanDto.Response.of(plans));
    }
}
