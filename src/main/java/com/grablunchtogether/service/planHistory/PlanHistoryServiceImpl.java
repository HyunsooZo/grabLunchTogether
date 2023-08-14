package com.grablunchtogether.service.planHistory;

import com.grablunchtogether.common.exception.ContentNotFoundException;
import com.grablunchtogether.common.exception.UserInfoNotFoundException;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.Plan;
import com.grablunchtogether.domain.PlanHistory;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.plan.PlanDto;
import com.grablunchtogether.repository.PlanHistoryRepository;
import com.grablunchtogether.repository.PlanRepository;
import com.grablunchtogether.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EnableScheduling
@RequiredArgsConstructor
@Service
public class PlanHistoryServiceImpl implements PlanHistoryService {
    private final PlanHistoryRepository planHistoryRepository;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void updatePlanHistory() {
        List<Plan> completedPlans =
                planRepository.findCompletedPlans(LocalDateTime.now());
        List<Plan> pendingPlans =
                planRepository.findPendingPlans(LocalDateTime.now());
        List<Plan> canceledPlans =
                planRepository.findCanceledPlans(LocalDateTime.now());

        pendingPlans.forEach(plan -> {
            plan.expired();
            planRepository.save(plan);
        });

        if (canceledPlans.isEmpty() && completedPlans.isEmpty()) {
            return;
        }

        if (!canceledPlans.isEmpty()) {
            registerHistory(canceledPlans);
        }
        if (!completedPlans.isEmpty()) {
            registerHistory(completedPlans);
        }

        canceledPlans.forEach(plan -> {
            plan.historyLoadCancel();
            planRepository.save(plan);
        });

        completedPlans.forEach(plan -> {
            plan.historyLoadComplete();
            planRepository.save(plan);
        });
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
                        .build());
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult listMyHistory(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserInfoNotFoundException("고객 정보를 찾을 수 없습니다.")
        );

        List<PlanHistory> requestedList =
                planHistoryRepository.findByRequesterId(user);

        List<PlanHistory> acceptedList =
                planHistoryRepository.findByAccepterId(user);

        List<PlanDto> result = new ArrayList<>();

        requestedList.forEach(planHistory -> {
            result.add(PlanDto.of(planHistory.getPlanId()));
        });

        acceptedList.forEach(planHistory -> {
            result.add(PlanDto.of(planHistory.getPlanId()));
        });

        if (result.isEmpty()) {
            throw new ContentNotFoundException("히스토리가 존재하지 않습니다.");
        }

        return ServiceResult.success("목록 가져오기 성공", result);
    }
}
