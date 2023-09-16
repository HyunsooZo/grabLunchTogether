package com.grablunchtogether.service;

import com.grablunchtogether.domain.ChatRoom;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.chat.ChatRoomDto;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.exception.ErrorCode;
import com.grablunchtogether.repository.ChatMessageRepository;
import com.grablunchtogether.repository.ChatRoomRepository;
import com.grablunchtogether.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatRoomDto.CreateRoomDto createOrFindRoom(Long userIdOpenedChat, Long userIdOpponent) {
        ChatRoom chatRoom =
                //기존에 두 유저 간 생성된 방이 있는지 확인 , 없다면 새로운 방을 만들어 저장
                chatRoomRepository.findByUserOpenedChatAndUserOpponent(userIdOpenedChat, userIdOpponent)
                        .orElseGet(() -> chatRoomRepository.findByUserOpenedChatAndUserOpponent(userIdOpponent, userIdOpenedChat)
                                .orElse(
                                        chatRoomRepository.save(
                                                ChatRoom.builder()
                                                        .roomUUID(UUID.randomUUID().toString())
                                                        .lastSeenUser(null)
                                                        .userOpenedChat(userIdOpenedChat)
                                                        .userOpponent(userIdOpponent)
                                                        .build())));
        return ChatRoomDto.CreateRoomDto.of(chatRoom);
    }
}
