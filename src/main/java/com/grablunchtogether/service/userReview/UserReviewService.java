package com.grablunchtogether.service.userReview;

import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.PlanHistory;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.domain.UserReview;
import com.grablunchtogether.dto.userReview.UserReviewDto;
import com.grablunchtogether.dto.userReview.UserReviewInput;
import com.grablunchtogether.repository.PlanHistoryRepository;
import com.grablunchtogether.repository.UserRepository;
import com.grablunchtogether.repository.UserReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.grablunchtogether.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class UserReviewService {
    private final UserRepository userRepository;
    private final PlanHistoryRepository planHistoryRepository;
    private final UserReviewRepository userReviewRepository;

    @Transactional
    public ServiceResult addReview(Long userId, Long planHistoryId,
                                   UserReviewInput userReviewInput) {

        PlanHistory planHistory = planHistoryRepository.findById(planHistoryId)
                .orElseThrow(() -> new CustomException(CONTENT_NOT_FOUND));

        User targetUser = null;

        if (Objects.equals(planHistory.getAccepterId().getId(), userId)) {
            targetUser = planHistory.getRequesterId();
        } else if (Objects.equals(planHistory.getRequesterId().getId(), userId)) {
            targetUser = planHistory.getAccepterId();
        } else {
            throw new CustomException(NOT_PERMITTED);
        }

        Double newAverageRate =
                calculateAverageRate(targetUser, userReviewInput.getRate());

        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(USER_INFO_NOT_FOUND));

        Optional<UserReview> optionalUserReview =
                userReviewRepository.findByPlanIdAndReviewerId(planHistory.getPlanId(), user);
        if (optionalUserReview.isPresent()) {
            throw new CustomException(USER_REVIEW_ALREADY_EXISTS);
        }

        targetUser.rateUpdate(newAverageRate);

        userReviewRepository.save(
                UserReview.builder()
                        .reviewerId(user)
                        .targetedId(targetUser)
                        .planId(planHistory.getPlanId())
                        .reviewContent(userReviewInput.getReviewContent())
                        .rate(userReviewInput.getRate())
                        .build()
        );

        return ServiceResult.success("리뷰등록이 완료되었습니다.");
    }

    @Transactional
    public ServiceResult editReview(Long userId,
                                    Long userReviewId,
                                    UserReviewInput userReviewEditInput) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(USER_INFO_NOT_FOUND));

        UserReview userReview = userReviewRepository.findById(userReviewId).orElseThrow(
                () -> new CustomException(CONTENT_NOT_FOUND));

        if (!userReview.getReviewerId().equals(user)) {
            throw new CustomException(NOT_PERMITTED);
        }

        userReview.edit(userReviewEditInput);

        userReviewRepository.save(userReview);

        return ServiceResult.success("리뷰 수정이 완료되었습니다.");
    }

    @Transactional
    public ServiceResult deleteReview(Long userId, Long userReviewId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        UserReview userReview = userReviewRepository.findById(userReviewId)
                .orElseThrow(() -> new CustomException(CONTENT_NOT_FOUND));

        if (!userReview.getReviewerId().equals(user)) {
            throw new CustomException(NOT_PERMITTED);
        }

        userReviewRepository.delete(userReview);

        return ServiceResult.success("리뷰 삭제가 완료되었습니다.");
    }

    @Transactional(readOnly = true)
    public ServiceResult listReviews(Long targetUserId) {

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        List<UserReview> list = userReviewRepository.findByTargetedId(user);

        if (list.isEmpty()) {
            throw new CustomException(CONTENT_NOT_FOUND);
        }

        List<UserReviewDto> result = new ArrayList<>();

        list.forEach(userReview -> {
            result.add(UserReviewDto.of(userReview));
        });
        return ServiceResult.success("목록 조회 성공", result);
    }

    private Double calculateAverageRate(User targetUser, Double newRate) {

        User target = userRepository.findById(targetUser.getId())
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        List<UserReview> userReview = userReviewRepository.findByTargetedId(target);

        if (userReview.size() == 0) {
            return newRate;
        }

        Double previousAverageRate = targetUser.getUserRate();

        return ((previousAverageRate * userReview.size()) + newRate) / (userReview.size() + 1);
    }
}
