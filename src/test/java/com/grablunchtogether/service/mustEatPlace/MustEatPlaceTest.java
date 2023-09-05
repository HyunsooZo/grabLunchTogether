package com.grablunchtogether.service.mustEatPlace;

import com.grablunchtogether.domain.MustEatPlace;
import com.grablunchtogether.dto.mustEatPlace.MustEatPlaceDto;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.repository.MustEatPlaceRepository;
import com.grablunchtogether.service.MustEatPlaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@DisplayName("맛집정보 불러오기")
class MustEatPlaceTest {
    @Mock
    private MustEatPlaceRepository mustEatPlaceRepository;

    private MustEatPlaceService mustEatPlaceService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mustEatPlaceService =
                new MustEatPlaceService(mustEatPlaceRepository);
    }

    @Test
    @DisplayName("성공")
    public void testMustEatPlaceList_Success() {
        //given
        String city = "서울";
        MustEatPlace mustEatPlace1 = MustEatPlace.builder()
                .restaurant("restaurant")
                .menu("menu")
                .address("address")
                .rate("rate")
                .operationHour("operationTime")
                .city(city)
                .build();
        MustEatPlace mustEatPlace2 = MustEatPlace.builder()
                .restaurant("restaurant2")
                .menu("menu2")
                .address("address2")
                .rate("rate2")
                .operationHour("operationTime2")
                .city(city)
                .build();

        List<MustEatPlace> listFromRepo = Arrays.asList(mustEatPlace1,mustEatPlace2);

        List<MustEatPlaceDto.Dto> collect = listFromRepo.stream()
                .map(MustEatPlaceDto.Dto::of)
                .collect(Collectors.toList());

        Mockito.when(mustEatPlaceRepository.findByCityOrderByRateDesc(city))
                .thenReturn(listFromRepo);
        //when
        List<MustEatPlaceDto.Dto> result = mustEatPlaceService.mustEatPlaceList(city);
        //then
        assertThat(result.get(1).getId()).isEqualTo(collect.get(1).getId());
    }

    @Test
    @DisplayName("성공")
    public void testGetMustEatPlaceTest_Success(){
        //given
        Long id = 1L;

        MustEatPlace mustEatPlace = MustEatPlace.builder()
                .restaurant("restaurant")
                .menu("menu")
                .address("address")
                .rate("rate")
                .operationHour("operationTime")
                .city("city")
                .build();

        Mockito.when(mustEatPlaceRepository.findById(id))
                .thenReturn(Optional.of(mustEatPlace));
        //when
        MustEatPlaceDto.Dto mustEatPlace1 = mustEatPlaceService.getMustEatPlace(id);
        //then
        assertThat(mustEatPlace1).isInstanceOf(MustEatPlaceDto.Dto.class);
    }

    @Test
    @DisplayName("실패")
    public void testGetMustEatPlaceTest_Fail(){
        //given
        Long id = 1L;
        //when,then
        assertThatThrownBy(()->mustEatPlaceService.getMustEatPlace(id))
                .isInstanceOf(CustomException.class)
                .hasMessage("존재하지 않는 맛집정보입니다.");
    }
}