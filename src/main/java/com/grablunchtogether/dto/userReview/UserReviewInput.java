package com.grablunchtogether.dto.userReview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserReviewInput {

    @NotBlank(message = "내용을 입력해주세요.")
    private String reviewContent;

    @Min(value = 0, message = "0 이상의 값을 입력해주세요.")
    @Max(value = 5, message = "5 이하의 값을 입력해주세요.")
    private Double rate;
}
