package com.grablunchtogether.dto.chat;

import com.grablunchtogether.domain.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatRoomDto {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CreateRoomDto {
        private Long id;
        private String roomUUID;

        public static CreateRoomDto of(ChatRoom chatRoom) {
            return CreateRoomDto.builder()
                    .id(chatRoom.getId())
                    .roomUUID(chatRoom.getRoomUUID())
                    .build();
        }
    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CreateRoomResponse {
        private Long id;
        private String roomUUID;

        public static CreateRoomResponse of(CreateRoomDto createRoomDto) {
            return CreateRoomResponse.builder()
                    .id(createRoomDto.getId())
                    .roomUUID(createRoomDto.getRoomUUID())
                    .build();
        }
    }
}
