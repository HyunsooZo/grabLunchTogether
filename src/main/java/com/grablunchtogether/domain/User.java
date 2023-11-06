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
    private String email;

    @Column
    private String name;

    @JsonIgnore
    @Column
    private String password;

    @Column
    private String phoneNumber;

    @Column
    private Double rate;

    @Column
    private String company;

    @Column
    public String profileUrl;

    @Column
    public String nameCardUrl;

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
        this.phoneNumber = userPhoneNumber;
        this.company = company;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    public void setRate(Double newAverageRate) {
        this.rate = newAverageRate;
    }

    public void setStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public void setProfileUrl(String imageUrl) {
        this.profileUrl = imageUrl;
    }
}

