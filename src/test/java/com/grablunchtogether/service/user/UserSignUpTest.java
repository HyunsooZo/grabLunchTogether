package com.grablunchtogether.service.user;

import com.grablunchtogether.common.exception.UserSignUpException;
import com.grablunchtogether.common.results.serviceResult.ServiceResult;
import com.grablunchtogether.configuration.springSecurity.JwtTokenProvider;
import com.grablunchtogether.domain.User;
import com.grablunchtogether.dto.geocode.GeocodeDto;
import com.grablunchtogether.dto.user.UserSignUpInput;
import com.grablunchtogether.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class UserSignUpTest {
    @Mock
    private UserRepository userRepository;
    private JwtTokenProvider jwtTokenProvider;
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        userService = new UserServiceImpl(userRepository,jwtTokenProvider);
    }

    @Test
    public void testUserSignUp_ExistingUser() {
        // Given
        UserSignUpInput userSignUpInput = UserSignUpInput.builder()
                .userEmail("existing@example.com")
                .build();

        GeocodeDto userCoordinate = new GeocodeDto();

        User existingUser = new User();
        when(userRepository.findByUserEmail(userSignUpInput.getUserEmail()))
                .thenReturn(Optional.of(existingUser));

        // When,Then
        assertThatThrownBy
                (() -> userService.userSignUp(userSignUpInput, userCoordinate))
                .isInstanceOf(UserSignUpException.class)
                .hasMessage("이미 존재하는 회원입니다.");

        verify(userRepository, times(1))
                .findByUserEmail(userSignUpInput.getUserEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUserSignUp_Success() {
        // Given
        UserSignUpInput userSignUpInput =
                UserSignUpInput.builder()
                        .userEmail("test@example.com")
                        .userName("Test User")
                        .userPassword("password")
                        .userPhoneNumber("123-4567-1111")
                        .company("Test Company")
                        .build();

        String phoneNumber = userSignUpInput.getUserPhoneNumber().replaceAll("-", "");

        GeocodeDto userCoordinate =
                GeocodeDto.builder()
                        .latitude(123.456)
                        .longitude(789.012)
                        .build();

        when(userRepository.findByUserEmail(anyString())).thenReturn(Optional.empty());

        // when
        ServiceResult result = userService.userSignUp(
                UserSignUpInput.builder()
                        .userEmail(userSignUpInput.getUserEmail())
                        .userName(userSignUpInput.getUserName())
                        .userPassword(userSignUpInput.getUserPassword())
                        .userPhoneNumber(phoneNumber)
                        .company(userSignUpInput.getCompany())
                        .build(), userCoordinate);

        // then
        assertThat(result).isEqualTo(ServiceResult.success("회원가입 완료"));
        verify(userRepository).findByUserEmail(eq("test@example.com"));
        verify(userRepository).save(argThat(user ->
                user.getUserEmail().equals("test@example.com") &&
                        user.getUserName().equals("Test User") &&
                        user.getUserPhoneNumber().equals(phoneNumber) &&
                        user.getCompany().equals("Test Company") &&
                        user.getLatitude() == 123.456 &&
                        user.getLongitude() == 789.012
        ));
    }
}