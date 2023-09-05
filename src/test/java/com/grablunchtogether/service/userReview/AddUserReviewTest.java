package com.grablunchtogether.service.userReview;

import com.grablunchtogether.domain.Plan;
import com.grablunchtogether.domain.PlanHistory;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.domain.UserReview;
import com.grablunchtogether.dto.userReview.UserReviewInput;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.repository.PlanHistoryRepository;
import com.grablunchtogether.repository.UserRepository;
import com.grablunchtogether.repository.UserReviewRepository;
import com.grablunchtogether.service.UserReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.times;

@DisplayName("리뷰 등록")
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
    @DisplayName("성공")
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
        userReviewService.addReview(accepter.getId(), planHistoryId, userReviewInput);

        // Then
        Mockito.verify(userReviewRepository, times(1))
                .save(Mockito.any(UserReview.class));
    }

    @Test
    @DisplayName("실패(약속실행된적없음)")
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
                .isInstanceOf(CustomException.class)
                .hasMessage("존재하지 않는 약속 히스토리 입니다.");
    }

    @Test
    @DisplayName("실패(권한없음)")
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
                .isInstanceOf(CustomException.class)
                .hasMessage("해당 데이터에 대한 접근 권한이 없습니다.");
    }
}
