package com.grablunchtogether.repository;

import com.grablunchtogether.domain.Plan;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.common.enums.PlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan,Long> {
    Optional<Plan> findByRequesterIdAndAccepterIdAndPlanStatus(Long requesterId,
                                                               Long accepterId,
                                                               PlanStatus planStatus);
}
