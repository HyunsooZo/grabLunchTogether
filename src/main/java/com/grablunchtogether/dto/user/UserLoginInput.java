package com.grablunchtogether.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLoginInput {
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "아이디를 입력해 주세요.")
    private String userEmail;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    public String userPassword;
}
