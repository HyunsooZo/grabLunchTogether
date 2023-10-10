package com.grablunchtogether.domain;

import com.grablunchtogether.dto.userReview.UserReviewDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@AuditOverride(forClass = BaseEntity.class)
@Entity
public class UserReview extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reviewerId", referencedColumnName = "id")
    private User reviewerId;

    @ManyToOne
    @JoinColumn(name = "targetedId", referencedColumnName = "id")
    private User targetedId;

    @ManyToOne
    @JoinColumn(name = "planId", referencedColumnName = "id")
    private Plan planId;

    @Column
    private String reviewContent;

    @Column
    private Double rate;

    public void edit(UserReviewDto.UserReviewRequest userReviewEditInput) {
        this.reviewContent = userReviewEditInput.getReviewContent();
    }
}