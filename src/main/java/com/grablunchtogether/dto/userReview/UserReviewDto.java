package com.grablunchtogether.dto.userReview;

import com.grablunchtogether.domain.UserReview;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

public class UserReviewDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("회원리뷰 요청")
    public static class UserReviewRequest {

        @NotBlank(message = "내용을 입력해주세요.")
        private String reviewContent;

        @Min(value = 0, message = "0 이상의 값을 입력해주세요.")
        @Max(value = 5, message = "5 이하의 값을 입력해주세요.")
        private Double rate;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("회원리뷰 Dto")
    public static class Dto {
        private Long id;
        private Long reviewerId;
        private Long targetedId;
        private String reviewerEmail;
        private Long planId;
        private String reviewContent;
        private Double rate;
        private LocalDateTime registeredAt;
        private LocalDateTime updatedAt;

        public static Dto of(UserReview userReview) {
            return Dto.builder()
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

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("회원리뷰 응답")
    public static class Response {
        private List<Dto> reviews;

        public static Response from(List<Dto> reviews) {
            return Response.builder().reviews(reviews).build();
        }
    }
}
