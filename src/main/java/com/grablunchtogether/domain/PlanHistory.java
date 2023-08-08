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
public class PlanHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "planId", referencedColumnName = "id")
    private Plan planId;

    @ManyToOne
    @JoinColumn(name = "requesterId", referencedColumnName = "id")
    private User requesterId;

    @ManyToOne
    @JoinColumn(name = "accepterId", referencedColumnName = "id")
    private User accepterId;

    @Column
    private LocalDateTime registeredAt;
}
