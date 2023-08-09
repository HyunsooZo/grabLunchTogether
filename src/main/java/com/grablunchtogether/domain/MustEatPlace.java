package com.grablunchtogether.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class MustEatPlace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String restaurant;

    @Column
    private String menu;

    @Column
    private String address;

    @Column
    private String operationHour;

    @Column
    private String city;

    @Column
    private String rate;
}
