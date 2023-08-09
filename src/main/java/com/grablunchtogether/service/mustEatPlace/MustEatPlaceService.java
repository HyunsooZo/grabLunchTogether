package com.grablunchtogether.service.mustEatPlace;

import org.springframework.scheduling.annotation.Scheduled;

public interface MustEatPlaceService {
    //매주 월요일 00:00 별점 및 영업정보 업데이트를 위해 스케줄링을 통한 크롤링 실행
    @Scheduled(cron = "0 0 * * 1 *")
    void crawlMustEatPlace();
}
