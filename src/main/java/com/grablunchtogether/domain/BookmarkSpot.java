package com.grablunchtogether.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class BookmarkSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User userId;

    @Column
    private String restaurant;

    @Column
    private String menu;

    @Column
    private String address;

    @Column
    private String operationHour;

    @Column
    private String rate;

    @Column
    private LocalDateTime registeredAt;
}
