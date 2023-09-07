package com.grablunchtogether.repository;

import com.grablunchtogether.domain.BookmarkSpot;
import com.grablunchtogether.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkSpotRepository extends JpaRepository<BookmarkSpot, Long> {
    List<BookmarkSpot> findByUserId(User user);
}
