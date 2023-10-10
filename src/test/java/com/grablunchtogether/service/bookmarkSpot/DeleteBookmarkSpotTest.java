package com.grablunchtogether.service.bookmarkSpot;

import com.grablunchtogether.domain.BookmarkSpot;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.repository.BookmarkSpotRepository;
import com.grablunchtogether.repository.MustEatPlaceRepository;
import com.grablunchtogether.repository.UserRepository;
import com.grablunchtogether.service.BookmarkSpotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("맛집북마크 삭제")
class DeleteBookmarkSpotTest {
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
        bookmarkSpotService = new BookmarkSpotService(bookmarkSpotRepository,
                userRepository, mustEatPlaceRepository);
    }

    @Test
    @DisplayName("성공")
    public void deleteBookmarkSpot_Success() {
        //given
        User user = User.builder().id(1L).build();
        BookmarkSpot bookmarkSpot = BookmarkSpot.builder()
                .id(1L)
                .userId(user)
                .restaurant("테스트")
                .build();

        when(bookmarkSpotRepository.findById(bookmarkSpot.getId()))
                .thenReturn(Optional.of(bookmarkSpot));
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        //when
        bookmarkSpotService.deleteBookmarkSpot(bookmarkSpot.getId(), user.getId());
        //then
        verify(bookmarkSpotRepository, times(1)).delete(bookmarkSpot);
    }

    @Test
    @DisplayName("실패(고객불일치)")
    public void deleteBookmarkSpot_Fail_UserNotMatch() {
        //given
        User user = User.builder().id(2L).build();
        User user2 = User.builder().id(3L).build();
        BookmarkSpot bookmarkSpot = BookmarkSpot.builder()
                .id(1L)
                .userId(user2)
                .restaurant("테스트")
                .build();

        when(bookmarkSpotRepository.findById(bookmarkSpot.getId()))
                .thenReturn(Optional.of(bookmarkSpot));
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        //when,then
        assertThatThrownBy(() -> bookmarkSpotService.deleteBookmarkSpot(bookmarkSpot.getId(), user.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("해당 데이터에 대한 접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("실패(북마크미존재)")
    public void deleteBookmarkSpot_Fail_ContentNotFound() {
        //given
        User user = User.builder().id(1L).build();
        BookmarkSpot bookmarkSpot = BookmarkSpot.builder()
                .id(1L)
                .userId(user)
                .restaurant("테스트")
                .build();

        when(bookmarkSpotRepository.findById(bookmarkSpot.getId()))
                .thenReturn(Optional.empty());
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        //when,then
        assertThatThrownBy(() -> bookmarkSpotService.deleteBookmarkSpot(bookmarkSpot.getId(), user.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("존재하지 않는 즐겨찾기 맛집정보입니다.");
    }
}
