package com.sparta.javafeed.dto;

import com.sparta.javafeed.entity.Newsfeed;
import com.sparta.javafeed.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class NewsfeedResponseDtoTest {
    private NewsfeedResponseDto newsfeedResponseDto;

    @BeforeEach
    void setNewsfeedResponseDto() {
        Newsfeed testNewsfeed = setNewsfeed();
        newsfeedResponseDto = new NewsfeedResponseDto(testNewsfeed);
    }



    private Newsfeed setNewsfeed() {
        User testUser = setUser();
        NewsfeedRequestDto requestDto = NewsfeedRequestDto.builder()
                .title("자바의 정석")
                .description("자바는 객체지향 프로그래밍 언어 OOP 이다.")
                .build();

        return new Newsfeed(requestDto.getTitle(), requestDto.getDescription(), testUser);
    }



    private User setUser() {
        SignupRequestDto requestDto = SignupRequestDto.builder()
                .accountId("insidesy1234")
                .password("1q2w3e4r!@#$")
                .email("insidesy1234@gmail.com")
                .name("김예현")
                .build();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        return new User(requestDto, encodedPassword);
    }



    @Test
    @DisplayName("[toDto()] ResponseDto로 반환")
    void toDtoSuccess() {
        // given
        Newsfeed newsfeed = setNewsfeed();

        // when
        NewsfeedResponseDto responseDto = NewsfeedResponseDto.toDto(newsfeed);

        // then
        assertEquals(newsfeedResponseDto.getAccountId(), responseDto.getAccountId());
        assertEquals(newsfeedResponseDto.getTitle(), responseDto.getTitle());
        assertEquals(newsfeedResponseDto.getDescription(), responseDto.getDescription());
    }
}