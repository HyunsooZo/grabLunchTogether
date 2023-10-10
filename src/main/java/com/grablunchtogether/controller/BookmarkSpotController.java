package com.grablunchtogether.controller;

import com.grablunchtogether.config.JwtTokenProvider;
import com.grablunchtogether.dto.bookmarkSpot.BookmarkSpotDto;
import com.grablunchtogether.service.BookmarkSpotService;
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
@RequestMapping("/api/bookmark-spots")
@Api(tags = "BookMark Spot API", description = "즐겨찾는 장소와 관련된 API")
@RestController
public class BookmarkSpotController {
    private final BookmarkSpotService bookmarkSpotService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    @ApiOperation(value = "맛집 즐겨찾기 등록", notes = "사용자가 직접입력하여 맛집 즐겨찾기를 등록합니다.")
    public ResponseEntity<Void> registerBookmarkSpot(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody BookmarkSpotDto.Request bookmarkSpotRequest) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        bookmarkSpotService.registerBookmark(bookmarkSpotRequest, userId);

        return ResponseEntity.status(CREATED).build();
    }

    @PostMapping("/must-eat-place/{mustEatPlaceId}")
    @ApiOperation(value = "맛집 즐겨찾기 등록", notes = "사용자가 조회된 맛집을 이용해 맛집 즐겨찾기를 등록합니다.")
    public ResponseEntity<Void> registerBookmarkSpotWithMustEatPlace(
            @PathVariable Long mustEatPlaceId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        bookmarkSpotService.registerBookmarkWithMustEatPlace(mustEatPlaceId, userId);

        return ResponseEntity.status(CREATED).build();
    }

    @GetMapping
    @ApiOperation(value = "맛집 즐겨찾기 조회", notes = "본인이 등록된 맛집 즐겨찾기 목록을 가져옵니다.")
    public ResponseEntity<BookmarkSpotDto.Response> listBookmarkSpot(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        List<BookmarkSpotDto.Dto> bookmarkSpotDtos =
                bookmarkSpotService.listBookmarkSpot(userId);

        return ResponseEntity.status(OK).body(BookmarkSpotDto.Response.of(bookmarkSpotDtos));
    }

    @DeleteMapping("/{bookmarkSpotId}")
    @ApiOperation(value = "맛집 즐겨찾기 삭제", notes = "본인이 등록된 맛집 즐겨찾기 목록을 삭제합니다.")
    public ResponseEntity<Void> deleteBookmarkSpot(
            @PathVariable Long bookmarkSpotId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        bookmarkSpotService.deleteBookmarkSpot(bookmarkSpotId, userId);

        return ResponseEntity.status(NO_CONTENT).build();
    }
}
