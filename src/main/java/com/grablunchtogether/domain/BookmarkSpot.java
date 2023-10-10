package com.grablunchtogether.domain;

import com.grablunchtogether.dto.bookmarkSpot.BookmarkSpotDto;
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
public class BookmarkSpot extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User userId;

    @Column
    private String restaurant;

    @Column
    private String menu;

    @Column
    private String address;

    @Column
    private String operationHour;

    @Column
    private String rate;

    public static BookmarkSpot of(BookmarkSpotDto.Request bookmarkSpotRequest, User user) {
        return BookmarkSpot.builder()
                .restaurant(bookmarkSpotRequest.getRestaurant())
                .userId(user)
                .menu(bookmarkSpotRequest.getMenu())
                .rate(bookmarkSpotRequest.getRate())
                .operationHour(bookmarkSpotRequest.getOperationHour())
                .address(bookmarkSpotRequest.getAddress())
                .build();
    }
}
