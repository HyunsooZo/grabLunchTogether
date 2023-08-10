package com.grablunchtogether.dto.userReview;

import com.grablunchtogether.domain.UserReview;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserReviewDto {
    private Long id;
    private Long reviewerId;
    private Long targetedId;
    private String reviewerEmail;
    private Long planId;
    private String reviewContent;
    private Double rate;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;

    public static UserReviewDto of(UserReview userReview) {
        return UserReviewDto.builder()
                .id(userReview.getId())
                .reviewerId(userReview.getReviewerId().getId())
                .targetedId(userReview.getTargetedId().getId())
                .reviewerEmail(userReview.getReviewerId().getUserEmail())
                .planId(userReview.getPlanId().getId())
                .reviewContent(userReview.getReviewContent())
                .rate(userReview.getRate())
                .registeredAt(userReview.getRegisteredAt())
                .updatedAt(userReview.getUpdatedAt())
                .build();
    }
}
