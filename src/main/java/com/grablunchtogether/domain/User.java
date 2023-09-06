package com.grablunchtogether.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.grablunchtogether.enums.UserRole;
import com.grablunchtogether.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@AuditOverride(forClass = BaseEntity.class)
@Entity
public class User extends BaseEntity {
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

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    public void update(String userPhoneNumber, String company, Double latitude,
                       Double longitude) {
        this.userPhoneNumber = userPhoneNumber;
        this.company = company;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setPassword(String newPassword) {
        this.userPassword = newPassword;
    }

    public void setRate(Double newAverageRate) {
        this.userRate = newAverageRate;
    }

    public void setStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }
}

