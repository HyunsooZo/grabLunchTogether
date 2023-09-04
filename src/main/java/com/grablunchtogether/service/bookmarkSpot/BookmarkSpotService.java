package com.grablunchtogether.service.bookmarkSpot;

import com.grablunchtogether.exception.CustomException;
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

import java.util.ArrayList;
import java.util.List;

import static com.grablunchtogether.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class BookmarkSpotService {
    private final BookmarkSpotRepository bookmarkSpotRepository;
    private final UserRepository userRepository;
    private final MustEatPlaceRepository mustEatPlaceRepository;

    @Transactional
    public ServiceResult registerBookmark(BookmarkSpotInput bookmarkSpotInput,
                                          Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        bookmarkSpotRepository.save(BookmarkSpot.builder()
                .userId(user)
                .restaurant(bookmarkSpotInput.getRestaurant())
                .menu(bookmarkSpotInput.getMenu())
                .address(bookmarkSpotInput.getAddress())
                .operationHour(bookmarkSpotInput.getOperationHour())
                .rate(bookmarkSpotInput.getRate())
                .build());

        return ServiceResult.success("맛집 즐겨찾기 등록 완료");
    }

    @Transactional
    public ServiceResult registerBookmarkWithMustEatPlace(Long mustEatPlaceId,
                                                          Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        MustEatPlace mustEatPlace = mustEatPlaceRepository.findById(mustEatPlaceId)
                .orElseThrow(() -> new CustomException(CONTENT_NOT_FOUND));

        bookmarkSpotRepository.save(BookmarkSpot.builder()
                .userId(user)
                .restaurant(mustEatPlace.getRestaurant())
                .menu(mustEatPlace.getMenu())
                .address(mustEatPlace.getAddress())
                .operationHour(mustEatPlace.getOperationHour())
                .rate(mustEatPlace.getRate())
                .build());

        return ServiceResult.success("맛집 즐겨찾기 등록 완료");
    }

    @Transactional(readOnly = true)
    public ServiceResult listBookmarkSpot(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        List<BookmarkSpot> listEntity = bookmarkSpotRepository.findByUserId(user);

        if (listEntity.isEmpty()) {
            throw new CustomException(CONTENT_NOT_FOUND);
        }

        List<BookmarkSpotDto> result = new ArrayList<>();

        listEntity.forEach(bookmarkSpot -> {
            result.add(BookmarkSpotDto.of(bookmarkSpot));
        });

        return ServiceResult.success("목록가져오기 성공", result);
    }

    @Transactional
    public ServiceResult deleteBookmarkSpot(Long bookmarkSpotId, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        BookmarkSpot bookmarkSpot = bookmarkSpotRepository.findById(bookmarkSpotId)
                .orElseThrow(() -> new CustomException(CONTENT_NOT_FOUND));

        if (!bookmarkSpot.getUserId().equals(user)) {
            throw new CustomException(NOT_PERMITTED);
        }

        bookmarkSpotRepository.delete(bookmarkSpot);

        return ServiceResult.success("즐겨찾기 맛집을 삭제했습니다.");
    }
}
