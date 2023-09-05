package com.grablunchtogether.service.plan;

import com.grablunchtogether.domain.Plan;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.plan.PlanDto;
import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.repository.PlanRepository;
import com.grablunchtogether.repository.UserRepository;
import com.grablunchtogether.service.PlanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.grablunchtogether.enums.PlanStatus.COMPLETED;
import static com.grablunchtogether.enums.PlanStatus.REQUESTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("약속 수정")
class PlanEditTest {
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

        PlanDto.Request planModificationInput =
                PlanDto.Request.builder()
                        .planRestaurant("aa")
                        .planMenu("ss")
                        .planTime(LocalDateTime.now())
                        .requestMessage("123123")
                        .build();

        Mockito.when(planRepository.findById(requester.getId()))
                .thenReturn(Optional.of(plan));

        //when
        planService.editPlanRequest(requester.getId(), plan.getId(), planModificationInput);

        //then
        assertThat(plan.getRequestMessage()).isEqualTo("123123");
    }

    @Test
    @DisplayName("실패(이미 성사된 약속)")
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

        PlanDto.Request planModificationInput =
                PlanDto.Request.builder()
                        .planRestaurant("aa")
                        .planMenu("ss")
                        .planTime(LocalDateTime.now())
                        .requestMessage("123123")
                        .build();

        Mockito.when(planRepository.findById(requester.getId()))
                .thenReturn(Optional.of(plan));

        //when,then
        assertThatThrownBy(() ->
                        planService.editPlanRequest(requester.getId(), plan.getId(), planModificationInput))
                .isInstanceOf(CustomException.class)
                .hasMessage("요청중인 상태의 점심약속만 수정할 수 있습니다.");
    }

    @Test
    @DisplayName("실패(권한없음)")
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

        PlanDto.Request planModificationInput =
                PlanDto.Request.builder()
                        .planRestaurant("aa")
                        .planMenu("ss")
                        .planTime(LocalDateTime.now())
                        .requestMessage("123123")
                        .build();

        Mockito.when(planRepository.findById(requester.getId()))
                .thenReturn(Optional.of(plan));

        //when,then
        assertThatThrownBy(() ->
                        planService.editPlanRequest(requester.getId(), plan.getId(), planModificationInput))
                .isInstanceOf(CustomException.class)
                .hasMessage("해당 데이터에 대한 접근 권한이 없습니다.");
    }
}