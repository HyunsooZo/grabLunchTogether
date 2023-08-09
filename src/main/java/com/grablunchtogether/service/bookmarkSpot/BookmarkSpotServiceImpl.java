package com.grablunchtogether.service.bookmarkSpot;

import com.grablunchtogether.common.exception.ContentNotFoundException;
import com.grablunchtogether.common.exception.UserInfoNotFoundException;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.BookmarkSpot;
import com.grablunchtogether.domain.MustEatPlace;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.bookmarkSpot.BookmarkSpotInput;
import com.grablunchtogether.repository.BookmarkSpotRepository;
import com.grablunchtogether.repository.MustEatPlaceRepository;
import com.grablunchtogether.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class BookmarkSpotServiceImpl implements BookmarkSpotService {
    private final BookmarkSpotRepository bookmarkSpotRepository;
    private final UserRepository userRepository;
    private final MustEatPlaceRepository mustEatPlaceRepository;

    @Override
    @Transactional
    public ServiceResult registerBookmark(BookmarkSpotInput bookmarkSpotInput,
                                          Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserInfoNotFoundException("고객정보를 찾을 수 없습니다."));

        bookmarkSpotRepository.save(BookmarkSpot.builder()
                .userId(user)
                .restaurant(bookmarkSpotInput.getRestaurant())
                .menu(bookmarkSpotInput.getMenu())
                .address(bookmarkSpotInput.getAddress())
                .operationHour(bookmarkSpotInput.getOperationHour())
                .rate(bookmarkSpotInput.getRate())
                .registeredAt(LocalDateTime.now())
                .build());

        return ServiceResult.success("맛집 즐겨찾기 등록 완료");
    }

    @Override
    @Transactional
    public ServiceResult registerBookmarkWithMustEatPlace(Long mustEatPlaceId,
                                                          Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserInfoNotFoundException("고객정보를 찾을 수 없습니다."));

        MustEatPlace mustEatPlace = mustEatPlaceRepository.findById(mustEatPlaceId)
                .orElseThrow(() -> new ContentNotFoundException("등록되지 않은 맛집정보입니다."));


        bookmarkSpotRepository.save(BookmarkSpot.builder()
                .userId(user)
                .restaurant(mustEatPlace.getRestaurant())
                .menu(mustEatPlace.getMenu())
                .address(mustEatPlace.getAddress())
                .operationHour(mustEatPlace.getOperationHour())
                .rate(mustEatPlace.getRate())
                .registeredAt(LocalDateTime.now())
                .build());

        return ServiceResult.success("맛집 즐겨찾기 등록 완료");
    }
}
