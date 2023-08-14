package com.grablunchtogether.repository;

import com.grablunchtogether.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUserEmail(String userEmail);
    @Query(value =
            "SELECT u.user_email,u.user_name,u.user_rate,u.company," +
                    "(6371 * acos(cos(radians(:lat)) * cos(radians(latitude)) * cos(radians(longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(latitude)))) AS distance" +
                    " FROM user u" +
                    " WHERE (6371 * acos(cos(radians(:lat)) * cos(radians(latitude)) * cos(radians(longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(latitude)))) <= :criteria" +
                    " ORDER BY distance",
            countQuery = "SELECT count(*) FROM user u WHERE (6371 * acos(cos(radians(:lat)) * cos(radians(latitude)) * cos(radians(longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(Latitude)))) <= :criteria",
            nativeQuery = true)
    List<Object[]> getUserListByDistance(@Param("lat") double latitude,
                                         @Param("lng") double longitude,
                                         @Param("criteria") double kilometer);
}
