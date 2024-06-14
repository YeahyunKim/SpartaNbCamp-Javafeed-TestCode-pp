package com.sparta.javafeed.entity;

import com.sparta.javafeed.dto.NewsfeedRequestDto;
import com.sparta.javafeed.dto.SignupRequestDto;
import com.sparta.javafeed.dto.UserInfoRequestDto;
import com.sparta.javafeed.enums.ErrorType;
import com.sparta.javafeed.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class NewsfeedTest {
    private Newsfeed newsfeed;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    // @BeforeEach는 테스트 코드가 실행되기 전에 실행시켜주는 어노테이션이다. 뉴스피드 테스트를 진행하기 위해 먼저 유저를 생성하고 뉴스피드를 생성해야하기 때문에, setNewsfeed() 메서드에 @BeforeEach를 걸어준다.
    @BeforeEach
    void setNewsfeed() {
        User user = setUser();
        NewsfeedRequestDto requestDto = NewsfeedRequestDto.builder()
                .title("자바의 정석")
                .description("자바는 객체지향 프로그래밍 언어 OOP 이다.")
                .build();

        newsfeed = new Newsfeed(requestDto.getTitle(), requestDto.getDescription(), user);
    }


    private User setUser() {
        SignupRequestDto requestDto = SignupRequestDto.builder()
                .accountId("insidesy1234")
                .password("1q2w3e4r!@#$")
                .email("insidesy1234@gmail.com")
                .name("김예현")
                .build();

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        return new User(requestDto, encodedPassword);
    }


    @Test
    @DisplayName("[update()] 업데이트 뉴스피드")
    void update() {
        //given
        NewsfeedRequestDto requestDto = new NewsfeedRequestDto("파이썬의 정석", "파이썬으로 갈아탈까?");

        //when
        newsfeed.update(requestDto);

        //then
        assertEquals(requestDto.getTitle(), newsfeed.getTitle());
        assertEquals(requestDto.getDescription(), newsfeed.getDescription());
    }


    @Test
    @DisplayName("[userValidate()] 유저 검증 성공")
    void userValidateSuccess() {
        //given
        User user = setUser();

        //when,then
        newsfeed.userValidate(user);
    }


    @Test
    @DisplayName("[userValidate()] 유저 검증 실패")
    void userValidateFail() {
        // given
        User user = setUser();

        UserInfoRequestDto requestDto = UserInfoRequestDto.builder()
                .name("김예현2")
                .build();

        user.updateUserInfo(requestDto);

        // when, then
        CustomException exception = assertThrows(CustomException.class, () -> newsfeed.userValidate(user));
        assertEquals(ErrorType.NO_AUTHENTICATION, exception.getErrorType());
    }
}