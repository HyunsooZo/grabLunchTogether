package com.grablunchtogether.service.bookmarkSpot;

import com.grablunchtogether.common.exception.UserInfoNotFoundException;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.bookmarkSpot.BookmarkSpotInput;
import com.grablunchtogether.repository.BookmarkSpotRepository;
import com.grablunchtogether.repository.MustEatPlaceRepository;
import com.grablunchtogether.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

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
        bookmarkSpotService = new BookmarkSpotServiceImpl(bookmarkSpotRepository,
                userRepository , mustEatPlaceRepository);
    }

    @Test
    public void addBookmarkSpot_Success() {
        //given
        User user = User.builder()
                .id(1L)
                .build();

        BookmarkSpotInput input = BookmarkSpotInput.builder()
                .restaurant("테스트 식당")
                .menu("테스트메뉴")
                .rate("5.0")
                .operationHour("테스트시간")
                .address("테스트주소")
                .build();

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        //when
        ServiceResult result = bookmarkSpotService.registerBookmark(input, user.getId());
        //then
        Assertions.assertThat(result.isResult()).isTrue();
        Assertions.assertThat(result.getMessage()).isEqualTo("맛집 즐겨찾기 등록 완료");
    }

    @Test
    public void addBookmarkSpot_Fail() {
        //given
        User user = User.builder()
                .id(1L)
                .build();

        BookmarkSpotInput input = BookmarkSpotInput.builder()
                .restaurant("테스트 식당")
                .menu("테스트메뉴")
                .rate("5.0")
                .operationHour("테스트시간")
                .address("테스트주소")
                .build();

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());
        //when,then
        Assertions.assertThatThrownBy(
                ()->bookmarkSpotService.registerBookmark(input, user.getId()))
                .isInstanceOf(UserInfoNotFoundException.class)
                .hasMessage("고객정보를 찾을 수 없습니다.");
    }
}