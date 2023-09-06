package com.grablunchtogether.service;

import com.grablunchtogether.domain.Plan;
import com.grablunchtogether.domain.PlanHistory;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.plan.PlanDto;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.exception.ErrorCode;
import com.grablunchtogether.repository.PlanHistoryRepository;
import com.grablunchtogether.repository.PlanRepository;
import com.grablunchtogether.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EnableScheduling
@RequiredArgsConstructor
@Service
public class PlanHistoryService {
    private final PlanHistoryRepository planHistoryRepository;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void updatePlanHistory() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedTime = LocalDateTime.now().format(formatter);
        LocalDateTime currentTime = LocalDateTime.parse(formattedTime, formatter);
        System.out.println(currentTime);

        List<Plan> completedPlans =
                planRepository.findCompletedPlansUsingNativeQuery(currentTime);
        List<Plan> pendingPlans =
                planRepository.findPendingPlansUsingNativeQuery(currentTime);
        List<Plan> canceledPlans =
                planRepository.findCanceledPlansUsingNativeQuery(currentTime);

        pendingPlans.forEach(plan -> {
            plan.expired();
            planRepository.save(plan);
        });

        canceledPlans.forEach(plan -> {
            plan.historyLoadCancel();
            planRepository.save(plan);
            registerHistory(plan);
        });

        completedPlans.forEach(plan -> {
            plan.historyLoadComplete();
            registerHistory(plan);
            planRepository.save(plan);
        });
    }

    @Transactional
    public void registerHistory(Plan plan) {
        planHistoryRepository.save(PlanHistory.builder()
                .planId(plan)
                .requesterId(plan.getRequester())
                .accepterId(plan.getAccepter())
                .build());
    }

    @Transactional(readOnly = true)
    public List<PlanDto.Dto> listMyHistory(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_INFO_NOT_FOUND));

        List<PlanHistory> planHistories =
                Stream
                        .concat(planHistoryRepository.findByRequesterId(user).stream(),
                                planHistoryRepository.findByAccepterId(user).stream())
                        .collect(Collectors.toList());

        return planHistories.stream()
                .map(planHistory -> PlanDto.Dto.of(planHistory.getPlanId()))
                .collect(Collectors.toList());
    }
}
