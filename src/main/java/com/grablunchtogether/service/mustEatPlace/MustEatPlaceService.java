package com.grablunchtogether.service.mustEatPlace;

import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import org.springframework.scheduling.annotation.Scheduled;

public interface MustEatPlaceService {
    //매주 월요일 00:00 별점 및 영업정보 업데이트를 위해 스케줄링을 통한 크롤링 실행
    @Scheduled(cron = "0 0 * * 1 *")
    void crawlMustEatPlace();

    //도시 이름을 기반으로 맛집리스트 조회(별점순)
    ServiceResult mustEatPlaceList(String cityName);
}
