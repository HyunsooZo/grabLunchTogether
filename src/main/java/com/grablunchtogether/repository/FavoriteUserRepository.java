package com.grablunchtogether.repository;

import com.grablunchtogether.domain.FavoriteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteUserRepository extends JpaRepository<FavoriteUser, Long> {

}
