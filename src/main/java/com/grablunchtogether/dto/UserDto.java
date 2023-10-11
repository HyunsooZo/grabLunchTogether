package com.grablunchtogether.dto;

import com.grablunchtogether.domain.User;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserDto {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("회원 로그인 요청")
    public static class LoginRequest {
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @NotBlank(message = "아이디를 입력해 주세요.")
        private String userEmail;

        @NotBlank(message = "비밀번호를 입력해 주세요.")
        public String userPassword;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("회원가입 요청")
    public static class SignUpRequest {
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @NotBlank(message = "이메일은 필수입력 항목입니다.")
        private String userEmail;

        @Size(min = 4, max = 12, message = "비밀번호는 4~12자 사이로 입력해주세요.")
        @NotBlank(message = "비밀번호는 필수입력 항목입니다.")
        private String userPassword;

        @Size(min = 2, max = 10, message = "이름은 2~10자 사이로 입력해주세요.")
        @NotBlank(message = "이름은 필수입력 항목입니다.")
        private String userName;

        @Size(min = 10, max = 12, message = "휴대전화번호는 10~12자로 입력해주세요.")
        @NotBlank(message = "휴대폰번호는 필수입력 항목입니다.")
        private String userPhoneNumber;

        @NotBlank(message = "회사이름은 필수입력 항목입니다.")
        private String company;

        @NotBlank(message = "회사 주소는 필수입력 항목입니다.")
        private String streetName;

        @NotBlank(message = "회사 주소는 필수입력 항목입니다.")
        private String streetNumber;

        private String profileUrl;

        private String nameCardUrl;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("OCR 회원가입 요청")
    public static class OcrSignUpRequest {
        //이미지 업로드 후 얻은 명함을 사용
        @NotBlank(message = "파일이름을 입력 해주세요.")
        private String imageName;

        @Size(min = 4, max = 12, message = "비밀번호는 4~12자 사이로 입력해주세요.")
        @NotBlank(message = "비밀번호를 입력해 주세요.")
        private String password;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("비밀번호 초기화 메세지")
    public static class PasswordResetMessage {
        private String to;
        private String subject;
        private String randomPassword;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("비밀번호 초기화 요청")
    public static class PasswordResetRequest {
        private String email;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("회원 정보 수정 요청")
    public static class InfoEditRequest {
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

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("비밀번호 변경 요청")
    public static class PasswordChangeRequest {

        @Size(min = 4, max = 12, message = "비밀번호는 4~12자 사이로 입력해주세요.")
        @NotBlank(message = "기존 비밀번호는 필수입력 항목입니다.")
        private String userExistingPassword;

        @Size(min = 4, max = 12, message = "비밀번호는 4~12자 사이로 입력해주세요.")
        @NotBlank(message = "신규 비밀번호는 필수입력 항목입니다.")
        private String userNewPassword;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("회원 Dto")
    public static class Dto {
        private Long id;
        private String userEmail;
        private String userName;
        private String userPhoneNumber;
        private double userRate;
        private String company;
        private String profileUrl;
        private String nameCardUrl;
        private double latitude;
        private double longitude;

        public static Dto from(User user) {
            return Dto.builder()
                    .id(user.getId())
                    .userEmail(user.getUserEmail())
                    .userName(user.getUserName())
                    .userPhoneNumber(user.getUserPhoneNumber())
                    .userRate(user.getUserRate())
                    .nameCardUrl(user.getNameCardUrl())
                    .profileUrl(user.getProfileUrl())
                    .company(user.getCompany())
                    .latitude(user.getLatitude())
                    .longitude(user.getLongitude())
                    .build();
        }
    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("회원 탈퇴 요청")
    public static class WithdrawalRequest {
        @NotBlank(message = "비밀번호를 입력해 주세요.")
        public String userPassword;
    }
}