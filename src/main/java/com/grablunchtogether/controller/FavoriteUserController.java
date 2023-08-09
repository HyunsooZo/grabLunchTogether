package com.grablunchtogether.controller;

import com.grablunchtogether.common.results.responseResult.ResponseError;
import com.grablunchtogether.common.results.responseResult.ResponseResult;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.dto.favoriteUser.FavoriteUserInput;
import com.grablunchtogether.dto.user.UserDto;
import com.grablunchtogether.service.favoriteUser.FavoriteUserService;
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
@RequestMapping("/api/favorite-user")
@Api(tags = "Favorite User API", description = "즐겨찾는 유저 관련된 API")
@RestController
public class FavoriteUserController {
    private final UserService userService;
    private final FavoriteUserService favoriteUserService;

    @PostMapping("/add/user/{favoriteUserId}")
    @ApiOperation(value = "즐겨찾는 친구 추가하기", notes = "즐겨찾는 유저를 추가합니다.")
    public ResponseEntity<?> addFavoriteUser(
            @PathVariable Long favoriteUserId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody FavoriteUserInput favoriteUserInput,
            Errors errors) {

        ResponseEntity<?> responseErrorList = errorValidation(errors);
        if (responseErrorList != null) {
            return responseErrorList;
        }

        UserDto user = userService.tokenValidation(token);

        ServiceResult result = favoriteUserService.addFavoriteUser(favoriteUserInput,
                                                                    user.getId(),
                                                                    favoriteUserId);

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
