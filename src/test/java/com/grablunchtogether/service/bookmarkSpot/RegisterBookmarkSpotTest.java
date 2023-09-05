package com.grablunchtogether.service.bookmarkSpot;

import com.grablunchtogether.domain.BookmarkSpot;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.bookmarkSpot.BookmarkSpotDto;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.repository.BookmarkSpotRepository;
import com.grablunchtogether.repository.MustEatPlaceRepository;
import com.grablunchtogether.repository.UserRepository;
import com.grablunchtogether.service.BookmarkSpotService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;

@DisplayName("북마크 맛집 추가")
class RegisterBookmarkSpotTest {
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
    @DisplayName(value = "성공")
    public void addBookmarkSpot_Success() {
        //given
        User user = User.builder()
                .id(1L)
                .build();

        BookmarkSpotDto.Request input = BookmarkSpotDto.Request.builder()
                .restaurant("테스트 식당")
                .menu("테스트메뉴")
                .rate("5.0")
                .operationHour("테스트시간")
                .address("테스트주소")
                .build();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        //when
        bookmarkSpotService.registerBookmark(input, user.getId());
        //then
        verify(bookmarkSpotRepository, times(1)).save(any(BookmarkSpot.class));
    }

    @Test
    @DisplayName(value = "실패(고객정보없음)")
    public void addBookmarkSpot_Fail() {
        //given
        User user = User.builder()
                .id(1L)
                .build();

        BookmarkSpotDto.Request input = BookmarkSpotDto.Request.builder()
                .restaurant("테스트 식당")
                .menu("테스트메뉴")
                .rate("5.0")
                .operationHour("테스트시간")
                .address("테스트주소")
                .build();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());
        //when,then
        Assertions.assertThatThrownBy(
                        () -> bookmarkSpotService.registerBookmark(input, user.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("회원정보를 찾을 수 없습니다.");
    }
}