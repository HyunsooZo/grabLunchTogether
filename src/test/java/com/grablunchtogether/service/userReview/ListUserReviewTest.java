package com.grablunchtogether.service.userReview;

import com.grablunchtogether.domain.Plan;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.domain.UserReview;
import com.grablunchtogether.dto.UserReviewDto;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.repository.UserRepository;
import com.grablunchtogether.repository.UserReviewRepository;
import com.grablunchtogether.service.UserReviewService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("리뷰 불러오기")
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
    @DisplayName("성공")
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

        List<UserReviewDto.Dto> collect = userReviews.stream()
                .map(UserReviewDto.Dto::of)
                .collect(Collectors.toList());

        Mockito.when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));
        Mockito.when(userReviewRepository.findByTargetedId(targetUser)).thenReturn(userReviews);

        // When
        List<UserReviewDto.Dto> reviews = userReviewService.listReviews(targetUserId);

        // Then
        Assertions.assertThat(reviews.get(0).getReviewContent())
                .isEqualTo(collect.get(0).getReviewContent());
    }

    @Test
    @DisplayName("실패(고객정보없음)")
    public void listReviews_Fail_UserInfoNotFound() {
        // Given
        Long targetUserId = 1L;

        Mockito.when(userRepository.findById(targetUserId)).thenReturn(Optional.empty());

        // When, Then
        assertThatThrownBy(() -> userReviewService.listReviews(targetUserId))
                .isInstanceOf(CustomException.class)
                .hasMessage("회원정보를 찾을 수 없습니다.");
    }
}
