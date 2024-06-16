package com.sparta.javafeed.service;

import com.sparta.javafeed.dto.PasswordReqeustDto;
import com.sparta.javafeed.dto.SignupRequestDto;
import com.sparta.javafeed.dto.SignupResponseDto;
import com.sparta.javafeed.dto.UserInfoResponseDto;
import com.sparta.javafeed.entity.Comment;
import com.sparta.javafeed.entity.Newsfeed;
import com.sparta.javafeed.entity.User;
import com.sparta.javafeed.enums.ErrorType;
import com.sparta.javafeed.enums.UserStatus;
import com.sparta.javafeed.exception.CustomException;
import com.sparta.javafeed.jwt.JwtUtil;
import com.sparta.javafeed.repository.CommentRepository;
import com.sparta.javafeed.repository.UserRepository;
import com.sparta.javafeed.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.BDDMockito.given;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mock
    private JwtUtil jwtUtil;

    private User mockUserSetup() {
        SignupRequestDto requestDto = SignupRequestDto.builder()
                .accountId("insidesy123")
                .password("Insidesyt123!@#")
                .email("insidesy123@gmail.com")
                .name("김예현")
                .build();
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        User testUser = new User(requestDto, encodedPassword);
        testUser.setUserStatus(UserStatus.BEFORE_VERIFIED);

        return new User(requestDto, encodedPassword);
    }

    @Test
    @DisplayName("signupUser() 회원 가입")
    void signupUser() throws Exception {
        // given
        SignupRequestDto requestDto =new SignupRequestDto(
                "insidesy123", "Insidesyt123!@#", "김예현", "insidesy123@gmail.com");

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        User user = mockUserSetup();

        given(passwordEncoder.encode(requestDto.getPassword())).willReturn(encodedPassword);

        // when
        SignupResponseDto responseDto = userService.signupUser(requestDto);

        // then
        assertEquals(responseDto.getPassword(), encodedPassword);
        assertEquals(responseDto.getAccountId(), user.getAccountId());
        assertEquals(responseDto.getEmail(), user.getEmail());
        assertEquals(responseDto.getName(), user.getName());
    }


    @Test
    @DisplayName("signupUser() 회원 가입 - 아이디 중복 예외")
    void signupUserThrowsExceptionForDuplicateAccountId() {
        // given
        SignupRequestDto requestDto = new SignupRequestDto("duplicateAccountId", "test@example.com", "김예현", "insidesy1234@gmail.com");
        User existingUser = new User();
        existingUser.setAccountId("duplicateAccountId");

        given(userRepository.findByAccountId(requestDto.getAccountId()))
                .willReturn(Optional.of(existingUser));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.signupUser(requestDto);
        });

        assertEquals(ErrorType.DUPLICATE_ACCOUNT_ID, exception.getErrorType());
    }


    @Test
    @DisplayName("signupUser() 회원 가입 - 이메일 중복 예외")
    void signupUserThrowsExceptionForDuplicateEmail() {
        // given
        SignupRequestDto requestDto = new SignupRequestDto("newAccountId", "duplicate@example.com", "김예현", "insidesy123@gmail.com");
        User existingUser = new User();
        existingUser.setEmail("duplicate@example.com");

        given(userRepository.findByEmail(requestDto.getEmail()))
                .willReturn(Optional.of(existingUser));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.signupUser(requestDto);
        });

        assertEquals(ErrorType.DUPLICATE_EMAIL, exception.getErrorType());
    }


    @Test
    @DisplayName("deactiveUser() 회원 탈퇴")
    void deactiveUser() throws Exception {
        // given
        User testUser = mockUserSetup();
        PasswordReqeustDto reqeustDto = new PasswordReqeustDto("Insidesyt123!@#");

        given(userRepository.findByAccountId(testUser.getAccountId()))
                .willReturn(Optional.of(testUser));

        given(passwordEncoder.matches(reqeustDto.getPassword(), testUser.getPassword()))
                .willReturn(true);

        // when
        userService.deactiveUser(reqeustDto, testUser.getAccountId());

        // then
        assertEquals(testUser.getUserStatus(), UserStatus.DEACTIVATE);
    }


    @Test
    @DisplayName("deactiveUser() 회원 탈퇴 - 이미 가입한 유저 예외")
    void deactiveUserExceptionForDeactiveUser() {
        // given
        User testUser = mockUserSetup();
        testUser.updateUserStatus(UserStatus.DEACTIVATE); // 이미 탈퇴된 상태로 설정
        PasswordReqeustDto requestDto = new PasswordReqeustDto("Insidesyt123!@#");

        given(userRepository.findByAccountId(testUser.getAccountId()))
                .willReturn(Optional.of(testUser));

        // when, then
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.deactiveUser(requestDto, testUser.getAccountId());
        });

        assertEquals(ErrorType.DEACTIVATE_USER, exception.getErrorType());
    }


    @Test
    @DisplayName("deactiveUser() 회원 탈퇴 - 비밀번호 불일치 예외")
    void deactiveUserThrowsExceptionForInvalidPassword() {
        // given
        User testUser = mockUserSetup();
        PasswordReqeustDto requestDto = new PasswordReqeustDto("wrongPassword123");

        given(userRepository.findByAccountId(testUser.getAccountId()))
                .willReturn(Optional.of(testUser));

        given(passwordEncoder.matches(requestDto.getPassword(), testUser.getPassword()))
                .willReturn(false);

        // when, then
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.deactiveUser(requestDto, testUser.getAccountId());
        });

        assertEquals(ErrorType.INVALID_PASSWORD, exception.getErrorType());
    }


    @Test
    @DisplayName("getUser() 사용자 조회")
    void getUser() {
        // given
        User testUser = mockUserSetup();

        given(userRepository.findByAccountId(testUser.getAccountId()))
                .willReturn(Optional.of(testUser));

        // when
        UserInfoResponseDto responseDto = userService.getUser(testUser.getAccountId());

        // then
        assertNotNull(responseDto);
        assertEquals(testUser.getAccountId(), responseDto.getAccountId());
        assertEquals(testUser.getEmail(), responseDto.getEmail());
    }

}