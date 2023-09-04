package com.grablunchtogether.domain;

import com.grablunchtogether.enums.PlanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.grablunchtogether.enums.PlanStatus.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@AuditOverride(forClass = BaseEntity.class)
@Entity
public class Plan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requesterId", referencedColumnName = "id")
    private User requester;

    @ManyToOne
    @JoinColumn(name = "accepterId", referencedColumnName = "id")
    private User accepter;

    @Column
    private String planMenu;

    @Column
    private String planRestaurant;

    @Column
    @Enumerated(EnumType.STRING)
    private PlanStatus planStatus;

    @Column
    private String requestMessage;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime planTime;

    // 수락/ 거절을 위한 메서드
    public void approve(Character approvalCode) {
        this.planStatus = approvalCode == 'Y' ? ACCEPTED : REJECTED;
    }

    public void cancel() {
        this.planStatus = CANCELED;
    }

    public void update(String planMenu, String planRestaurant, LocalDateTime planTime,
                       String requestMessage) {
        this.planMenu = planMenu;
        this.planRestaurant = planRestaurant;
        this.planTime = planTime;
        this.requestMessage = requestMessage;
    }

    public void expired() {
        this.planStatus = EXPIRED;
    }

    public void historyLoadComplete() {
        this.planStatus = HISTORY_LOADED_COMPLETED;
    }

    public void historyLoadCancel() {
        this.planStatus = HISTORY_LOADED_CANCELED;
    }
}
