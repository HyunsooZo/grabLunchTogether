package com.grablunchtogether.repository;

import com.grablunchtogether.domain.BookmarkSpot;
import com.grablunchtogether.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkSpotRepository extends JpaRepository<BookmarkSpot, Long> {
    List<BookmarkSpot> findByUserId(User user);
}
