package com.grablunchtogether.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    public void update(String userPhoneNumber, String company, Double latitude,
                       Double longitude){
        this.userPhoneNumber = userPhoneNumber;
        this.company = company;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void changePassword(String newPassword){
        this.userPassword = newPassword;
    }

    public void rateUpdate(Double newAverageRate) {
        this.userRate = newAverageRate;
    }
}

