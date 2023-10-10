package com.grablunchtogether.service.bookmarkSpot;

import com.grablunchtogether.domain.BookmarkSpot;
import com.grablunchtogether.domain.MustEatPlace;
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

@DisplayName("맛집정보로 북마크 추가")
class RegisterBookmarkSpotWithMustEatPlaceTest {
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
    public void addBookmarkSpotWMustEatPlace_Success() {
        //given
        User user = User.builder()
                .id(1L)
                .build();

        MustEatPlace mustEatPlace = MustEatPlace.builder()
                .id(1L)
                .restaurant("테스트 식당")
                .menu("테스트메뉴")
                .rate("5.0")
                .operationHour("테스트시간")
                .address("테스트주소")
                .city("서울")
                .build();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(mustEatPlaceRepository.findById(mustEatPlace.getId()))
                .thenReturn(Optional.of(mustEatPlace));

        //when
        bookmarkSpotService.registerBookmarkWithMustEatPlace(mustEatPlace.getId(), user.getId());

        //then
        verify(bookmarkSpotRepository, times(1)).save(any(BookmarkSpot.class));
    }

    @Test
    @DisplayName(value = "실패(회원정보없음)")
    public void addBookmarkSpotWMustEatPlace_Fail_UserNotFound() {
        //given
        User user = User.builder()
                .id(1L)
                .build();

        MustEatPlace mustEatPlace = MustEatPlace.builder()
                .id(1L)
                .restaurant("테스트 식당")
                .menu("테스트메뉴")
                .rate("5.0")
                .operationHour("테스트시간")
                .address("테스트주소")
                .city("서울")
                .build();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());
        //when,then
        assertThatThrownBy(() -> bookmarkSpotService
                .registerBookmarkWithMustEatPlace(mustEatPlace.getId(), user.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("회원정보를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName(value = "실패(맛집정보없음)")
    public void addBookmarkSpotWMustEatPlace_Fail_SpotNotFound() {
        //given
        User user = User.builder()
                .id(1L)
                .build();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(mustEatPlaceRepository.findById(1L))
                .thenReturn(Optional.empty());
        //when,then
        assertThatThrownBy(() -> bookmarkSpotService
                .registerBookmarkWithMustEatPlace(1L, user.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("존재하지 않는 맛집정보입니다.");
    }
}