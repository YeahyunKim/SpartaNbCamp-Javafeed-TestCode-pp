package com.sparta.javafeed.service;

import com.sparta.javafeed.dto.*;
import com.sparta.javafeed.entity.User;
import com.sparta.javafeed.enums.UserStatus;
import com.sparta.javafeed.jwt.JwtUtil;
import com.sparta.javafeed.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    private User testUser;


    @Test
    @Order(1)
    @DisplayName("signupUser() 회원가입")
    void signupUser() {
        // given
        SignupRequestDto requestDto =new SignupRequestDto(
                "insidesy1234", "rla159357qwe!@#", "김예현", "insidesy98@gmail.com");

        // when
        SignupResponseDto responseDto = userService.signupUser(requestDto);

        // then
        assertNotNull(responseDto);
        assertEquals(responseDto.getAccountId(), requestDto.getAccountId());
        assertEquals(responseDto.getEmail(), requestDto.getEmail());
        assertEquals(responseDto.getName(), requestDto.getName());
    }

    @Test
    @Order(2)
    @DisplayName("findByAccountId() AccountId로 회원 찾기")
    void findByAccountId() {
        // given
        String accountId = "insidesy1234";

        // when
        testUser = userService.findByAccountId(accountId);

        //then
        assertNotNull(testUser);
        assertEquals(testUser.getAccountId(), accountId);
    }

    @Test
    @Order(3)
    @DisplayName("getUser() 회원 조회")
    void getUser() {
        // given
        // when
        UserInfoResponseDto response = userService.getUser(testUser.getAccountId());

        // then
        assertNotNull(response);
        assertEquals(response.getEmail(), testUser.getEmail());
        assertEquals(response.getName(), testUser.getName());
    }

    @Test
    @Order(4)
    @DisplayName("updateUser() 회원 정보 수정")
    void updateUser() {
        // given
        UserInfoRequestDto request = new UserInfoRequestDto("김예현2","insidesy98@gmail.com", "한 줄 소개");

        // when
        userService.updateUser(request, testUser.getAccountId());

        // then
        UserInfoResponseDto response = userService.getUser(testUser.getAccountId());
        assertNotNull(response);
        assertEquals(response.getName(), request.getName());
        assertEquals(response.getIntro(), request.getIntro());
    }


    @Test
    @Order(5)
    @DisplayName("updatePassword() 비밀번호 변경")
    void updatePassword() {
        // given
        PasswordUpdateDto request = new PasswordUpdateDto("rla159357qwe!@#", "rla159357qwe!@#3");

        // when
        userService.updatePassword(request, testUser.getAccountId());

        // then
        testUser = userService.findByAccountId(testUser.getAccountId());
        assertNotNull(testUser);
        assertTrue(passwordEncoder.matches(request.getNewPassword(), testUser.getPassword()));
    }


    @Test
    @Order(6)
    @DisplayName("updateUserEmailSent() 이메일 전송")
    void updateUserEmailSent() {
        // given
        LocalDateTime sentAt = LocalDateTime.now();

        // when
        userService.updateUserEmailSent(testUser.getEmail(), sentAt);

        // then
        testUser = userService.findByEmail(testUser.getEmail());
        assertNotNull(testUser);
        assertEquals(testUser.getEmailSentAt(), sentAt);
    }

    @Test
    @Order(7)
    @DisplayName("updateUserStatus() 이메일 인증")
    void updateUserStatus() {
        // given
        EmailVerifyCheckRequestDto request = new EmailVerifyCheckRequestDto(testUser.getEmail(), "authNum");

        // when
        userService.updateUserStatus(request);
        testUser = userService.findByEmail(testUser.getEmail());

        // then
        assertNotNull(testUser);
        assertEquals(testUser.getUserStatus(), UserStatus.ACTIVE);
    }

    @Test
    @Order(8)
    @DisplayName("findByEmail() email로 회원 찾기")
    void findByEmail() {
        // given
        User testUser2 = testUser;
        // when
        testUser = userService.findByEmail(testUser.getEmail());

        // then
        assertNotNull(testUser);
        assertEquals(testUser.getEmail(), testUser2.getEmail());
        assertEquals(testUser.getName(), testUser2.getName());
    }


    @Test
    @Order(9)
    @DisplayName("logout() 로그아웃")
    void logout() {
        // given
        testUser.saveRefreshToken("refreshToken");

        // when
        userService.logout(testUser, "accessToken", "refreshToken");

        // then
        testUser = userService.findByEmail(testUser.getEmail());
        assertNotNull(testUser);
        assertTrue(testUser.getRefreshToken().isEmpty());
    }


    @Test
    @DisplayName("deactiveUser() 회원 탈퇴")
    void deactiveUser() {
        // given
        PasswordReqeustDto reqeust = new PasswordReqeustDto("rla159357qwe!@#3");

        // when
        userService.deactiveUser(reqeust, testUser.getAccountId());

        // then
        testUser = userService.findByEmail(testUser.getEmail());
        assertNotNull(testUser);
        assertEquals(UserStatus.DEACTIVATE, testUser.getUserStatus());
    }
}