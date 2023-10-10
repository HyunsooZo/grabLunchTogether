package com.grablunchtogether.service.plan;

import com.grablunchtogether.domain.Plan;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.repository.PlanRepository;
import com.grablunchtogether.repository.UserRepository;
import com.grablunchtogether.service.PlanService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.grablunchtogether.enums.PlanStatus.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("약속 승인")
class PlanApprovalTest {

    @Mock
    private PlanRepository planRepository;
    @Mock
    private UserRepository userRepository;
    private PlanService planService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        planService = new PlanService(userRepository, planRepository);
    }

    @Test
    @DisplayName("성공")
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
                .build();

        Mockito.when(planRepository.findByIdAndAccepterId(1L, accepter.getId()))
                .thenReturn(Optional.of(plan1));
        Mockito.when(planRepository.findByIdAndAccepterId(1L, requester.getId()))
                .thenReturn(Optional.of(plan2));


        //when
        planService.approvePlan(accepter.getId(), plan1.getId(), 'Y');
        planService.approvePlan(requester.getId(), plan2.getId(), 'N');

        //then
        assertThat(plan1.getPlanStatus()).isEqualTo(ACCEPTED);
        assertThat(plan2.getPlanStatus()).isEqualTo(REJECTED);
    }

    @Test
    @DisplayName("실패(없는 약속)")
    public void TestPlanApproval_Fail_EmptyPlan() {
        //given

        User accepter = User.builder().id(2L).build();

        Plan plan = Plan.builder().id(1L).build();


        Mockito.when(planRepository.findByIdAndAccepterId(1L, accepter.getId()))
                .thenReturn(Optional.empty());

        //when,then
        Assertions
                .assertThatThrownBy(() -> planService.approvePlan(accepter.getId(), plan.getId(), 'Y'))
                .isInstanceOf(CustomException.class)
                .hasMessage("존재하지 않는 약속정보입니다.");
    }

    @Test
    @DisplayName("실패(이미승인됨)")
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
                .build();

        Mockito.when(planRepository.findByIdAndAccepterId(1L, accepter.getId()))
                .thenReturn(Optional.of(plan1));

        //when,then
        Assertions
                .assertThatThrownBy(() -> planService.approvePlan(accepter.getId(), plan1.getId(), 'Y'))
                .isInstanceOf(CustomException.class)
                .hasMessage("해당 데이터에 대한 접근 권한이 없습니다.");
    }
}