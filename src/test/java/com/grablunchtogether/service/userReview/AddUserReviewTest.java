package com.grablunchtogether.service.userReview;

import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.Plan;
import com.grablunchtogether.domain.PlanHistory;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.userReview.UserReviewInput;
import com.grablunchtogether.repository.PlanHistoryRepository;
import com.grablunchtogether.repository.UserRepository;
import com.grablunchtogether.repository.UserReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class AddUserReviewTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PlanHistoryRepository planHistoryRepository;

    @Mock
    private UserReviewRepository userReviewRepository;

    private UserReviewService userReviewService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userReviewService = new UserReviewService(userRepository, planHistoryRepository, userReviewRepository);
    }

    @Test
    public void addReview_Success() {
        // Given
        Long planHistoryId = 1L;
        UserReviewInput userReviewInput = new UserReviewInput();
        userReviewInput.setRate(4.5);
        userReviewInput.setReviewContent("Great experience!");

        User requester = User.builder().id(2L).userRate(4.0).build();
        User accepter = User.builder().id(3L).userRate(3.8).build();

        PlanHistory planHistory = PlanHistory.builder()
                .id(planHistoryId)
                .requesterId(requester)
                .accepterId(accepter)
                .planId(Plan.builder()
                        .requester(requester)
                        .accepter(accepter)
                        .build())
                .build();

        Mockito.when(planHistoryRepository.findById(planHistoryId)).thenReturn(Optional.of(planHistory));
        Mockito.when(userRepository.findById(accepter.getId())).thenReturn(Optional.of(accepter));
        Mockito.when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));

        // When
        ServiceResult result = userReviewService.addReview(accepter.getId(), planHistoryId, userReviewInput);

        // Then
        assertThat(result.isResult()).isTrue();
        assertThat(result.getMessage()).isEqualTo("리뷰등록이 완료되었습니다.");
    }

    @Test
    public void addReview_Fail_ContentNotFound() {
        // Given
        Long userId = 1L;
        Long planHistoryId = 1L;
        UserReviewInput userReviewInput = new UserReviewInput();
        userReviewInput.setRate(4.5);
        userReviewInput.setReviewContent("Great experience!");

        Mockito.when(planHistoryRepository.findById(planHistoryId)).thenReturn(Optional.empty());

        // When, Then
        assertThatThrownBy(() -> userReviewService.addReview(userId, planHistoryId, userReviewInput))
                .isInstanceOf(ContentNotFoundException.class)
                .hasMessage("히스토리가 존재하지 않습니다.");
    }

    @Test
    public void addReview_Fail_AuthorityException() {
        // Given
        Long userId = 1L;
        Long planHistoryId = 1L;
        UserReviewInput userReviewInput = new UserReviewInput();
        userReviewInput.setRate(4.5);
        userReviewInput.setReviewContent("Great experience!");

        User requester = User.builder().id(2L).userRate(4.0).build();
        User accepter = User.builder().id(3L).userRate(3.8).build();

        PlanHistory planHistory = PlanHistory.builder()
                .id(planHistoryId)
                .requesterId(requester)
                .accepterId(accepter)
                .planId(new Plan())
                .build();

        Mockito.when(planHistoryRepository.findById(planHistoryId)).thenReturn(Optional.of(planHistory));

        // When, Then
        assertThatThrownBy(() -> userReviewService.addReview(userId, planHistoryId, userReviewInput))
                .isInstanceOf(AuthorityException.class)
                .hasMessage("본인이 참석한 점심약속 대상에 대해서만 리뷰를 남길 수 있습니다.");
    }
}
