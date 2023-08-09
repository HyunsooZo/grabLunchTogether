package com.grablunchtogether.domain;

import com.grablunchtogether.dto.favoriteUser.FavoriteUserInput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class FavoriteUser {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User userId;

    @ManyToOne
    @JoinColumn(name = "favoriteUserId", referencedColumnName = "id")
    private User favoriteUserId;

    @Column
    private String nickName;

    @Column
    private LocalDateTime registeredAt;

    public void edit(FavoriteUserInput favoriteUserEditInput) {
        this.nickName = favoriteUserEditInput.getNickName();
    }
}
