package com.grablunchtogether.service.bookmarkSpot;

import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.dto.bookmarkSpot.BookmarkSpotInput;

public interface BookmarkSpotService {
    ServiceResult registerBookmark(BookmarkSpotInput bookmarkSpotInput, Long id);
}
