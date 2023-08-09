package com.grablunchtogether.repository;

import com.grablunchtogether.domain.MustEatPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MustEatPlaceRepository extends JpaRepository<MustEatPlace,Long> {
    List<MustEatPlace> findByCityOrderByRateDesc(String cityName);
}
