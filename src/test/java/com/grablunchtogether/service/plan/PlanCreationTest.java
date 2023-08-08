package com.grablunchtogether.service.plan;

import com.grablunchtogether.common.enums.PlanStatus;
import com.grablunchtogether.common.exception.ExistingPlanException;
import com.grablunchtogether.common.exception.UserInfoNotFoundException;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.domain.Plan;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.plan.PlanCreationInput;
import com.grablunchtogether.repository.PlanRepository;
import com.grablunchtogether.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlanCreationTest {
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
    public void testPlanCreation_Success() {
        //given
        User requester = new User();
        requester.setId(1L);

        User accepter = new User();
        accepter.setId(2L);
        accepter.setUserPhoneNumber("01084983484");

        PlanCreationInput planCreationInput = PlanCreationInput.builder()
                .planMenu("test")
                .planRestaurant("testaurant")
                .planTime(LocalDateTime.now())
                .requestMessage("this is just a test")
                .build();

        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(userRepository.findById(accepter.getId())).thenReturn(Optional.of(accepter));

        //when
        ServiceResult result = planService.createPlan(requester.getId(),
                accepter.getId(), planCreationInput);
        //then
        assertThat(result).isEqualTo(ServiceResult.success("약속이 생성되었습니다."));
        verify(planRepository).save(argThat(plan ->
                plan.getPlanMenu().equals(planCreationInput.getPlanMenu()) &&
                        plan.getPlanRestaurant().equals(planCreationInput.getPlanRestaurant()) &&
                        plan.getPlanTime().equals(planCreationInput.getPlanTime()) &&
                        plan.getRequestMessage().equals(planCreationInput.getRequestMessage())
        ));
    }

    @Test
    public void testPlanCreation_Fail_Exists() {
        //given
        User requester = new User();
        requester.setId(1L);

        User accepter = new User();
        accepter.setId(2L);
        accepter.setUserPhoneNumber("01084983484");

        PlanCreationInput planCreationInput = PlanCreationInput.builder()
                .planMenu("test")
                .planRestaurant("testaurant")
                .planTime(LocalDateTime.now())
                .requestMessage("this is just a test")
                .build();

        Plan existingPlan = new Plan();

        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(userRepository.findById(accepter.getId())).thenReturn(Optional.of(accepter));
        when(planRepository.findByRequesterIdAndAccepterIdAndPlanStatus(requester.getId(),
                accepter.getId(), PlanStatus.REQUESTED)).thenReturn(Optional.of(existingPlan));


        //when,then
        assertThatThrownBy(() -> planService.createPlan(requester.getId(),
                accepter.getId(), planCreationInput))
                .isInstanceOf(ExistingPlanException.class)
                .hasMessage("상대방에게 신청한 '요청중' 상태의 점심약속이 존재합니다. " +
                        "기존점심약속이 수락/거절되었거나 완료된 경우 다시 신청 할 수 있습니다.");
    }

    @Test
    public void testPlanCreation_Fail_NoUser() {
        //given
        User requester = new User();
        requester.setId(1L);

        User accepter = new User();
        accepter.setId(2L);
        accepter.setUserPhoneNumber("01084983484");

        PlanCreationInput planCreationInput = new PlanCreationInput();

        when(userRepository.findById(requester.getId())).thenReturn(Optional.empty());
        when(userRepository.findById(accepter.getId())).thenReturn(Optional.empty());

        //when,then
        assertThatThrownBy(() -> planService.createPlan(requester.getId(),
                accepter.getId(), planCreationInput))
                .isInstanceOf(UserInfoNotFoundException.class)
                .hasMessage("존재하지 않는 유저입니다.");
    }
}