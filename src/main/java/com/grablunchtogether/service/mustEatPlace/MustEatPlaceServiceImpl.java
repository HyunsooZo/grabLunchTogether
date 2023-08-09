package com.grablunchtogether.service.mustEatPlace;

import com.grablunchtogether.domain.MustEatPlace;
import com.grablunchtogether.repository.MustEatPlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@EnableScheduling
@RequiredArgsConstructor
@Service
public class MustEatPlaceServiceImpl implements MustEatPlaceService {
    private final MustEatPlaceRepository mustEatPlaceRepository;

    @Value("${foodie_spot.url.header}")
    private String urlHeader;

    @Value("${foodie_spot.url.tail}")
    private String urlTail;

    private final List<String> cities =
            Arrays.asList("서울", "경기", "부산", "인천", "대구", "대전", "광주", "수원",
                    "울산", "창원", "성남", "고양", "용인", "안산", "안양", "부천", "광명",
                    "평택", "제주", "포항", "경주");

    @PersistenceContext
    private EntityManager entityManager;

    // 크롤링이 진행중인지 확인 하기 위함
    private boolean isCrawlingInProgress = false;

    // 크롤링이 진행 중임을 표시하는 플래그 설정 (동기화)
    private synchronized void startCrawling() {
        isCrawlingInProgress = true;
    }

    // 크롤링이 완료되었음을 표시하는 플래그 설정 (동기화)
    private synchronized void finishCrawling() {
        isCrawlingInProgress = false;
    }

    // 크롤링이 진행 중인지 여부를 확인하는 메서드 (동기화)
    private synchronized boolean isCrawlingInProgress() {
        return isCrawlingInProgress;
    }

    //매주 월요일 00:00 별점 및 영업정보 업데이트를 위해 스케줄링을 통한 크롤링 실행
    @Override
    @Scheduled(cron = "0 0 * * 1 *")
    @Transactional
    public void crawlMustEatPlace() {
        // 크롤링이 진행 중임을 표시하는 플래그 설정
        startCrawling();

        // 지난 주 맛집 정보 삭제
        entityManager.createQuery("DELETE FROM MustEatPlace").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE must_eat_place AUTO_INCREMENT = 1").executeUpdate();

        // 이번 주 업데이트 된 별점으로 맛집 정보 저장
        cities.forEach(this::crawlAndSave);
        log.info(String.format("맛집 크롤링 완료 / %s", LocalDateTime.now()));

        // 크롤링 진행 종료
        finishCrawling();
    }

    private void crawlAndSave(String city) {
        String url = urlHeader + city + "%20맛집&" + urlTail;
        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error(String.format(city + " 맛집정보 크롤링 중 에러 발생: %s", e.getMessage()));
        }

        if (document == null) {
            log.error("크롤링 중 에러 발생: document 객체가 null입니다.");
            return;
        }
        int length = Integer.MAX_VALUE;

        Elements restaurants = document.select("strong.tit_g");
        Elements menus = document.select("span.txt_ginfo ");
        Elements rates = document.select("em.num_rate");
        Elements addresses = document.select("span.txt_g");
        Elements operationTimes = document.select("span.txt_openoff");

        length = Math.min(length, restaurants.size());
        length = Math.min(length, menus.size());
        length = Math.min(length, rates.size());
        length = Math.min(length, addresses.size());
        length = Math.min(length, operationTimes.size());

        for (int i = 0; i < length; i++) {
            String restaurant = restaurants.get(i).text();
            String menu = menus.get(i).text();
            String rate = rates.get(i).text();
            String address = addresses.get(i).text();
            String operationTime = operationTimes.get(i).text();

            mustEatPlaceRepository.save(
                    MustEatPlace.builder()
                            .restaurant(restaurant)
                            .menu(menu)
                            .address(address)
                            .rate(rate)
                            .operationHour(operationTime)
                            .city(city)
                            .build());
        }
    }
}
