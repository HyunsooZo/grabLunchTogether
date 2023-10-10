package com.grablunchtogether.service.plan;

import com.grablunchtogether.domain.Plan;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.plan.PlanDto;
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
import java.util.ArrayList;
import java.util.List;

import static com.grablunchtogether.enums.PlanStatus.COMPLETED;
import static com.grablunchtogether.enums.PlanStatus.REQUESTED;

@DisplayName("신청한 약속목록 조회")
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
    @DisplayName("성공")
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

        List<PlanDto.Dto> dtoList = new ArrayList<>();
        dtoList.add(PlanDto.Dto.of(plan1));
        dtoList.add(PlanDto.Dto.of(plan2));
        List<Plan> list = new ArrayList<>();
        list.add(plan1);
        list.add(plan2);

        Mockito.when(planRepository.findByRequesterIdAndPlanStatusNot(requester.getId(), COMPLETED))
                .thenReturn(list);
        //when
        List<PlanDto.Dto> planListIRequested = planService.getPlanListIRequested(requester.getId());
        //then
        Assertions.assertThat(planListIRequested.get(0).getPlanMenu())
                .isEqualTo(dtoList.get(0).getPlanMenu());
    }
}