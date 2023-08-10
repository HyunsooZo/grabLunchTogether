package com.grablunchtogether.service.userReview;

import com.grablunchtogether.common.exception.AuthorityException;
import com.grablunchtogether.common.exception.ContentNotFoundException;
import com.grablunchtogether.common.exception.UserInfoNotFoundException;
import com.grablunchtogether.common.exception.UserReviewAlreadyExistsException;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.PlanHistory;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.domain.UserReview;
import com.grablunchtogether.dto.userReview.UserReviewInput;
import com.grablunchtogether.repository.PlanHistoryRepository;
import com.grablunchtogether.repository.UserRepository;
import com.grablunchtogether.repository.UserReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {
    private final UserRepository userRepository;
    private final PlanHistoryRepository planHistoryRepository;
    private final UserReviewRepository userReviewRepository;

    @Override
    @Transactional
    public ServiceResult addReview(Long userId, Long planHistoryId,
                                   UserReviewInput userReviewInput) {

        PlanHistory planHistory = planHistoryRepository.findById(planHistoryId)
                .orElseThrow(() -> new ContentNotFoundException("히스토리가 존재하지 않습니다."));

        User targetUser = null;

        if (Objects.equals(planHistory.getAccepterId().getId(), userId)) {
            targetUser = planHistory.getRequesterId();
        } else if (Objects.equals(planHistory.getRequesterId().getId(), userId)) {
            targetUser = planHistory.getAccepterId();
        } else {
            throw new AuthorityException("본인이 참석한 점심약속 대상에 대해서만 리뷰를 남길 수 있습니다.");
        }

        Double newAverageRate =
                calculateAverageRate(targetUser, userReviewInput.getRate());

        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserInfoNotFoundException("고객정보를 찾을 수 없습니다. 다시 시도해 주세요."));

        Optional<UserReview> optionalUserReview =
                userReviewRepository.findByPlanIdAndReviewerId(planHistory.getPlanId(), user);
        if (optionalUserReview.isPresent()) {
            throw new UserReviewAlreadyExistsException("이미 해당 점심약속에 대해 남긴 리뷰가 존재합니다.");
        }

        userReviewRepository.save(
                UserReview.builder()
                        .reviewerId(user)
                        .targetedId(targetUser)
                        .planId(planHistory.getPlanId())
                        .reviewContent(userReviewInput.getReviewContent())
                        .rate(newAverageRate)
                        .registeredAt(LocalDateTime.now())
                        .build()
        );

        return ServiceResult.success("리뷰등록이 완료되었습니다.");
    }

    private Double calculateAverageRate(User targetUser, Double newRate) {

        User target = userRepository.findById(targetUser.getId()).orElseThrow(
                () -> new UserInfoNotFoundException("리뷰대상 고객 정보를 찾을 수 없습니다.")
        );


        List<UserReview> userReview = userReviewRepository.findByTargetedId(target);

        if (userReview.size() == 0) {
            return newRate;
        }

        Double previousAverageRate = targetUser.getUserRate();

        return ((previousAverageRate * userReview.size()) + newRate) / (userReview.size() + 1);
    }
}
