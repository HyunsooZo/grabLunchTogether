package com.grablunchtogether.domain;

import com.grablunchtogether.dto.userReview.UserReviewInput;
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
public class UserReview {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reviewerId", referencedColumnName = "id")
    private User reviewerId;

    @ManyToOne
    @JoinColumn(name = "targetedId", referencedColumnName = "id")
    private User targetedId;

    @ManyToOne
    @JoinColumn(name = "planId", referencedColumnName = "id")
    private Plan planId;

    @Column
    private String reviewContent;

    @Column
    private Double rate;

    @Column
    private LocalDateTime registeredAt;

    @Column
    private LocalDateTime updatedAt;

    public void edit(UserReviewInput userReviewEditInput){
        this.reviewContent = userReviewEditInput.getReviewContent();
        this.updatedAt = LocalDateTime.now();
    }
}