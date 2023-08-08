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
public class UserOcrSignUpInput {
    //이미지 서버 부재로 프로젝트 내에 저장해둔 로컬 이미지 경로를 올리는 것으로 대체
    @NotBlank(message = "파일이름을 입력 해주세요.")
    private String imageName;

    @Size(min = 4, max = 12, message = "비밀번호는 4~12자 사이로 입력해주세요.")
    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String password;
}
