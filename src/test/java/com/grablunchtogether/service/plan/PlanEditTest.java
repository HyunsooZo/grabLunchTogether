package com.grablunchtogether.service.plan;

import com.grablunchtogether.common.exception.AuthorityException;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.Plan;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.plan.PlanCreationInput;
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

import static com.grablunchtogether.common.enums.PlanStatus.COMPLETED;
import static com.grablunchtogether.common.enums.PlanStatus.REQUESTED;

class PlanEditTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PlanRepository planRepository;

    private PlanService planService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        planService = new PlanServiceImpl(userRepository, planRepository);
    }

    @Test
    public void TestEditPlan_Success() {
        //given
        User requester = User.builder().id(1L).build();

        User accepter = User.builder().id(2L).build();

        Plan plan = Plan.builder()
                .id(1L)
                .requester(requester)
                .accepter(accepter)
                .planRestaurant("a")
                .planMenu("a")
                .planTime(LocalDateTime.now())
                .requestMessage("a")
                .planStatus(REQUESTED)
                .build();

        PlanCreationInput planModificationInput =
                PlanCreationInput.builder()
                        .planRestaurant("aa")
                        .planMenu("ss")
                        .planTime(LocalDateTime.now())
                        .requestMessage("123123")
                        .build();

        Mockito.when(planRepository.findById(requester.getId()))
                .thenReturn(Optional.of(plan));

        //when
        ServiceResult result = planService.editPlanRequest(requester.getId(), plan.getId(), planModificationInput);

        //then
        Assertions.assertThat(result.isResult()).isTrue();
        Assertions.assertThat(plan.getRequestMessage()).isEqualTo("123123");
    }

    @Test
    public void TestEditPlan_Fail_AlreadyDone() {
        //given
        User requester = User.builder().id(1L).build();

        User accepter = User.builder().id(2L).build();

        Plan plan = Plan.builder()
                .id(1L)
                .requester(requester)
                .accepter(accepter)
                .planRestaurant("a")
                .planMenu("a")
                .planTime(LocalDateTime.now())
                .requestMessage("a")
                .planStatus(COMPLETED)
                .build();

        PlanCreationInput planModificationInput =
                PlanCreationInput.builder()
                        .planRestaurant("aa")
                        .planMenu("ss")
                        .planTime(LocalDateTime.now())
                        .requestMessage("123123")
                        .build();

        Mockito.when(planRepository.findById(requester.getId()))
                .thenReturn(Optional.of(plan));

        //when,then
        Assertions.assertThatThrownBy(() ->
                        planService.editPlanRequest(requester.getId(), plan.getId(), planModificationInput))
                .isInstanceOf(AuthorityException.class)
                .hasMessage("요청중인 상태의 점심약속만 수정할 수 있습니다.");
    }

    @Test
    public void TestEditPlan_Fail_NotMyPlan() {
        //given
        User requester = User.builder().id(1L).build();
        User accepter = User.builder().id(2L).build();

        Plan plan = Plan.builder()
                .id(1L)
                .requester(accepter)
                .accepter(requester)
                .planRestaurant("a")
                .planMenu("a")
                .planTime(LocalDateTime.now())
                .requestMessage("a")
                .planStatus(COMPLETED)
                .build();

        PlanCreationInput planModificationInput =
                PlanCreationInput.builder()
                        .planRestaurant("aa")
                        .planMenu("ss")
                        .planTime(LocalDateTime.now())
                        .requestMessage("123123")
                        .build();

        Mockito.when(planRepository.findById(requester.getId()))
                .thenReturn(Optional.of(plan));

        //when,then
        Assertions.assertThatThrownBy(() ->
                        planService.editPlanRequest(requester.getId(), plan.getId(), planModificationInput))
                .isInstanceOf(AuthorityException.class)
                .hasMessage("내가 신청한 약속만 수정할 수 있습니다.");
    }
}