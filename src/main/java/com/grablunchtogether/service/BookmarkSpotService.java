package com.grablunchtogether.service;

import com.grablunchtogether.domain.BookmarkSpot;
import com.grablunchtogether.domain.MustEatPlace;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.bookmarkSpot.BookmarkSpotDto;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.repository.BookmarkSpotRepository;
import com.grablunchtogether.repository.MustEatPlaceRepository;
import com.grablunchtogether.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.grablunchtogether.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class BookmarkSpotService {
    private final BookmarkSpotRepository bookmarkSpotRepository;
    private final UserRepository userRepository;
    private final MustEatPlaceRepository mustEatPlaceRepository;

    @Transactional
    public void registerBookmark(BookmarkSpotDto.Request bookmarkSpotInput,
                                 Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        bookmarkSpotRepository.save(BookmarkSpot.of(bookmarkSpotInput,user));
    }

    @Transactional
    public void registerBookmarkWithMustEatPlace(Long mustEatPlaceId,
                                                 Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        MustEatPlace mustEatPlace = mustEatPlaceRepository.findById(mustEatPlaceId)
                .orElseThrow(() -> new CustomException(MUST_EAT_PLACE_NOT_FOUND));

        bookmarkSpotRepository.save(BookmarkSpot.builder()
                .userId(user)
                .restaurant(mustEatPlace.getRestaurant())
                .menu(mustEatPlace.getMenu())
                .address(mustEatPlace.getAddress())
                .operationHour(mustEatPlace.getOperationHour())
                .rate(mustEatPlace.getRate())
                .build());
    }

    @Transactional(readOnly = true)
    public List<BookmarkSpotDto.Dto> listBookmarkSpot(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        List<BookmarkSpot> listEntity = bookmarkSpotRepository.findByUserId(user);

        return listEntity.stream()
                .map(BookmarkSpotDto.Dto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteBookmarkSpot(Long bookmarkSpotId, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        BookmarkSpot bookmarkSpot = bookmarkSpotRepository.findById(bookmarkSpotId)
                .orElseThrow(() -> new CustomException(BOOKMARK_SPOT_NOT_FOUND));

        if (!bookmarkSpot.getUserId().equals(user)) {
            throw new CustomException(NOT_PERMITTED);
        }

        bookmarkSpotRepository.delete(bookmarkSpot);
    }
}
