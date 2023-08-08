package com.grablunchtogether.service.plan;

import com.grablunchtogether.common.exception.ExistingPlanException;
import com.grablunchtogether.common.exception.UserInfoNotFoundException;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.Plan;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.plan.PlanCreationInput;
import com.grablunchtogether.repository.PlanRepository;
import com.grablunchtogether.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.grablunchtogether.common.enums.PlanStatus.REQUESTED;

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
}
