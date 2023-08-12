package com.grablunchtogether.controller;

import com.grablunchtogether.common.results.responseResult.ResponseError;
import com.grablunchtogether.common.results.responseResult.ResponseResult;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.dto.user.UserDto;
import com.grablunchtogether.dto.userReview.UserReviewInput;
import com.grablunchtogether.service.user.UserService;
import com.grablunchtogether.service.userReview.UserReviewService;
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
@Api(tags = "User Review API", description = "사용자 리뷰 관련된 API")
@RequestMapping("/api/review")
@RestController
public class UserReviewController {
    private final UserReviewService userReviewService;
    private final UserService userService;

    @PostMapping("/planhistory/{planHistoryId}")
    @ApiOperation(value = "상대방에게 리뷰등록", notes = "점심약속을 마친 상대방에게 리뷰를 남깁니다.")
    public ResponseEntity<?> addReview(
            @PathVariable Long planHistoryId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody UserReviewInput userReviewInput,
            Errors errors) {

        ResponseEntity<?> responseErrorList = errorValidation(errors);
        if (responseErrorList != null) {
            return responseErrorList;
        }

        UserDto userDto = userService.tokenValidation(token);

        ServiceResult result =
                userReviewService.addReview(userDto.getId(), planHistoryId, userReviewInput);

        return ResponseResult.result(result);
    }

    @PutMapping("/{userReviewId}")
    @ApiOperation(value = "작성된 리뷰 수정", notes = "작성된 리뷰를 수정합니다.(별점은 수정이 불가합니다.)")
    public ResponseEntity<?> editUserReview(
            @PathVariable Long userReviewId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody UserReviewInput userReviewEditInput) {

        UserDto userDto = userService.tokenValidation(token);

        ServiceResult result =
                userReviewService.editReview(userDto.getId(), userReviewId, userReviewEditInput);

        return ResponseResult.result(result);
    }

    @DeleteMapping("/{userReviewId}")
    @ApiOperation(value = "작성된 리뷰 삭제", notes = "작성된 리뷰를 삭제합니다.(평균 별점은 변동되지 않습니다.)")
    public ResponseEntity<?> deleteUserReview(
            @PathVariable Long userReviewId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDto userDto = userService.tokenValidation(token);

        ServiceResult result =
                userReviewService.deleteReview(userDto.getId(), userReviewId);

        return ResponseResult.result(result);
    }

    @GetMapping("/{targetUserId}")
    @ApiOperation(value = "사용자에 대한 리뷰목록 조회", notes = "해당 사용자에 대한 리뷰들을 조회합니다.")
    public ResponseEntity<?> listUserReview(
            @PathVariable Long targetUserId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDto userDto = userService.tokenValidation(token);

        ServiceResult result =
                userReviewService.listReviews(targetUserId);

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
