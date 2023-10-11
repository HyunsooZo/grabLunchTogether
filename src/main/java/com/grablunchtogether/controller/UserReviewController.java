package com.grablunchtogether.controller;

import com.grablunchtogether.config.JwtTokenProvider;
import com.grablunchtogether.service.UserReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.grablunchtogether.dto.UserReviewDto.*;
import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@Api(tags = "User Review API", description = "사용자 리뷰 관련된 API")
@RequestMapping("/api/review")
@RestController
public class UserReviewController {
    private final UserReviewService userReviewService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/planhistory/{planHistoryId}")
    @ApiOperation(value = "상대방에게 리뷰등록", notes = "점심약속을 마친 상대방에게 리뷰를 남깁니다.")
    public ResponseEntity<?> addReview(
            @PathVariable Long planHistoryId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody UserReviewRequest userReviewRequest) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        userReviewService.addReview(userId, planHistoryId, userReviewRequest);

        return ResponseEntity.status(CREATED).build();
    }

    @PutMapping("/{userReviewId}")
    @ApiOperation(value = "작성된 리뷰 수정", notes = "작성된 리뷰를 수정합니다.(별점은 수정이 불가합니다.)")
    public ResponseEntity<?> editUserReview(
            @PathVariable Long userReviewId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody UserReviewRequest userReviewEditInput) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        userReviewService.editReview(userId, userReviewId, userReviewEditInput);

        return ResponseEntity.status(NO_CONTENT).build();
    }

    @DeleteMapping("/{userReviewId}")
    @ApiOperation(value = "작성된 리뷰 삭제", notes = "작성된 리뷰를 삭제합니다.(평균 별점은 변동되지 않습니다.)")
    public ResponseEntity<?> deleteUserReview(
            @PathVariable Long userReviewId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        userReviewService.deleteReview(userId, userReviewId);

        return ResponseEntity.status(NO_CONTENT).build();
    }

    @GetMapping("/user/{targetUserId}")
    @ApiOperation(value = "사용자에 대한 리뷰목록 조회", notes = "해당 사용자에 대한 리뷰들을 조회합니다.")
    public ResponseEntity<?> listUserReview(
            @PathVariable Long targetUserId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        List<Dto> reviews = userReviewService.listReviews(targetUserId);

        return ResponseEntity.status(OK).body(Response.from(reviews));
    }
}
