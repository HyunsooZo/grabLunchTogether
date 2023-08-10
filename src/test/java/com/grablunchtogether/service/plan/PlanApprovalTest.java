package com.grablunchtogether.service.plan;

import com.grablunchtogether.common.exception.AuthorityException;
import com.grablunchtogether.common.exception.ContentNotFoundException;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.Plan;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.repository.PlanRepository;
import com.grablunchtogether.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.grablunchtogether.common.enums.PlanStatus.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PlanApprovalTest {

    @Mock
    private PlanRepository planRepository;
    @Mock
    private UserRepository userRepository;
    private PlanService planService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        planService = new PlanServiceImpl(userRepository, planRepository);
    }

    @Test
    public void TestPlanApproval_Success() {
        //given
        User requester = User.builder().id(1L).build();
        User accepter = User.builder().id(2L).build();

        Plan plan1 = Plan.builder()
                .id(1L)
                .requester(requester)
                .accepter(accepter)
                .planRestaurant("a")
                .planMenu("a")
                .planTime(LocalDateTime.now())
                .requestMessage("a")
                .planStatus(REQUESTED)
                .RegisteredAt(LocalDateTime.now())
                .build();

        Plan plan2 = Plan.builder()
                .id(1L)
                .requester(accepter)
                .accepter(requester)
                .planRestaurant("a")
                .planMenu("a")
                .planTime(LocalDateTime.now())
                .requestMessage("a")
                .planStatus(REQUESTED)
                .RegisteredAt(LocalDateTime.now())
                .build();

        Mockito.when(planRepository.findByIdAndAccepterId(1L, accepter.getId()))
                .thenReturn(Optional.of(plan1));
        Mockito.when(planRepository.findByIdAndAccepterId(1L, requester.getId()))
                .thenReturn(Optional.of(plan2));


        //when
        ServiceResult y = planService.approvePlan(accepter.getId(), plan1.getId(), 'Y');
        ServiceResult n = planService.approvePlan(requester.getId(), plan2.getId(), 'N');

        //then
        Assertions.assertThat(y.isResult()).isTrue();
        Assertions.assertThat(n.isResult()).isTrue();
        assertThat(plan1.getPlanStatus()).isEqualTo(ACCEPTED);
        assertThat(plan2.getPlanStatus()).isEqualTo(REJECTED);
    }

    @Test
    public void TestPlanApproval_Fail_EmptyPlan() {
        //given

        User accepter = User.builder().id(2L).build();

        Plan plan = Plan.builder().id(1L).build();


        Mockito.when(planRepository.findByIdAndAccepterId(1L, accepter.getId()))
                .thenReturn(Optional.empty());

        //when,then
        Assertions
                .assertThatThrownBy(() -> planService.approvePlan(accepter.getId(), plan.getId(), 'Y'))
                .isInstanceOf(ContentNotFoundException.class)
                .hasMessage("존재하지 않는 점심약속이거나 나에게 요청된 약속이 아닙니다.");
    }

    @Test
    public void TestPlanApproval_Fail_AlreadyDone() {
        //given
        User requester = User.builder().id(1L).build();
        User accepter = User.builder().id(2L).build();

        Plan plan1 = Plan.builder()
                .id(1L)
                .requester(requester)
                .accepter(accepter)
                .planRestaurant("a")
                .planMenu("a")
                .planTime(LocalDateTime.now())
                .requestMessage("a")
                .planStatus(ACCEPTED)
                .RegisteredAt(LocalDateTime.now())
                .build();

        Mockito.when(planRepository.findByIdAndAccepterId(1L, accepter.getId()))
                .thenReturn(Optional.of(plan1));

        //when,then
        Assertions
                .assertThatThrownBy(() -> planService.approvePlan(accepter.getId(), plan1.getId(), 'Y'))
                .isInstanceOf(AuthorityException.class)
                .hasMessage("이미 수락 또는 거절/만료 된 약속입니다.");
    }
}