package com.grablunchtogether.service.planHistory;

import com.grablunchtogether.domain.Plan;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PlanHistoryService {
    @Transactional
    @Scheduled(cron = "0 * * * * *")
    void updatePlanHistory();

    @Transactional
    void registerHistory(List<Plan> plans);
}
