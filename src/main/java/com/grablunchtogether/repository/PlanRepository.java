package com.grablunchtogether.repository;

import com.grablunchtogether.common.enums.PlanStatus;
import com.grablunchtogether.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findByRequesterIdAndAccepterIdAndPlanStatus(Long requesterId,
                                                               Long accepterId,
                                                               PlanStatus planStatus);

    List<Plan> findByAccepterIdAndPlanStatus(Long userId, PlanStatus planStatus);

    List<Plan> findByRequesterIdAndPlanStatusNot(Long userId, PlanStatus planStatus);
}
