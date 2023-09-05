package com.grablunchtogether.dto.plan;

import com.grablunchtogether.domain.Plan;
import com.grablunchtogether.enums.PlanStatus;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;


public class PlanDto {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("Plan 요청")
    public static class Request {
        @NotBlank(message = "메뉴는 필수입력 항목입니다.")
        private String planMenu;

        @NotBlank(message = "식당이름은 필수입력 항목입니다.")
        private String planRestaurant;

        private String requestMessage;

        @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
        private LocalDateTime planTime;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("Plan Dto")
    public static class Dto {
        private long id;
        private long requesterId;
        private long accepterId;
        private String requesterEmail;
        private String accepterEmail;
        private String planMenu;
        private String planRestaurant;
        private PlanStatus planStatus;
        private LocalDateTime planTime;

        public static Dto of(Plan plan) {
            return Dto.builder()
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

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("Plan 응답")
    public static class Response {
        private List<Dto> plans;

        public static Response of(List<Dto> plans) {
            return Response.builder().plans(plans).build();
        }
    }
}
