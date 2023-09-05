package com.grablunchtogether.controller;

import com.grablunchtogether.configuration.JwtTokenProvider;
import com.grablunchtogether.dto.favoriteUser.FavoriteUserDto;
import com.grablunchtogether.service.FavoriteUserService;
import com.grablunchtogether.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RequestMapping("/api/favorite-users")
@Api(tags = "Favorite User API", description = "즐겨찾는 유저 관련된 API")
@RestController
public class FavoriteUserController {
    private final UserService userService;
    private final FavoriteUserService favoriteUserService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/user/{otherUserId}")
    @ApiOperation(value = "즐겨찾는 친구 추가하기", notes = "즐겨찾는 유저를 추가합니다.")
    public ResponseEntity<Void> addFavoriteUser(
            @PathVariable Long otherUserId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody FavoriteUserDto.Request favoriteUserRequest) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        favoriteUserService.addFavoriteUser(favoriteUserRequest, userId, otherUserId);

        return ResponseEntity.status(CREATED).build();
    }

    @PatchMapping("/{favoriteUserId}")
    @ApiOperation(value = "즐겨찾는 친구 닉네임 수정하기", notes = "즐겨찾는 유저 닉네임을 수정합니다.")
    public ResponseEntity<Void> editFavoriteUser(
            @PathVariable Long favoriteUserId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody FavoriteUserDto.Request favoriteUserEditRequest) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        favoriteUserService.editFavoriteUser(favoriteUserEditRequest, userId, favoriteUserId);

        return ResponseEntity.status(NO_CONTENT).build();
    }

    @DeleteMapping("/{favoriteUserId}")
    @ApiOperation(value = "즐겨찾는 친구 삭제하기", notes = "즐겨찾는 유저를 삭제합니다.")
    public ResponseEntity<Void> deleteFavoriteUser(
            @PathVariable Long favoriteUserId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        favoriteUserService.deleteFavoriteUser(userId, favoriteUserId);

        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PostMapping
    @ApiOperation(value = "즐겨찾는 친구 조회하기", notes = "즐겨찾는 유저목록을 조회합니다.")
    public ResponseEntity<FavoriteUserDto.Response> listFavoriteUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        List<FavoriteUserDto.Dto> favoriteUsers =
                favoriteUserService.listFavoriteUser(userId);

        return ResponseEntity.status(OK).body(FavoriteUserDto.Response.of(favoriteUsers));
    }
}
