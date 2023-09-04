package com.grablunchtogether.service.plan;

import com.grablunchtogether.exception.CustomException;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.Plan;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.plan.PlanDto;
import com.grablunchtogether.repository.PlanRepository;
import com.grablunchtogether.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.grablunchtogether.enums.PlanStatus.COMPLETED;
import static com.grablunchtogether.enums.PlanStatus.REQUESTED;

class PlanGetListIRequestedTest {
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
    public void testGetPlanListIRequested_Success() {
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
                .id(2L)
                .requester(requester)
                .accepter(accepter)
                .planRestaurant("b")
                .planMenu("b")
                .planTime(LocalDateTime.now())
                .requestMessage("b")
                .planStatus(REQUESTED)
                .build();

        List<PlanDto> dtoList = new ArrayList<>();
        dtoList.add(PlanDto.of(plan1));
        dtoList.add(PlanDto.of(plan2));
        List<Plan> list = new ArrayList<>();
        list.add(plan1);
        list.add(plan2);

        Mockito.when(planRepository.findByRequesterIdAndPlanStatusNot(requester.getId(), COMPLETED))
                .thenReturn(list);
        //when
        ServiceResult result = planService.getPlanListIRequested(requester.getId());
        //then
        Assertions.assertThat((List<PlanDto>) result.getObject())
                .isEqualTo(dtoList);
    }
    @Test
    public void testGetPlanListIRequested_Fail() {
        //given
        User requester = User.builder().id(1L).build();

        User accepter = User.builder().id(2L).build();

        List<Plan> list = new ArrayList<>();

        Mockito.when(planRepository.findByRequesterIdAndPlanStatusNot(requester.getId(), COMPLETED))
                .thenReturn(list);

        //when,then
        Assertions.assertThatThrownBy(()->planService.getPlanListIRequested(requester.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("요청한 점심식사 요청이 없습니다.");
    }
}