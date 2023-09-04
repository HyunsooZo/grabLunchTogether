package com.grablunchtogether.service.mustEatPlace;

import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.MustEatPlace;
import com.grablunchtogether.dto.mustEatPlace.MustEatPlaceDto;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.repository.MustEatPlaceRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        List<MustEatPlaceDto> listDto = new ArrayList<>();
        listDto.add(MustEatPlaceDto.of(mustEatPlace1));
        listDto.add(MustEatPlaceDto.of(mustEatPlace2));

        List<MustEatPlace> listFromRepo = new ArrayList<>();
        listFromRepo.add(mustEatPlace1);
        listFromRepo.add(mustEatPlace2);

        Mockito.when(mustEatPlaceRepository.findByCityOrderByRateDesc(city))
                .thenReturn(listFromRepo);
        //when
        ServiceResult result = mustEatPlaceService.mustEatPlaceList(city);
        //then
        Assertions.assertThat(result.getObject()).isEqualTo(listDto);
        Assertions.assertThat((List<MustEatPlaceDto>) result.getObject())
                .size().isEqualTo(2);
    }

    @Test
    public void testMustEatPlaceList_Fail_CityNotRegistered() {
        //given
        String city = "애리조나";

        //when,then
        Assertions.assertThatThrownBy(() -> mustEatPlaceService.mustEatPlaceList(city))
                .isInstanceOf(CustomException.class)
                .hasMessage("해당 지역에 대한 맛집 정보가 등록되어있지 않습니다.");
    }

    @Test
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
        MustEatPlaceDto result = mustEatPlaceService.getMustEatPlace(id);
        //then
        Assertions.assertThat(result).isEqualTo(MustEatPlaceDto.of(mustEatPlace));
    }

    @Test
    public void testGetMustEatPlaceTest_Fail(){
        //given
        Long id = 1L;
        //when,then
        Assertions.assertThatThrownBy(()->mustEatPlaceService.getMustEatPlace(id))
                .isInstanceOf(CustomException.class)
                .hasMessage("존재하지 않는 맛집정보입니다.");
    }
}