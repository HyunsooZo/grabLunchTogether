package com.grablunchtogether.dto.plan;

import com.grablunchtogether.enums.PlanStatus;
import com.grablunchtogether.domain.Plan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanDto {

    private long id;
    private long requesterId;
    private long accepterId;
    private String requesterEmail;
    private String accepterEmail;
    private String planMenu;
    private String planRestaurant;
    private PlanStatus planStatus;
    private LocalDateTime planTime;

    public static PlanDto of(Plan plan) {
        return PlanDto.builder()
                .id(plan.getId())
                .requesterId(plan.getRequester().getId())
                .accepterId(plan.getAccepter().getId())
                .requesterEmail(plan.getRequester().getUserEmail())
                .accepterEmail(plan.getAccepter().getUserEmail())
                .planMenu(plan.getPlanMenu())
                .planRestaurant(plan.getPlanRestaurant())
                .planStatus(plan.getPlanStatus())
                .planTime(plan.getPlanTime())
                .build();
    }
}
