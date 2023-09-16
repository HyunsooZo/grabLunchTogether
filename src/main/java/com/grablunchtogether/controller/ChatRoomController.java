package com.grablunchtogether.controller;

import com.grablunchtogether.config.JwtTokenProvider;
import com.grablunchtogether.dto.chat.ChatRoomDto;
import com.grablunchtogether.service.ChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RequestMapping("/api/chat/rooms")
@Api(tags = "User API", description = "사용자와 관련된 API")
@RestController
public class ChatRoomController {
    private final ChatService chatService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("targetUser/{targetUserId}")
    @ApiOperation(value = "채팅방 생성 또는 채팅방 불러오기", notes = "상대방과의 채팅을 생성(또는기존 채팅을 불러오기)")
    public ResponseEntity<?> deleteBookmarkSpot(
            @PathVariable Long targetUserId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token){

        Long userId = jwtTokenProvider.getIdFromToken(token);

        ChatRoomDto.CreateRoomDto createRoomDto = chatService.createOrFindRoom(userId, targetUserId);

        return ResponseEntity.status(OK).body(ChatRoomDto.CreateRoomResponse.of(createRoomDto));
    }
}
