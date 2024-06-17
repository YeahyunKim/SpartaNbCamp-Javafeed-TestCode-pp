package com.sparta.javafeed.service;

import com.sparta.javafeed.dto.CommentRequestDto;
import com.sparta.javafeed.dto.CommentResponseDto;
import com.sparta.javafeed.dto.SignupRequestDto;
import com.sparta.javafeed.entity.Comment;
import com.sparta.javafeed.entity.Newsfeed;
import com.sparta.javafeed.entity.User;
import com.sparta.javafeed.enums.UserStatus;
import com.sparta.javafeed.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private NewsfeedService newsfeedService;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private User mockUserSetup() {
        SignupRequestDto requestDto = SignupRequestDto.builder()
                .accountId("insidesy123")
                .password("Insidesyt123!@#")
                .email("insidesy123@gmail.com")
                .name("김예현")
                .build();
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        User testUser = new User(requestDto, encodedPassword);
        testUser.setId(1L);
        testUser.setUserStatus(UserStatus.BEFORE_VERIFIED);

        return testUser;
    }

    private Newsfeed getNewsfeed() {
        return new Newsfeed(1L, mockUserSetup(), "자바의 정석", "자바는 객체지향 프로그래밍 언어이다.", null);
    }

    private Comment getComment() {
        User user = mockUserSetup();
        Newsfeed newsfeed = getNewsfeed();
        String description = "자바는 어렵나요?? 궁금합니다.";

        return new Comment(user, newsfeed, description);
    }

    private List<Comment> createCommentLists() {
        Comment comment = getComment();
        List<Comment> commentList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            commentList.add(comment);
        }
        return commentList;
    }


    @Test
    @DisplayName("addComment() 댓글 등록 성공")
    void addComment() {
        // given
        Comment comment = getComment();

        given(newsfeedService.getNewsfeed(comment.getNewsfeed().getId())).willReturn(comment.getNewsfeed());

        // when
        CommentResponseDto commentResponse = commentService.addComment(comment.getUser(), comment.getNewsfeed().getId(), comment.getDescription());

        // then
        assertNotNull(commentResponse);
        assertEquals(commentResponse.getWriter(), comment.getUser().getName());
        assertEquals(commentResponse.getDescription(), comment.getDescription());
    }


    @Test
    @DisplayName("getComments()댓글 목록 조회 성공")
    void getComments() {
        // given
        Comment comment = getComment();
        List<Comment> commentList = createCommentLists();
        given(commentRepository.findAllByNewsfeedIdAndUser_UserStatus(comment.getNewsfeed().getId(), UserStatus.ACTIVE))
                .willReturn(commentList);

        // when
        List<CommentResponseDto> responseList = commentService.getComments(comment.getNewsfeed().getId());

        assertNotNull(responseList);
        assertEquals(responseList.size(), commentList.size());
    }

    @Test
    @DisplayName("updateComment() 댓글 수정 성공")
    void updateComment() {
        // given
        Comment comment = getComment();
        CommentRequestDto requestDto = new CommentRequestDto("파이썬으로 갈아타야 하나요?");

        given(commentRepository.findById(comment.getId()))
                .willReturn(Optional.of(comment));
        given(newsfeedService.getNewsfeed(comment.getNewsfeed().getId()))
                .willReturn(comment.getNewsfeed());

        // when
        CommentResponseDto responseDto = commentService
                .updateComment(comment.getId(), requestDto, comment.getUser());

        // then
        assertNotNull(responseDto);
        assertEquals(responseDto.getWriter(), comment.getUser().getName());
        assertEquals(responseDto.getDescription(), comment.getDescription());
    }

    @Test
    @DisplayName("deleteComment() 댓글 삭제")
    void deleteComment() {
        // given
        Comment comment = getComment();

        given(commentRepository.findById(comment.getId()))
                .willReturn(Optional.of(comment));
        given(newsfeedService.getNewsfeed(comment.getNewsfeed().getId()))
                .willReturn(comment.getNewsfeed());

        // when
        commentService.deleteComment(comment.getId(), comment.getUser());
    }


}