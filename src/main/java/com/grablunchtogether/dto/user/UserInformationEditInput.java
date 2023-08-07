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
public class UserInformationEditInput {
    @Size(min = 4, max = 12, message = "비밀번호는 4~12자 사이로 입력해주세요.")
    @NotBlank(message = "기존 비밀번호는 필수입력 항목입니다.")
    private String userPassword;

    @Size(min = 10, max = 12, message = "휴대전화번호는 10~12자로 입력해주세요.")
    @NotBlank(message = "휴대폰번호는 필수입력 항목입니다.")
    private String userPhoneNumber;

    @NotBlank(message = "회사이름은 필수입력 항목입니다.")
    private String company;

    @NotBlank(message = "회사 주소는 필수입력 항목입니다.")
    private String address;

    @NotBlank(message = "회사 주소는 필수입력 항목입니다.")
    private String streetNumber;
}