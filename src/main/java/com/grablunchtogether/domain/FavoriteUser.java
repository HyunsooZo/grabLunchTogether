package com.grablunchtogether.domain;

import com.grablunchtogether.dto.favoriteUser.FavoriteUserDto;
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
public class FavoriteUser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User userId;

    @ManyToOne
    @JoinColumn(name = "favoriteUserId", referencedColumnName = "id")
    private User favoriteUserId;

    @Column
    private String nickName;

    public void setNickname(FavoriteUserDto.Request favoriteUserEditRequest) {
        this.nickName = favoriteUserEditRequest.getNickName();
    }
}
