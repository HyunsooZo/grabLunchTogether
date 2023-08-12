package com.grablunchtogether.repository;

import com.grablunchtogether.domain.enums.PlanStatus;
import com.grablunchtogether.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findByRequesterIdAndAccepterIdAndPlanStatus(Long requesterId,
                                                               Long accepterId,
                                                               PlanStatus planStatus);

    List<Plan> findByAccepterIdAndPlanStatus(Long userId, PlanStatus planStatus);

    List<Plan> findByRequesterIdAndPlanStatusNot(Long userId, PlanStatus planStatus);

    Optional<Plan> findByIdAndAccepterId(Long planId, Long userId);

    @Query("SELECT p FROM Plan p WHERE p.planTime < :currentDateTime AND p.planStatus = 'REQUESTED'")
    List<Plan> findPendingPlans(@Param("currentDateTime") LocalDateTime currentDateTime);

    @Query("SELECT p FROM Plan p WHERE p.planTime < :currentDateTime AND p.planStatus = 'COMPLETED'")
    List<Plan> findCompletedPlans(@Param("currentDateTime") LocalDateTime currentDateTime);

    @Query("SELECT p FROM Plan p WHERE p.planTime < :currentDateTime AND p.planStatus = 'CANCELED'")
    List<Plan> findCanceledPlans(@Param("currentDateTime") LocalDateTime currentDateTime);
}
