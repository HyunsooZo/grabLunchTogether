package com.grablunchtogether.dto.user;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class UserDistanceDto {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("회원 거리 조회 Dto")
    public static class Dto {
        private String id;
        private String userEmail;
        private String userName;
        private double userRate;
        private String company;
        private String distance;

        public static Dto from(Object[] object) {
            return Dto.builder()
                    .id(object[4].toString())
                    .userName(object[1].toString())
                    .userEmail(object[0].toString())
                    .userRate(Double.parseDouble(object[2].toString()))
                    .company(object[3].toString())
                    .distance(String.format("%.1f", (Double) object[5]))
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("회원 거리 조회 응답")
    public static class Response {
        private List<Dto> aroundUsers;

        public static Response of(List<Dto> users) {
            return Response.builder().aroundUsers(users).build();
        }
    }
}