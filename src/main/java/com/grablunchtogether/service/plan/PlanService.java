package com.grablunchtogether.service.plan;

import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.dto.plan.PlanCreationInput;

public interface PlanService {
    //점심약속 생성
    ServiceResult createPlan(Long id, Long accepterId, PlanCreationInput planCreationInput);
}
