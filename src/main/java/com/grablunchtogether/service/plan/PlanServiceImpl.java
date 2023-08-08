package com.grablunchtogether.service.plan;

import com.grablunchtogether.common.exception.AuthorityException;
import com.grablunchtogether.common.exception.ContentNotFoundException;
import com.grablunchtogether.common.exception.ExistingPlanException;
import com.grablunchtogether.common.exception.UserInfoNotFoundException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.grablunchtogether.common.enums.PlanStatus.*;

@RequiredArgsConstructor
@Service
public class PlanServiceImpl implements PlanService {
    private final UserRepository userRepository;
    private final PlanRepository planRepository;

    @Override
    @Transactional
    public ServiceResult createPlan(Long userId,
                                    Long accepterId,
                                    PlanCreationInput planCreationInput) {

        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new UserInfoNotFoundException("존재하지 않는 유저입니다."));

        User accepter = userRepository.findById(accepterId)
                .orElseThrow(() -> new UserInfoNotFoundException("존재하지 않는 유저입니다."));

        planRepository
                .findByRequesterIdAndAccepterIdAndPlanStatus(userId, accepterId, REQUESTED)
                .ifPresent(plan -> {
                    throw new ExistingPlanException(
                            "상대방에게 신청한 '요청중' 상태의 점심약속이 존재합니다." +
                                    " 기존점심약속이 수락/거절되었거나 완료된 경우 다시 신청 할 수 있습니다."
                    );
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
                        .RegisteredAt(LocalDateTime.now())
                        .build()
        );

        return ServiceResult.success("약속이 생성되었습니다.");
    }

    //나에게 요청된 점심약속 목록가져오기
    @Override
    @Transactional(readOnly = true)
    public ServiceResult getPlanListReceived(Long userId) {

        List<PlanDto> result = new ArrayList<>();

        List<Plan> list =
                planRepository.findByAccepterIdAndPlanStatus(userId, REQUESTED);

        if (list.isEmpty()) {
            throw new ContentNotFoundException("받은 점심약속 요청이 없습니다.");
        }

        list.forEach(plan -> {
            result.add(PlanDto.of(plan));
        });

        return ServiceResult.success("목록 수신 성공", result);
    }

    //내가 요청한 점심약속 목록 조회
    @Override
    @Transactional(readOnly = true)
    public ServiceResult getPlanListIRequested(Long userId) {
        List<PlanDto> result = new ArrayList<>();

        List<Plan> list =
                planRepository.findByRequesterIdAndPlanStatusNot(userId, COMPLETED);

        if (list.isEmpty()) {
            throw new ContentNotFoundException("요청한 점심식사 요청이 없습니다.");
        }

        list.forEach(plan -> {
            result.add(PlanDto.of(plan));
        });

        return ServiceResult.success("목록 수신 성공", result);
    }

    //받은 요청을 수락(Y) 또는 거절(N)
    @Override
    @Transactional
    public ServiceResult approvePlan(Long userId, Long planId, Character acceptCode) {

        Plan plan = planRepository.findByIdAndAccepterId(planId, userId).orElseThrow(
                () -> new ContentNotFoundException("존재하지 않는 점심약속이거나 나에게 요청된 약속이 아닙니다.")
        );

        if (!plan.getPlanStatus().equals(REQUESTED)) {
            throw new AuthorityException("이미 수락 또는 거절/만료 된 약속입니다.");
        }

        plan.approve(acceptCode);

        planRepository.save(plan);

        return ServiceResult.success(acceptCode == 'Y' ? "수락이 완료되었습니다." : "거절이 완료되었습니다.");
    }

    // 받은 or 신청한 요청을 취소
    @Override
    @Transactional
    public ServiceResult cancelPlan(Long userid, Long planId) {

        Plan plan = planRepository.findById(planId).orElseThrow(()
                -> new ContentNotFoundException("존재하지 않는 점심약속입니다."));

        if (!Objects.equals(plan.getAccepter().getId(), userid) &&
                !Objects.equals(plan.getRequester().getId(), userid)) {
            throw new AuthorityException("본인이 요청/수신 한 점심약속 만 취소할 수 있습니다.");
        }

        if (!Objects.equals(plan.getPlanStatus(), ACCEPTED)) {
            throw new AuthorityException("이미 취소 되었거나 취소할 수 없는상태의 점심약속입니다.");
        }

        plan.cancel();

        planRepository.save(plan);

        return ServiceResult.success("점심약속요청이 취소되었습니다.");
    }

    //내가 요청한 점심약속 수정
    @Override
    @Transactional
    public ServiceResult editPlanRequest(Long userId,
                                         Long planId,
                                         PlanCreationInput planModificationInput) {

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ContentNotFoundException("존재하지 않는 약속 신청입니다."));

        if (!Objects.equals(plan.getRequester().getId(), userId)) {
            throw new AuthorityException("내가 신청한 약속만 수정할 수 있습니다.");
        }

        if (!Objects.equals(plan.getPlanStatus(), REQUESTED)) {
            throw new AuthorityException("요청중인 상태의 점심약속만 수정할 수 있습니다.");
        }

        plan.update(planModificationInput.getPlanMenu(),
                planModificationInput.getPlanRestaurant(),
                planModificationInput.getPlanTime(),
                planModificationInput.getRequestMessage());

        planRepository.save(plan);

        return ServiceResult.success("점심약속 요청 수정이 완료되었습니다.");
    }
}
