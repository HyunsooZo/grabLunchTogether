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
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.grablunchtogether.enums.PlanStatus.COMPLETED;
import static com.grablunchtogether.enums.PlanStatus.REQUESTED;
import static org.mockito.Mockito.*;

@DisplayName("약속 삭제")
class PlanDeleteTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PlanRepository planRepository;

    private PlanService planService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        planService = new PlanService(userRepository, planRepository);
    }

    @Test
    @DisplayName("성공")
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
                .planTime(LocalDateTime.parse("2200-09-01T11:00"))
                .requestMessage("a")
                .planStatus(REQUESTED)
                .build();

        when(planRepository.findById(requester.getId()))
                .thenReturn(Optional.of(plan));

        //when
        planService.planDeletion(requester.getId(), plan.getId());

        //then
        verify(planRepository,times(1)).delete(plan);
    }

    @Test
    @DisplayName("실패(이미 성사된 약속)")
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

        when(planRepository.findById(plan.getId()))
                .thenReturn(Optional.of(plan));

        //when,then
        Assertions.assertThatThrownBy(() ->
                        planService.planDeletion(requester.getId(), plan.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 수락 또는 거절된 점심약속은 삭제할 수 없습니다. 점심약속 취소를 진행 해주세요.");
    }

    @Test
    @DisplayName("실패(권한없음)")
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

        when(planRepository.findById(plan.getId()))
                .thenReturn(Optional.of(plan));

        //when,then
        Assertions.assertThatThrownBy(() ->
                        planService.planDeletion(requester.getId(), plan.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("해당 데이터에 대한 접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("실패(삭제가능시간 초과)")
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
                .planTime(LocalDateTime.parse("2021-08-08T00:14:02"))
                .requestMessage("a")
                .planStatus(REQUESTED)
                .build();

        when(planRepository.findById(plan.getId()))
                .thenReturn(Optional.of(plan));

        //when,then
        Assertions.assertThatThrownBy(() ->
                        planService.planDeletion(requester.getId(), plan.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("약속시간 1시간 이전에만 가능합니다.");
    }
}