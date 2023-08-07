package com.grablunchtogether.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserChangePasswordInput {

    @Size(min = 4, max = 12, message = "비밀번호는 4~12자 사이로 입력해주세요.")
    @NotBlank(message = "기존 비밀번호는 필수입력 항목입니다.")
    private String userExistingPassword;

    @Size(min = 4, max = 12, message = "비밀번호는 4~12자 사이로 입력해주세요.")
    @NotBlank(message = "신규 비밀번호는 필수입력 항목입니다.")
    private String userNewPassword;
}
