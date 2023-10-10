package com.grablunchtogether.service.bookmarkSpot;

import com.grablunchtogether.domain.BookmarkSpot;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.bookmarkSpot.BookmarkSpotDto;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.repository.BookmarkSpotRepository;
import com.grablunchtogether.repository.MustEatPlaceRepository;
import com.grablunchtogether.repository.UserRepository;
import com.grablunchtogether.service.BookmarkSpotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DisplayName("북마크 맛집 리스트 조회")
class ListBookmarkSpotTest {
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
    public void listBookmarkSpot_Success() {
        //given
        User user = User.builder().id(1L).build();

        BookmarkSpot bookmarkSpot1 = BookmarkSpot.builder()
                .id(1L)
                .userId(user)
                .restaurant("테스트 식당")
                .build();
        BookmarkSpot bookmarkSpot2 = BookmarkSpot.builder()
                .id(2L)
                .userId(user)
                .restaurant("테스트 식당")
                .build();

        List<BookmarkSpot> list = new ArrayList<>();
        list.add(bookmarkSpot1);
        list.add(bookmarkSpot2);

        List<BookmarkSpotDto.Dto> listDto = new ArrayList<>();
        listDto.add(BookmarkSpotDto.Dto.of(bookmarkSpot1));
        listDto.add(BookmarkSpotDto.Dto.of(bookmarkSpot2));

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        Mockito.when(bookmarkSpotRepository.findByUserId(user))
                .thenReturn(list);

        //when
        List<BookmarkSpotDto.Dto> dtos = bookmarkSpotService.listBookmarkSpot(user.getId());

        //then
        assertThat(dtos).isEqualTo(dtos);
    }

    @Test
    @DisplayName("실패(고객정보없음)")
    public void listBookmarkSpot_Fail_UserNotFound() {
        //given
        User user = User.builder().id(1L).build();

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());

        //when,then
        assertThatThrownBy(() -> bookmarkSpotService.listBookmarkSpot(user.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("회원정보를 찾을 수 없습니다.");
    }
}