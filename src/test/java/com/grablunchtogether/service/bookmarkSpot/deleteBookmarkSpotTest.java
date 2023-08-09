package com.grablunchtogether.service.bookmarkSpot;

import com.grablunchtogether.common.exception.AuthorityException;
import com.grablunchtogether.common.exception.ContentNotFoundException;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.BookmarkSpot;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.repository.BookmarkSpotRepository;
import com.grablunchtogether.repository.MustEatPlaceRepository;
import com.grablunchtogether.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class deleteBookmarkSpotTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookmarkSpotRepository bookmarkSpotRepository;
    @Mock
    private MustEatPlaceRepository mustEatPlaceRepository;

    private BookmarkSpotService bookmarkSpotService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        bookmarkSpotService = new BookmarkSpotServiceImpl(bookmarkSpotRepository,
                userRepository, mustEatPlaceRepository);
    }

    @Test
    public void deleteBookmarkSpot_Success() {
        //given
        User user = User.builder().id(1L).build();
        BookmarkSpot bookmarkSpot = BookmarkSpot.builder()
                .id(1L)
                .userId(user)
                .restaurant("테스트")
                .build();

        Mockito.when(bookmarkSpotRepository.findById(bookmarkSpot.getId()))
                .thenReturn(Optional.of(bookmarkSpot));
        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        //when
        ServiceResult result =
                bookmarkSpotService.deleteBookmarkSpot(bookmarkSpot.getId(), user.getId());
        //then
        assertThat(result.isResult()).isTrue();
        assertThat(result.getMessage()).isEqualTo("즐겨찾기 맛집을 삭제했습니다.");
    }

    @Test
    public void deleteBookmarkSpot_Fail_UserNotMatch() {
        //given
        User user = User.builder().id(2L).build();
        BookmarkSpot bookmarkSpot = BookmarkSpot.builder()
                .id(1L)
                .userId(user)
                .restaurant("테스트")
                .build();

        Mockito.when(bookmarkSpotRepository.findById(bookmarkSpot.getId()))
                .thenReturn(Optional.of(bookmarkSpot));
        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        //when,then
        assertThatThrownBy(() -> bookmarkSpotService.deleteBookmarkSpot(bookmarkSpot.getId(), user.getId()))
                .isInstanceOf(AuthorityException.class)
                .hasMessage("본인의 즐겨찾기 식당만 삭제할 수 있습니다.");
    }

    @Test
    public void deleteBookmarkSpot_Fail_ContentNotFound() {
        //given
        User user = User.builder().id(1L).build();
        BookmarkSpot bookmarkSpot = BookmarkSpot.builder()
                .id(1L)
                .userId(user)
                .restaurant("테스트")
                .build();

        Mockito.when(bookmarkSpotRepository.findById(bookmarkSpot.getId()))
                .thenReturn(Optional.empty());
        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        //when,then
        assertThatThrownBy(() -> bookmarkSpotService.deleteBookmarkSpot(bookmarkSpot.getId(), user.getId()))
                .isInstanceOf(ContentNotFoundException.class)
                .hasMessage("등록되지 않은 맛집정보입니다.");
    }
}
