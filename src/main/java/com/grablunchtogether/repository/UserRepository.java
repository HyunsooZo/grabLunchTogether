package com.grablunchtogether.repository;

import com.grablunchtogether.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<Object> findByUserEmail(String userEmail);
}