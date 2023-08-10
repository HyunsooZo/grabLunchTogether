package com.grablunchtogether.service.userReview;

import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.dto.userReview.UserReviewInput;

public interface UserReviewService {
    // 상대방에게 리뷰 추가
    ServiceResult addReview(Long userId, Long planHistoryId, UserReviewInput userReviewInput);

    // 작성된 리뷰 수정
    ServiceResult editReview(Long id, Long userReviewId, UserReviewInput userReviewEditInput);

    // 작성된 리뷰 삭제
    ServiceResult deleteReview(Long id, Long userReviewId);

    // 작성된 리뷰목록 조회
    ServiceResult listReviews(Long id, Long targetUserId);
}
