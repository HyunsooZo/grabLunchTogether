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

@DisplayName("약속 취소")
class PlanCancelTest {
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
                .planStatus(ACCEPTED)
                .build();

        Mockito.when(planRepository.findById(plan1.getId()))
                .thenReturn(Optional.of(plan1));

        //when
        planService.cancelPlan(accepter.getId(), plan1.getId());

        //then
        assertThat(plan1.getPlanStatus()).isEqualTo(CANCELED);
    }

    @Test
    @DisplayName("실패(관련없는약속취소시도)")
    public void TestPlanApproval_Fail_NotInvolved() {
        //given
        User requester = User.builder().id(1L).build();
        User accepter = User.builder().id(2L).build();

        Plan plan2 = Plan.builder()
                .id(2L)
                .requester(User.builder().id(9L).build())
                .accepter(User.builder().id(10L).build())
                .planRestaurant("a")
                .planMenu("a")
                .planTime(LocalDateTime.now())
                .requestMessage("a")
                .planStatus(ACCEPTED)
                .build();

        Mockito.when(planRepository.findById(plan2.getId()))
                .thenReturn(Optional.of(plan2));

        //when,then
        Assertions.assertThatThrownBy(() -> planService.cancelPlan(accepter.getId(), plan2.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("해당 데이터에 대한 접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("실패(관련없는약속취소시도)")
    public void TestPlanApproval_Fail_NotAccepted() {
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

        Mockito.when(planRepository.findById(plan1.getId()))
                .thenReturn(Optional.of(plan1));

        //when,then
        Assertions.assertThatThrownBy(() -> planService.cancelPlan(requester.getId(), plan1.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 취소 되었거나 취소할 수 없는상태의 점심약속입니다.");
    }
}