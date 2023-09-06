package com.grablunchtogether.service;

import com.grablunchtogether.domain.PlanHistory;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.domain.UserReview;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.repository.PlanHistoryRepository;
import com.grablunchtogether.repository.UserRepository;
import com.grablunchtogether.repository.UserReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.grablunchtogether.dto.userReview.UserReviewDto.Dto;
import static com.grablunchtogether.dto.userReview.UserReviewDto.UserReviewRequest;
import static com.grablunchtogether.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class UserReviewService {
    private final UserRepository userRepository;
    private final PlanHistoryRepository planHistoryRepository;
    private final UserReviewRepository userReviewRepository;

    @Transactional
    public void addReview(Long userId, Long planHistoryId,
                          UserReviewRequest userReviewRequest) {

        PlanHistory planHistory = planHistoryRepository.findById(planHistoryId)
                .orElseThrow(() -> new CustomException(PLAN_HISTORY_NOT_FOUND));

        User targetUser = null;

        if (Objects.equals(planHistory.getAccepterId().getId(), userId)) {
            targetUser = planHistory.getRequesterId();
        } else if (Objects.equals(planHistory.getRequesterId().getId(), userId)) {
            targetUser = planHistory.getAccepterId();
        } else {
            throw new CustomException(NOT_PERMITTED);
        }

        Double newAverageRate =
                calculateAverageRate(targetUser, userReviewRequest.getRate());

        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(USER_INFO_NOT_FOUND));

        Optional<UserReview> optionalUserReview =
                userReviewRepository.findByPlanIdAndReviewerId(planHistory.getPlanId(), user);

        if (optionalUserReview.isPresent()) {
            throw new CustomException(USER_REVIEW_ALREADY_EXISTS);
        }

        targetUser.setRate(newAverageRate);

        userReviewRepository.save(
                UserReview.builder()
                        .reviewerId(user)
                        .targetedId(targetUser)
                        .planId(planHistory.getPlanId())
                        .reviewContent(userReviewRequest.getReviewContent())
                        .rate(userReviewRequest.getRate())
                        .build()
        );
    }

    @Transactional
    public void editReview(Long userId,
                           Long userReviewId,
                           UserReviewRequest userReviewEditInput) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        UserReview userReview = userReviewRepository.findById(userReviewId)
                .orElseThrow(() -> new CustomException(USER_REVIEW_NOT_FOUND));

        if (!userReview.getReviewerId().equals(user)) {
            throw new CustomException(NOT_PERMITTED);
        }

        userReview.edit(userReviewEditInput);

        userReviewRepository.save(userReview);
    }

    @Transactional
    public void deleteReview(Long userId, Long userReviewId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        UserReview userReview = userReviewRepository.findById(userReviewId)
                .orElseThrow(() -> new CustomException(USER_REVIEW_NOT_FOUND));

        if (!userReview.getReviewerId().equals(user)) {
            throw new CustomException(NOT_PERMITTED);
        }

        userReviewRepository.delete(userReview);
    }

    @Transactional(readOnly = true)
    public List<Dto> listReviews(Long targetUserId) {

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        List<UserReview> list = userReviewRepository.findByTargetedId(user);

        return list.stream()
                .map(Dto::of)
                .collect(Collectors.toList());
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
