package com.grablunchtogether.service.bookmarkSpot;

import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.dto.bookmarkSpot.BookmarkSpotInput;

public interface BookmarkSpotService {
    //북마크 추가 (직접입력)
    ServiceResult registerBookmark(BookmarkSpotInput bookmarkSpotInput, Long id);

    //북마크 추가 (맛집정보로 추가)
    ServiceResult registerBookmarkWithMustEatPlace(Long mustEatPlaceId, Long id);
}