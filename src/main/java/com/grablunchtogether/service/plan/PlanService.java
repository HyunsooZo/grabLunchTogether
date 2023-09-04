package com.grablunchtogether.service.plan;

import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.exception.ErrorCode;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.Plan;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.plan.PlanCreationInput;
import com.grablunchtogether.dto.plan.PlanDto;
import com.grablunchtogether.repository.PlanRepository;
import com.grablunchtogether.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.grablunchtogether.exception.ErrorCode.*;
import static com.grablunchtogether.enums.PlanStatus.*;

@RequiredArgsConstructor
@Service
public class PlanService {
    private final UserRepository userRepository;
    private final PlanRepository planRepository;

    @Transactional
    public ServiceResult createPlan(Long userId,
                                    Long accepterId,
                                    PlanCreationInput planCreationInput) {

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
                        .planRestaurant(planCreationInput.getPlanRestaurant())
                        .planMenu(planCreationInput.getPlanMenu())
                        .planTime(planCreationInput.getPlanTime())
                        .requestMessage(planCreationInput.getRequestMessage())
                        .planStatus(REQUESTED)
                        .build()
        );

        return ServiceResult.success("약속이 생성되었습니다.");
    }

    //나에게 요청된 점심약속 목록가져오기
    @Transactional(readOnly = true)
    public ServiceResult getPlanListReceived(Long userId) {

        List<PlanDto> result = new ArrayList<>();

        List<Plan> list =
                planRepository.findByAccepterIdAndPlanStatus(userId, REQUESTED);

        if (list.isEmpty()) {
            throw new CustomException(CONTENT_NOT_FOUND);
        }

        list.forEach(plan -> {
            result.add(PlanDto.of(plan));
        });

        return ServiceResult.success("목록 수신 성공", result);
    }

    //내가 요청한 점심약속 목록 조회
    @Transactional(readOnly = true)
    public ServiceResult getPlanListIRequested(Long userId) {
        List<PlanDto> result = new ArrayList<>();

        List<Plan> list =
                planRepository.findByRequesterIdAndPlanStatusNot(userId, COMPLETED);

        if (list.isEmpty()) {
            throw new CustomException(CONTENT_NOT_FOUND);
        }

        list.forEach(plan -> {
            result.add(PlanDto.of(plan));
        });

        return ServiceResult.success("목록 수신 성공", result);
    }

    //받은 요청을 수락(Y) 또는 거절(N)
    @Transactional
    public ServiceResult approvePlan(Long userId, Long planId, Character acceptCode) {

        Plan plan = planRepository.findByIdAndAccepterId(planId, userId)
                .orElseThrow(() -> new CustomException(CONTENT_NOT_FOUND));

        if (!plan.getPlanStatus().equals(REQUESTED)) {
            throw new CustomException(NOT_PERMITTED);
        }

        plan.approve(acceptCode);

        planRepository.save(plan);

        return ServiceResult.success(acceptCode == 'Y' ? "수락이 완료되었습니다." : "거절이 완료되었습니다.");
    }

    // 받은 or 신청한 요청을 취소
    @Transactional
    public ServiceResult cancelPlan(Long userid, Long planId) {

        Plan plan = planRepository.findById(planId).orElseThrow(()
                -> new CustomException(CONTENT_NOT_FOUND));

        if (!Objects.equals(plan.getAccepter().getId(), userid) &&
                !Objects.equals(plan.getRequester().getId(), userid)) {
            throw new CustomException(NOT_PERMITTED);
        }

        if (!Objects.equals(plan.getPlanStatus(), ACCEPTED)) {
            throw new CustomException(ErrorCode.CAN_NOT_CANCEL_PLAN);
        }

        plan.cancel();

        planRepository.save(plan);

        return ServiceResult.success("점심약속요청이 취소되었습니다.");
    }

    //내가 요청한 점심약속 수정
    @Transactional
    public ServiceResult editPlanRequest(Long userId,
                                         Long planId,
                                         PlanCreationInput planModificationInput) {

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(CONTENT_NOT_FOUND));

        if (!Objects.equals(plan.getRequester().getId(), userId)) {
            throw new CustomException(NOT_PERMITTED);
        }

        if (!Objects.equals(plan.getPlanStatus(), REQUESTED)) {
            throw new CustomException(ErrorCode.CAN_NOT_EDIT_PLAN);
        }

        plan.update(planModificationInput.getPlanMenu(),
                planModificationInput.getPlanRestaurant(),
                planModificationInput.getPlanTime(),
                planModificationInput.getRequestMessage());

        planRepository.save(plan);

        return ServiceResult.success("점심약속 요청 수정이 완료되었습니다.");
    }

    //점심약속 삭제
    @Transactional
    public ServiceResult planDeletion(Long userid, Long planId) {
        Plan plan = planRepository.findById(planId).orElseThrow(()
                -> new CustomException(CONTENT_NOT_FOUND));

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

        return ServiceResult.success("점심약속요청이 삭제되었습니다.");
    }
}
