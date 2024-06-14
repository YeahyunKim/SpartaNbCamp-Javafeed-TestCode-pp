package com.sparta.javafeed.dto;

import com.sparta.javafeed.entity.Newsfeed;
import com.sparta.javafeed.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class NewsfeedRequestDtoTest {

    private NewsfeedRequestDto requestDto;

    @BeforeEach
    void setNewsfeedRequestDto() {
        requestDto = new NewsfeedRequestDto("자바의 정석", "자바는 재밌다");
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
    @DisplayName("[toEntity()] Entity 생성")
    void toEntity() {
        // given
        User user = setUser();

        // when
        Newsfeed newsfeed = requestDto.toEntity(user);

        // then
        assertEquals(user, newsfeed.getUser());
        assertEquals(requestDto.getTitle(), newsfeed.getTitle());
        assertEquals(requestDto.getDescription(), newsfeed.getDescription());
    }
}