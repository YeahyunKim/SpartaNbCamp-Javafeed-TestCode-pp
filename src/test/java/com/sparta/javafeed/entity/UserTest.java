package com.sparta.javafeed.entity;

import com.sparta.javafeed.dto.SignupRequestDto;
import com.sparta.javafeed.dto.UserInfoRequestDto;
import com.sparta.javafeed.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private User user;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    // @BeforeEach는 테스트 코드가 실행되기 전에 실행시켜주는 어노테이션이다. 유저 테스트를 진행하기 위해 먼저 유저를 생성해야하기 때문에, setUser() 메서드에 @BeforeEach를 걸어준다.
    @BeforeEach
    void setUser() {
        SignupRequestDto requestDto = SignupRequestDto.builder()
                .accountId("insidesy1234")
                .password("1q2w3e4r!@#$")
                .email("insidesy1234@gmail.com")
                .name("김예현")
                .build();

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        user = new User(requestDto, encodedPassword);
    }


    @Test
    @DisplayName("[updateUserStatus()] 업데이트 UserStatus")
    void updateUserStatus() {
        // given
        UserStatus userStatus = UserStatus.ACTIVE;

        // when
        user.updateUserStatus(userStatus);

        //then
        assertEquals(userStatus, user.getUserStatus());
    }


    @Test
    @DisplayName("[saveRefreshToken()] 리프레시 토큰 저장")
    void saveRefreshToken() {
        //given
        String refreshToken = "RefreshToken";

        //when
        user.saveRefreshToken(refreshToken);

        //then
        assertEquals(refreshToken, user.getRefreshToken());
    }


    @Test
    @DisplayName("[checkRefreshToken()] 리프레시 토큰 체크")
    void checkRefreshToken() {
        //given
        String refreshToken = "RefreshToken";
        user.saveRefreshToken(refreshToken);

        //when
        Boolean isValidToken = refreshToken.equals(user.getRefreshToken());

        //then
        assertTrue(isValidToken);
    }


    @Test
    @DisplayName("[updateUserInfo()] 유저 정보 업데이트")
    void updateUserInfo() {
        //given
        UserInfoRequestDto infoRequestDto = new UserInfoRequestDto("김예현2", "insidesy9@gmail.com", "개발공부는 어렵습니다.");

        //when
        user.updateUserInfo(infoRequestDto);

        //then
        assertEquals(infoRequestDto.getName(), user.getName());
        assertEquals(infoRequestDto.getEmail(), user.getEmail());
        assertEquals(infoRequestDto.getIntro(), user.getIntro());
    }


    @Test
    @DisplayName("[updatePassword()] 비밀번호 변경")
    void updatePassword() {
        // given
        String newPassword = "QWER!@#$1234";
        String encodedNewPassword = passwordEncoder.encode(newPassword);

        // when
        user.updatePassword(encodedNewPassword);

        // then
        assertTrue(passwordEncoder.matches(newPassword, user.getPassword()));
    }

}