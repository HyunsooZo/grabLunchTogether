package com.grablunchtogether.service.plan;

import com.grablunchtogether.common.exception.AuthorityException;
import com.grablunchtogether.common.exception.PlanTimeNotMatchedException;
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

import static com.grablunchtogether.domain.enums.PlanStatus.COMPLETED;
import static com.grablunchtogether.domain.enums.PlanStatus.REQUESTED;

class PlanDeleteTest {
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
    public void TestDeletePlan_Success() {
        //given
        User requester = User.builder().id(1L).build();
        User accepter = User.builder().id(2L).build();

        Plan plan = Plan.builder()
                .id(1L)
                .requester(requester)
                .accepter(accepter)
                .planRestaurant("a")
                .planMenu("a")
                .planTime(LocalDateTime.parse("2023-09-01T11:00"))
                .requestMessage("a")
                .planStatus(REQUESTED)
                .build();

        Mockito.when(planRepository.findById(requester.getId()))
                .thenReturn(Optional.of(plan));

        //when
        ServiceResult result = planService.planDeletion(requester.getId(), plan.getId());

        //then
        Assertions.assertThat(result.isResult()).isTrue();
    }

    @Test
    public void TestDeletePlan_Fail_AlreadyDone() {
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

        Mockito.when(planRepository.findById(plan.getId()))
                .thenReturn(Optional.of(plan));

        //when,then
        Assertions.assertThatThrownBy(() ->
                        planService.planDeletion(requester.getId(), plan.getId()))
                .isInstanceOf(AuthorityException.class)
                .hasMessage("이미 수락 또는 거절된 점심약속은 삭제할 수 없습니다. 점심약속 취소를 진행 해주세요.");
    }

    @Test
    public void TestDeletePlan_Fail_NotMyPlan() {
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

        Mockito.when(planRepository.findById(plan.getId()))
                .thenReturn(Optional.of(plan));

        //when,then
        Assertions.assertThatThrownBy(() ->
                        planService.planDeletion(requester.getId(), plan.getId()))
                .isInstanceOf(AuthorityException.class)
                .hasMessage("본인이 요청한 점심약속 만 삭제할 수 있습니다.");
    }

    @Test
    public void TestDeletePlan_Fail_TimeRule() {
        //given
        User requester = User.builder().id(1L).build();

        User accepter = User.builder().id(2L).build();
        Plan plan = Plan.builder()
                .id(1L)
                .requester(requester)
                .accepter(accepter)
                .planRestaurant("a")
                .planMenu("a")
                .planTime(LocalDateTime.parse("2023-08-08T00:14:02"))
                .requestMessage("a")
                .planStatus(REQUESTED)
                .build();

        Mockito.when(planRepository.findById(plan.getId()))
                .thenReturn(Optional.of(plan));

        //when,then
        Assertions.assertThatThrownBy(() ->
                        planService.planDeletion(requester.getId(), plan.getId()))
                .isInstanceOf(PlanTimeNotMatchedException.class)
                .hasMessage("약속시간 1시간 이전에만 삭제가 가능합니다.");
    }
}