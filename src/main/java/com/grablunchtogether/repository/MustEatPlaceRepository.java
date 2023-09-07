package com.grablunchtogether.repository;

import com.grablunchtogether.domain.MustEatPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MustEatPlaceRepository extends JpaRepository<MustEatPlace,Long> {
    List<MustEatPlace> findByCityOrderByRateDesc(String cityName);
}
