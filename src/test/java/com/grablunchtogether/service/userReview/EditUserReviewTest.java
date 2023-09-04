package com.grablunchtogether.service.userReview;

import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.Plan;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.domain.UserReview;
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

class EditUserReviewTest {
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
    public void editReview_Success() {
        // Given
        Long userId = 1L;
        Long userReviewId = 1L;
        UserReviewInput userReviewEditInput = new UserReviewInput();
        userReviewEditInput.setRate(4.0);
        userReviewEditInput.setReviewContent("Updated review content");

        User user = User.builder().id(userId).build();
        UserReview userReview = UserReview.builder()
                .id(userReviewId)
                .reviewerId(user)
                .rate(3.5)
                .reviewContent("Initial review content")
                .build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userReviewRepository.findById(userReviewId)).thenReturn(Optional.of(userReview));

        // When
        ServiceResult result = userReviewService.editReview(userId, userReviewId, userReviewEditInput);

        // Then
        assertThat(result.isResult()).isTrue();
        assertThat(result.getMessage()).isEqualTo("리뷰 수정이 완료되었습니다.");
        assertThat(userReview.getRate()).isEqualTo(3.5);
        assertThat(userReview.getReviewContent()).isEqualTo("Updated review content");
    }

    @Test
    public void editReview_Fail_UserInfoNotFound() {
        // Given
        Long userId = 1L;
        Long userReviewId = 1L;
        UserReviewInput userReviewEditInput = new UserReviewInput();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When, Then
        assertThatThrownBy(() -> userReviewService.editReview(userId, userReviewId, userReviewEditInput))
                .isInstanceOf(UserInfoNotFoundException.class)
                .hasMessage("고객정보를 찾을 수 없습니다. 다시 시도해 주세요.");
    }

    @Test
    public void editReview_Fail_ContentNotFound() {
        // Given
        Long userId = 1L;
        Long userReviewId = 1L;
        UserReviewInput userReviewEditInput = new UserReviewInput();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        Mockito.when(userReviewRepository.findById(userReviewId)).thenReturn(Optional.empty());

        // When, Then
        assertThatThrownBy(() -> userReviewService.editReview(userId, userReviewId, userReviewEditInput))
                .isInstanceOf(ContentNotFoundException.class)
                .hasMessage("존재하지 않는 리뷰입니다.");
    }

    @Test
    public void editReview_Fail_AuthorityException() {
        // Given
        UserReviewInput userReviewEditInput = UserReviewInput.builder()
                .reviewContent("s").build();

        User user = User.builder().id(2L).build();
        UserReview userReview = UserReview.builder()
                .id(1L)
                .reviewerId(User.builder().id(4L).build())
                .targetedId(user)
                .planId(Plan.builder().requester(user).accepter(user).build())
                .rate(3.5)
                .reviewContent("Initial review content")
                .build();

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userReviewRepository.findById(1L)).thenReturn(Optional.of(userReview));

        // When, Then
        assertThatThrownBy(() -> userReviewService.editReview(user.getId(), 1L, userReviewEditInput))
                .isInstanceOf(AuthorityException.class)
                .hasMessage("본인이 작성한 리뷰만 수정할 수 있습니다.");
    }
}