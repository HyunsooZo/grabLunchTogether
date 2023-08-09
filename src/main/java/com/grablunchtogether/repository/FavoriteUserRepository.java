package com.grablunchtogether.repository;

import com.grablunchtogether.domain.FavoriteUser;
import com.grablunchtogether.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteUserRepository extends JpaRepository<FavoriteUser, Long> {
    List<FavoriteUser> findByUserId(User user);
}
