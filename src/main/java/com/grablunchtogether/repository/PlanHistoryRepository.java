package com.grablunchtogether.repository;

import com.grablunchtogether.domain.Plan;
import com.grablunchtogether.domain.PlanHistory;
import com.grablunchtogether.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanHistoryRepository extends JpaRepository<PlanHistory, Long> {
    Optional<Plan> findByPlanId(Plan plan);

    List<PlanHistory> findByRequesterId(User userId);

    List<PlanHistory> findByAccepterId(User userId);
}
