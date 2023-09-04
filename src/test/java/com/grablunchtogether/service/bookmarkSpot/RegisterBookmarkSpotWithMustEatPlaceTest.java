package com.grablunchtogether.service.bookmarkSpot;

import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.MustEatPlace;
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

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Mockito.when(mustEatPlaceRepository.findById(mustEatPlace.getId()))
                .thenReturn(Optional.of(mustEatPlace));

        //when
        ServiceResult result =
                bookmarkSpotService.registerBookmarkWithMustEatPlace(mustEatPlace.getId(), user.getId());

        //then
        assertThat(result.isResult()).isTrue();
        assertThat(result.getMessage()).isEqualTo("맛집 즐겨찾기 등록 완료");
    }

    @Test
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

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());
        //when,then
        assertThatThrownBy(() -> bookmarkSpotService
                .registerBookmarkWithMustEatPlace(mustEatPlace.getId(), user.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("고객정보를 찾을 수 없습니다.");
    }

    @Test
    public void addBookmarkSpotWMustEatPlace_Fail_SpotNotFound() {
        //given
        User user = User.builder()
                .id(1L)
                .build();

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Mockito.when(mustEatPlaceRepository.findById(1L))
                .thenReturn(Optional.empty());
        //when,then
        assertThatThrownBy(() -> bookmarkSpotService
                .registerBookmarkWithMustEatPlace(1L, user.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("등록되지 않은 맛집정보입니다.");
    }
}