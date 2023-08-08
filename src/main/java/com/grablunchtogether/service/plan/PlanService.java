package com.grablunchtogether.service.plan;

import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.dto.plan.PlanCreationInput;

public interface PlanService {
    //점심약속 생성
    ServiceResult createPlan(Long id, Long accepterId, PlanCreationInput planCreationInput);

    //나에게 요청된 점심약속 리스트 가져오기
    ServiceResult getPlanListReceived(Long id);

    //내가 요청한 점심약속 리스트 가져오기
    ServiceResult getPlanListIRequested(Long id);

    //요청받은 점심약속을 수락 또는 거절
    ServiceResult approvePlan(Long id, Long planId, Character acceptCode);

    //수락된 점심약속을 취소
    ServiceResult cancelPlan(Long id, Long planId);

    //요청중인 상태의 점심약속을 수정
    ServiceResult editPlanRequest(Long id, Long planId, PlanCreationInput planModificationInput);

    //요청중인 상태의 점심약속을 삭제
    ServiceResult planDeletion(Long id, Long planId);
}
