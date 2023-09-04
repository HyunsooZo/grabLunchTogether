package com.grablunchtogether.service.bookmarkSpot;

import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.BookmarkSpot;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.bookmarkSpot.BookmarkSpotDto;
import com.grablunchtogether.repository.BookmarkSpotRepository;
import com.grablunchtogether.repository.MustEatPlaceRepository;
import com.grablunchtogether.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

        List<BookmarkSpotDto> listDto = new ArrayList<>();
        listDto.add(BookmarkSpotDto.of(bookmarkSpot1));
        listDto.add(BookmarkSpotDto.of(bookmarkSpot2));

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        Mockito.when(bookmarkSpotRepository.findByUserId(user))
                .thenReturn(list);

        //when
        ServiceResult result = bookmarkSpotService.listBookmarkSpot(user.getId());

        //then
        assertThat(result.isResult()).isTrue();
        assertThat(result.getMessage()).isEqualTo("목록가져오기 성공");
        assertThat(result.getObject()).isEqualTo(listDto);
    }

    @Test
    public void listBookmarkSpot_Fail_ContentNotFound() {
        //given
        User user = User.builder().id(1L).build();

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        Mockito.when(bookmarkSpotRepository.findByUserId(user))
                .thenReturn(new ArrayList<>());

        //when,then
        assertThatThrownBy(() -> bookmarkSpotService.listBookmarkSpot(user.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("즐겨찾기에 등록된 장소가 없습니다.");
    }

    @Test
    public void listBookmarkSpot_Fail_UserNotFound() {
        //given
        User user = User.builder().id(1L).build();

        Mockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());

        //when,then
        assertThatThrownBy(() -> bookmarkSpotService.listBookmarkSpot(user.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("고객정보를 찾을 수 없습니다.");
    }
}