package com.grablunchtogether.controller;

import com.grablunchtogether.common.results.responseResult.ResponseError;
import com.grablunchtogether.common.results.responseResult.ResponseResult;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.dto.bookmarkSpot.BookmarkSpotInput;
import com.grablunchtogether.dto.user.UserDto;
import com.grablunchtogether.service.bookmarkSpot.BookmarkSpotService;
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
@RequestMapping("/api/bookmark-spot")
@Api(tags = "BookMark Spot API", description = "즐겨찾는 장소와 관련된 API")
@RestController
public class BookmarkSpotController {
    private final UserService userService;
    private final BookmarkSpotService bookmarkSpotService;

    @PostMapping("/register/manual")
    @ApiOperation(value = "맛집 즐겨찾기 등록", notes = "사용자가 직접입력하여 맛집 즐겨찾기를 등록합니다.")
    public ResponseEntity<?> registerBookmarkSpot(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody BookmarkSpotInput bookmarkSpotInput,
            Errors errors) {

        ResponseEntity<?> responseErrorList = errorValidation(errors);
        if (responseErrorList != null) {
            return responseErrorList;
        }

        UserDto user = userService.tokenValidation(token);

        ServiceResult result =
                bookmarkSpotService.registerBookmark(bookmarkSpotInput, user.getId());

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
