package com.grablunchtogether.service.favoriteUser;

import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.dto.favoriteUser.FavoriteUserInput;

public interface FavoriteUserService {
    //즐겨찾기 유저 추가
    ServiceResult addFavoriteUser(FavoriteUserInput favoriteUserInput,
                                  Long id,
                                  Long favoriteUserId);
}
