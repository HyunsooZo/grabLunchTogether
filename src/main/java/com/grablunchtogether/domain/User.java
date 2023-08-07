package com.grablunchtogether.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String userEmail;

    @Column
    private String userName;

    @JsonIgnore
    @Column
    private String userPassword;

    @Column
    private String userPhoneNumber;

    @Column
    private Double userRate;

    @Column
    private String company;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column
    private LocalDateTime registeredAt;
}

