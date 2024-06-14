package com.sparta.javafeed.entity;

import com.sparta.javafeed.dto.CommentRequestDto;
import com.sparta.javafeed.dto.NewsfeedRequestDto;
import com.sparta.javafeed.dto.SignupRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {
    private Comment comment;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    // @BeforeEach는 테스트 코드가 실행되기 전에 실행시켜주는 어노테이션
    @BeforeEach
    void setComment() {
        User testUser = setUser();
        testUser.setId(1L);
        Newsfeed testNewsfeed = setNewsfeed();
        CommentRequestDto requestDto = CommentRequestDto.builder()
                .description("제가 자바를 공부해봐도 괜찮을까요?")
                .build();

        comment = new Comment(testUser, testNewsfeed, requestDto.getDescription());
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

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        return new User(requestDto, encodedPassword);
    }

    @Test
    @DisplayName("[validate()] 사용자 인증 성공")
    void validateSuccess() {
        //given
        User testUser = setUser();
        testUser.setId(1L);

        //when, then
        comment.validate(testUser);
    }



    @Test
    @DisplayName("[update()] 댓글 수정")
    void update() {
        // given
        String newDescription = "자바는 너무 어려워요";

        // when
        comment.update(newDescription);

        // then
        assertEquals(newDescription, comment.getDescription());
    }



    @Test
    @DisplayName("[increaseLikeCnt()] 좋아요 증가")
    void increaseLikeCnt() {
        // given
        int increaseCount = 8;

        // when
        for (int i = 0; i < increaseCount; i++) {
            comment.increaseLikeCnt();
        }

        // then
        assertEquals(increaseCount, comment.getLikeCnt());
    }



    @Test
    @DisplayName("[decreaseLikeCnt()] 좋아요 감소")
    void decreaseLikeCnt() {
        // given
        int increaseCount = 4;
        int decreaseCount = 2;
        for (int i = 0; i < increaseCount; i++) {
            comment.increaseLikeCnt();
        }

        // when
        for (int i = 0; i < decreaseCount; i++) {
            comment.decreaseLikeCnt();
        }

        // then
        assertEquals(increaseCount - decreaseCount, comment.getLikeCnt());
    }



}