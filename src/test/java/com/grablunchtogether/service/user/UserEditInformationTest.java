package com.grablunchtogether.service.user;

import com.grablunchtogether.common.exception.InvalidLoginException;
import com.grablunchtogether.common.exception.UserInfoNotFoundException;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.configuration.springSecurity.JwtTokenProvider;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.geocode.GeocodeDto;
import com.grablunchtogether.dto.user.UserInformationEditInput;
import com.grablunchtogether.repository.UserRepository;
import com.grablunchtogether.utility.PasswordUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserEditInformationTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    private UserServiceImpl userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        userService = new UserServiceImpl(userRepository, jwtTokenProvider);
    }

    @Test
    public void testEditUserInformation_Success() {
        // given
        Long userId = 1L;
        UserInformationEditInput userInformationEditInput =
                UserInformationEditInput.builder()
                        .userPassword("existingPassword")
                        .userPhoneNumber("123-456-7890")
                        .company("Test Company")
                        .build();

        GeocodeDto coordinate =
                GeocodeDto.builder().latitude(123.456).latitude(789.012).build();

        User existingUser = User.builder()
                .id(userId)
                .userPassword(PasswordUtility.getEncryptPassword("existingPassword"))
                .build();

        when(userRepository.findById(userId))
                .thenReturn(java.util.Optional.of(existingUser));

        // when
        ServiceResult result =
                userService.editUserInformation(userId, userInformationEditInput, coordinate);

        // then
        assertThat(result).isEqualTo(ServiceResult.success("고객정보 수정 완료"));
        assertThat(existingUser.getUserPhoneNumber()).isEqualTo("1234567890");
        assertThat(existingUser.getCompany()).isEqualTo("Test Company");
        assertThat(existingUser.getLatitude()).isEqualTo(coordinate.getLatitude());
        assertThat(existingUser.getLongitude()).isEqualTo(coordinate.getLongitude());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    public void testEditUserInformation_Fail_InvalidPassword() {
        // given
        Long userId = 1L;
        UserInformationEditInput userInformationEditInput = new UserInformationEditInput();
        userInformationEditInput.setUserPassword("wrongPassword");
        GeocodeDto coordinate = new GeocodeDto();

        User existingUser = new User();
        existingUser.changePassword(PasswordUtility.getEncryptPassword("existingPassword"));
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(existingUser));

        // when, then
        assertThatThrownBy(() -> userService.editUserInformation(userId, userInformationEditInput, coordinate))
                .isInstanceOf(InvalidLoginException.class)
                .hasMessage("기존 비밀번호가 일치하지 않습니다.");

        verify(userRepository, never()).save(existingUser);
    }

    @Test
    public void testEditUserInformation_Fail_UserInfoNotFound() {
        // given
        Long userId = 1L;
        UserInformationEditInput userInformationEditInput = new UserInformationEditInput();
        userInformationEditInput.setUserPassword("existingPassword");
        GeocodeDto coordinate = new GeocodeDto();

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        // when, then
        assertThatThrownBy(() -> userService.editUserInformation(userId, userInformationEditInput, coordinate))
                .isInstanceOf(UserInfoNotFoundException.class)
                .hasMessage("고객 정보를 찾을 수 없습니다. 다시 시도해 주세요.");

        verify(userRepository, never()).save(any());
    }
}