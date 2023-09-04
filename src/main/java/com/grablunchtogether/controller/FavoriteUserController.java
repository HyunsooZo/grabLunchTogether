package com.grablunchtogether.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/api/favorite-users")
@Api(tags = "Favorite User API", description = "즐겨찾는 유저 관련된 API")
@RestController
public class FavoriteUserController {
    private final UserService userService;
    private final FavoriteUserService favoriteUserService;

    @PostMapping("/user/{otherUserId}")
    @ApiOperation(value = "즐겨찾는 친구 추가하기", notes = "즐겨찾는 유저를 추가합니다.")
    public ResponseEntity<?> addFavoriteUser(
            @PathVariable Long otherUserId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody FavoriteUserInput favoriteUserInput) {

        UserDto user = userService.tokenValidation(token);

        ServiceResult result = favoriteUserService.addFavoriteUser(favoriteUserInput,
                user.getId(),
                otherUserId);

        return ResponseResult.result(result);
    }

    @PatchMapping("/{favoriteUserId}")
    @ApiOperation(value = "즐겨찾는 친구 닉네임 수정하기", notes = "즐겨찾는 유저 닉네임을 수정합니다.")
    public ResponseEntity<?> editFavoriteUser(
            @PathVariable Long favoriteUserId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody FavoriteUserInput favoriteUserEditInput) {

        UserDto user = userService.tokenValidation(token);

        ServiceResult result =
                favoriteUserService.editFavoriteUser(favoriteUserEditInput,
                        user.getId(),
                        favoriteUserId);

        return ResponseResult.result(result);
    }

    @DeleteMapping("/{favoriteUserId}")
    @ApiOperation(value = "즐겨찾는 친구 삭제하기", notes = "즐겨찾는 유저를 삭제합니다.")
    public ResponseEntity<?> deleteFavoriteUser(
            @PathVariable Long favoriteUserId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDto user = userService.tokenValidation(token);

        ServiceResult result =
                favoriteUserService.deleteFavoriteUser(user.getId(), favoriteUserId);

        return ResponseResult.result(result);
    }

    @PostMapping
    @ApiOperation(value = "즐겨찾는 친구 조회하기", notes = "즐겨찾는 유저목록을 조회합니다.")
    public ResponseEntity<?> listFavoriteUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDto user = userService.tokenValidation(token);

        ServiceResult result = favoriteUserService.listFavoriteUser(user.getId());

        return ResponseResult.result(result);
    }
}
