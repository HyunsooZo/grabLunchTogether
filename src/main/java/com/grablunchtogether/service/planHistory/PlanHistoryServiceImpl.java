package com.grablunchtogether.service.planHistory;

import com.grablunchtogether.domain.Plan;
import com.grablunchtogether.domain.PlanHistory;
import com.grablunchtogether.repository.PlanHistoryRepository;
import com.grablunchtogether.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@EnableScheduling
@RequiredArgsConstructor
@Service
public class PlanHistoryServiceImpl implements PlanHistoryService {
    private final PlanHistoryRepository planHistoryRepository;
    private final PlanRepository planRepository;

    @Override
    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void updatePlanHistory() {
        List<Plan> pendingPlans =
                planRepository.findPendingPlans(LocalDateTime.now());
        List<Plan> completedPlans =
                planRepository.findCompletedPlans(LocalDateTime.now());

        pendingPlans.forEach(plan -> {
            plan.expired();
            planRepository.save(plan);
        });

        registerHistory(completedPlans);
    }

    @Override
    @Transactional
    public void registerHistory(List<Plan> plans) {
        plans.forEach(plan -> {
            if (!planHistoryRepository.findByPlanId(plan).isPresent()) {
                planHistoryRepository.save(PlanHistory.builder()
                        .planId(plan)
                        .requesterId(plan.getRequester())
                        .accepterId(plan.getAccepter())
                        .registeredAt(LocalDateTime.now())
                        .build());
            }
        });
    }
}
