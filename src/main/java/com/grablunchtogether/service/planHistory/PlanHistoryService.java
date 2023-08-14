package com.grablunchtogether.service.planHistory;

import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.Plan;
import org.springframework.scheduling.annotation.Scheduled;

public interface PlanHistoryService {

    @Scheduled(cron = "0 * * * * *")
    void updatePlanHistory();

    void registerHistory(Plan plan);

    ServiceResult listMyHistory(Long userId);
}
