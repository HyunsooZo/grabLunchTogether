package com.grablunchtogether.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanCreationInput {
    @NotBlank(message = "메뉴는 필수입력 항목입니다.")
    private String planMenu;

    @NotBlank(message = "식당이름은 필수입력 항목입니다.")
    private String planRestaurant;

    private String requestMessage;

    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
    private LocalDateTime planTime;
}
