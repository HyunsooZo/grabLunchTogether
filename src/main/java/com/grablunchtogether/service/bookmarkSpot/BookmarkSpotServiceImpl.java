package com.grablunchtogether.service.bookmarkSpot;

import com.grablunchtogether.common.exception.AuthorityException;
import com.grablunchtogether.common.exception.ContentNotFoundException;
import com.grablunchtogether.common.exception.UserInfoNotFoundException;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.BookmarkSpot;
import com.grablunchtogether.domain.MustEatPlace;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.bookmarkSpot.BookmarkSpotDto;
import com.grablunchtogether.dto.bookmarkSpot.BookmarkSpotInput;
import com.grablunchtogether.repository.BookmarkSpotRepository;
import com.grablunchtogether.repository.MustEatPlaceRepository;
import com.grablunchtogether.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public ServiceResult listBookmarkSpot(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserInfoNotFoundException("고객정보를 찾을 수 없습니다."));

        List<BookmarkSpot> listEntity = bookmarkSpotRepository.findByUserId(user);

        if (listEntity.isEmpty()) {
            throw new ContentNotFoundException("즐겨찾기에 등록된 장소가 없습니다.");
        }

        List<BookmarkSpotDto> result = new ArrayList<>();

        listEntity.forEach(bookmarkSpot -> {
            result.add(BookmarkSpotDto.of(bookmarkSpot));
        });

        return ServiceResult.success("목록가져오기 성공", result);
    }

    @Override
    @Transactional
    public ServiceResult deleteBookmarkSpot(Long bookmarkSpotId, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserInfoNotFoundException("고객정보를 찾을 수 없습니다."));

        BookmarkSpot bookmarkSpot = bookmarkSpotRepository.findById(bookmarkSpotId)
                .orElseThrow(() -> new ContentNotFoundException("등록되지 않은 맛집정보입니다."));

        if (!bookmarkSpot.getUserId().equals(user)) {
            throw new AuthorityException("본인의 즐겨찾기 식당만 삭제할 수 있습니다.");
        }

        bookmarkSpotRepository.delete(bookmarkSpot);

        return ServiceResult.success("즐겨찾기 맛집을 삭제했습니다.");
    }
}
