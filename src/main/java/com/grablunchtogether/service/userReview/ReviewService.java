package com.grablunchtogether.service.userReview;

import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.dto.userReview.UserReviewInput;

public interface ReviewService {
    // 상대방에게 리뷰 추가
    ServiceResult addReview(Long userId, Long planHistoryId, UserReviewInput userReviewInput);
}
