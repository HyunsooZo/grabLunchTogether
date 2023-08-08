package com.grablunchtogether.service.planHistory;

import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.Plan;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public interface PlanHistoryService {

    @Scheduled(cron = "0 * * * * *")
    void updatePlanHistory();

    void registerHistory(List<Plan> plans);

    ServiceResult listMyHistory(Long userId);
}
