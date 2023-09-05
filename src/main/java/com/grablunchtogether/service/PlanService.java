package com.grablunchtogether.service;

import com.grablunchtogether.domain.Plan;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.plan.PlanDto;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.exception.ErrorCode;
import com.grablunchtogether.repository.PlanRepository;
import com.grablunchtogether.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.grablunchtogether.enums.PlanStatus.*;
import static com.grablunchtogether.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class PlanService {
    private final UserRepository userRepository;
    private final PlanRepository planRepository;

    @Transactional
    public void createPlan(Long userId,
                           Long accepterId,
                           PlanDto.Request planCreationRequest) {

        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        User accepter = userRepository.findById(accepterId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        planRepository
                .findByRequesterIdAndAccepterIdAndPlanStatus(userId, accepterId, REQUESTED)
                .ifPresent(plan -> {
                    throw new CustomException(PLAN_ALREADY_EXISTS);
                });

        planRepository.save(
                Plan.builder()
                        .requester(requester)
                        .accepter(accepter)
                        .planRestaurant(planCreationRequest.getPlanRestaurant())
                        .planMenu(planCreationRequest.getPlanMenu())
                        .planTime(planCreationRequest.getPlanTime())
                        .requestMessage(planCreationRequest.getRequestMessage())
                        .planStatus(REQUESTED)
                        .build()
        );
    }

    //나에게 요청된 점심약속 목록가져오기
    @Transactional(readOnly = true)
    public List<PlanDto.Dto> getPlanListReceived(Long userId) {

        List<Plan> plans =
                planRepository.findByAccepterIdAndPlanStatus(userId, REQUESTED);

        return plans.stream()
                .map(PlanDto.Dto::of)
                .collect(Collectors.toList());
    }

    //내가 요청한 점심약속 목록 조회
    @Transactional(readOnly = true)
    public List<PlanDto.Dto> getPlanListIRequested(Long userId) {

        List<Plan> plans =
                planRepository.findByRequesterIdAndPlanStatusNot(userId, COMPLETED);

        return plans.stream()
                .map(PlanDto.Dto::of)
                .collect(Collectors.toList());
    }

    //받은 요청을 수락(Y) 또는 거절(N)
    @Transactional
    public void approvePlan(Long userId, Long planId, Character acceptCode) {

        Plan plan = planRepository.findByIdAndAccepterId(planId, userId)
                .orElseThrow(() -> new CustomException(PLAN_NOT_FOUND));

        if (!plan.getPlanStatus().equals(REQUESTED)) {
            throw new CustomException(NOT_PERMITTED);
        }

        plan.approve(acceptCode);

        planRepository.save(plan);
    }

    // 받은 or 신청한 요청을 취소
    @Transactional
    public void cancelPlan(Long userid, Long planId) {

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(PLAN_NOT_FOUND));

        if (!Objects.equals(plan.getAccepter().getId(), userid) &&
                !Objects.equals(plan.getRequester().getId(), userid)) {
            throw new CustomException(NOT_PERMITTED);
        }

        if (!Objects.equals(plan.getPlanStatus(), ACCEPTED)) {
            throw new CustomException(ErrorCode.CAN_NOT_CANCEL_PLAN);
        }

        plan.cancel();

        planRepository.save(plan);
    }

    //내가 요청한 점심약속 수정
    @Transactional
    public void editPlanRequest(Long userId,
                                Long planId,
                                PlanDto.Request planModificationRequest) {

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(PLAN_NOT_FOUND));

        if (!Objects.equals(plan.getRequester().getId(), userId)) {
            throw new CustomException(NOT_PERMITTED);
        }

        if (!Objects.equals(plan.getPlanStatus(), REQUESTED)) {
            throw new CustomException(ErrorCode.CAN_NOT_EDIT_PLAN);
        }

        plan.update(planModificationRequest);

        planRepository.save(plan);
    }

    //점심약속 삭제
    @Transactional
    public void planDeletion(Long userid, Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(PLAN_NOT_FOUND));

        if (!Objects.equals(plan.getRequester().getId(), userid)) {
            throw new CustomException(NOT_PERMITTED);
        }

        if (!Objects.equals(plan.getPlanStatus(), REQUESTED)) {
            throw new CustomException(ErrorCode.NOT_AVAILABLE_TO_CANCEL);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = LocalDateTime.now().format(formatter);

        if (plan.getPlanTime().plusHours(1)
                .isBefore(LocalDateTime.parse(formattedDateTime, formatter))) {
            throw new CustomException(PLAN_TIME_NOT_MATCH);
        }

        planRepository.delete(plan);
    }
}
