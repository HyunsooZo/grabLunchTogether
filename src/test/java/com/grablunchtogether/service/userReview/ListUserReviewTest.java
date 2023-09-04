package com.grablunchtogether.service.userReview;

import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.Plan;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.domain.UserReview;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.repository.UserRepository;
import com.grablunchtogether.repository.UserReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ListUserReviewTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserReviewRepository userReviewRepository;

    private UserReviewService userReviewService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userReviewService = new UserReviewService(userRepository, null, userReviewRepository);
    }

    @Test
    public void listReviews_Success() {
        // Given
        Long targetUserId = 1L;
        User targetUser = User.builder().id(targetUserId).build();
        User user = User.builder().id(2L).build();

        List<UserReview> userReviews = new ArrayList<>();
        userReviews.add(
                UserReview.builder()
                        .id(1L)
                        .reviewerId(user)
                        .targetedId(targetUser)
                        .planId(new Plan())
                        .rate(4.0)
                        .reviewContent("Good")
                        .build());
        userReviews.add(
                UserReview.builder()
                        .id(1L)
                        .reviewerId(user)
                        .targetedId(targetUser)
                        .planId(new Plan())
                        .rate(4.0)
                        .reviewContent("Good")
                        .build());

        Mockito.when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));
        Mockito.when(userReviewRepository.findByTargetedId(targetUser)).thenReturn(userReviews);

        // When
        ServiceResult result = userReviewService.listReviews(targetUserId);

        // Then
        assertThat(result.isResult()).isTrue();
        assertThat(result.getMessage()).isEqualTo("목록 조회 성공");
        assertThat(result.getObject()).isInstanceOf(List.class);
        assertThat(((List<?>) result.getObject()).size()).isEqualTo(userReviews.size());
    }

    @Test
    public void listReviews_Fail_UserInfoNotFound() {
        // Given
        Long targetUserId = 1L;

        Mockito.when(userRepository.findById(targetUserId)).thenReturn(Optional.empty());

        // When, Then
        assertThatThrownBy(() -> userReviewService.listReviews(targetUserId))
                .isInstanceOf(CustomException.class)
                .hasMessage("고객정보가 존재하지 않습니다.");
    }

    @Test
    public void listReviews_Fail_ContentNotFound() {
        // Given
        Long targetUserId = 1L;
        User targetUser = User.builder().id(targetUserId).build();

        Mockito.when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));
        Mockito.when(userReviewRepository.findByTargetedId(targetUser)).thenReturn(new ArrayList<>());

        // When, Then
        assertThatThrownBy(() -> userReviewService.listReviews(targetUserId))
                .isInstanceOf(CustomException.class)
                .hasMessage("해당 유저에 대해 작성된 리뷰가 존재하지 않습니다.");
    }
}
