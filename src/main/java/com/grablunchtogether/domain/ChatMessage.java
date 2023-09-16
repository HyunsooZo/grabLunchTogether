package com.grablunchtogether.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long chatRoomId;

    @Column
    private Long senderId;

    @Column
    private Long receiverId;

    @Column(nullable = false)
    private String message;
}