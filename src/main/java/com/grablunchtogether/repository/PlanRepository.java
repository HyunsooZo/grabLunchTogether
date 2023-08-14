package com.grablunchtogether.repository;

import com.grablunchtogether.domain.Plan;
import com.grablunchtogether.domain.enums.PlanStatus;
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

    @Query(value = "SELECT * FROM plan p WHERE p.plan_time < :currentDateTime AND p.plan_status = 'REQUESTED'", nativeQuery = true)
    List<Plan> findPendingPlansUsingNativeQuery(@Param("currentDateTime") LocalDateTime currentDateTime);

    @Query(value = "SELECT * FROM plan p WHERE p.plan_time < :currentDateTime AND p.plan_status = 'ACCEPTED'", nativeQuery = true)
    List<Plan> findCompletedPlansUsingNativeQuery(@Param("currentDateTime") LocalDateTime currentDateTime);

    @Query(value = "SELECT * FROM plan p WHERE p.plan_time < :currentDateTime AND p.plan_status = 'CANCELED'", nativeQuery = true)
    List<Plan> findCanceledPlansUsingNativeQuery(@Param("currentDateTime") LocalDateTime currentDateTime);
}