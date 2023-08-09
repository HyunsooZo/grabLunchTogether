package com.grablunchtogether.service.favoriteUser;

import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.dto.favoriteUser.FavoriteUserInput;

public interface FavoriteUserService {
    //즐겨찾기 유저 추가
    ServiceResult addFavoriteUser(FavoriteUserInput favoriteUserInput,
                                  Long id,
                                  Long otherUserId);

    //즐겨찾기 유저 정보 수정
    ServiceResult editFavoriteUser(FavoriteUserInput favoriteUserEditInput,
                                   Long id,
                                   Long favoriteUserId);

    //즐겨찾기 유저 삭제
    ServiceResult deleteFavoriteUser(Long userId, Long favoriteUserId);

    //즐겨찾기 유저 목록 조회
    ServiceResult listFavoriteUser(Long userId);
}
