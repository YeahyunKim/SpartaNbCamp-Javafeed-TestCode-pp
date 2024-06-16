package com.sparta.javafeed.service;

import com.sparta.javafeed.dto.NewsfeedRequestDto;
import com.sparta.javafeed.dto.NewsfeedResponseDto;
import com.sparta.javafeed.dto.SignupRequestDto;
import com.sparta.javafeed.entity.Newsfeed;
import com.sparta.javafeed.entity.User;
import com.sparta.javafeed.enums.UserStatus;
import com.sparta.javafeed.exception.CustomException;
import com.sparta.javafeed.repository.NewsfeedRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NewsfeedServiceTest {
    @InjectMocks
    private NewsfeedService newsfeedService;

    @Mock
    private NewsfeedRepository newsfeedRepository;

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
        testUser.setUserStatus(UserStatus.BEFORE_VERIFIED);

        return new User(requestDto, encodedPassword);
    }

    private List<Newsfeed> createNewsfeedList() {
        List<Newsfeed> newsfeedList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            newsfeedList.add(new Newsfeed("자바의정석" + i, "자바는 재밌다" + i, mockUserSetup()));
        }
        return newsfeedList;
    }


    @Test
    @DisplayName("save() - 게시물 생성")
    void save() {
        // given
        NewsfeedRequestDto requestDto = new NewsfeedRequestDto("자바의 정석", "자바는 객체지향 프로그래밍이다.");
        User user = mockUserSetup();
        Newsfeed newsfeed = requestDto.toEntity(user);

        given(newsfeedRepository.save(any(Newsfeed.class))).willReturn(newsfeed);

        // when
        NewsfeedResponseDto responseDto = newsfeedService.save(requestDto, user);

        // then
        assertNotNull(responseDto);
        assertEquals(requestDto.getTitle(), responseDto.getTitle());
        assertEquals(requestDto.getDescription(), responseDto.getDescription());
    }

    @Test
    @DisplayName("getNewsfeed() - 게시물 조회")
    void getNewsfeed() {
        // given
        int page = 1;
        String searchStartDate = "20240101";
        String searchEndDate = "20241231";

        LocalDateTime startDateTime = LocalDate.parse(searchStartDate, DateTimeFormatter.ofPattern("yyyyMMdd")).atTime(0, 0, 0);
        LocalDateTime endDateTime = LocalDate.parse(searchEndDate, DateTimeFormatter.ofPattern("yyyyMMdd")).atTime(23, 59, 59);

        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "createdAt");
        Pageable pageable = PageRequest.of(page, 10, sort);

        List<Newsfeed> newsfeedList = createNewsfeedList();

        given(newsfeedRepository
                .findAllByCreatedAtBetweenAndUser_UserStatus(startDateTime, endDateTime, pageable, UserStatus.ACTIVE)
        ).willReturn(newsfeedList);

        given(newsfeedRepository.findAllByCreatedAtBetweenAndUser_UserStatus(startDateTime, endDateTime, pageable, UserStatus.ACTIVE))
                .willReturn(newsfeedList);

        // when
        Page<NewsfeedResponseDto> responseDto = newsfeedService.getNewsfeed(page, searchStartDate, searchEndDate);

        // then
        assertNotNull(responseDto);
        assertEquals(10, responseDto.getTotalElements());
        assertEquals(newsfeedList.get(0).getTitle(), responseDto.getContent().get(0).getTitle());
        assertEquals(newsfeedList.get(0).getDescription(), responseDto.getContent().get(0).getDescription());
        assertEquals(newsfeedList.size(), responseDto.getTotalElements());
    }

    @Test
    @DisplayName("updateNewsfeed() - 게시글 수정 성공")
    void updateNewsfeed() {
        // given
        User user = mockUserSetup();

        Long id = 1L;
        NewsfeedRequestDto newsfeedRequest = NewsfeedRequestDto.builder()
                .title("자바의 정석")
                .description("자바는 재밌다")
                .build();
        Newsfeed newsfeed = new Newsfeed(id, user, "자바의 정석2", "자바는 재밌다2", null);

        given(newsfeedRepository.findById(id))
                .willReturn(Optional.of(newsfeed));

        // when
        Long newsfeedId = newsfeedService.updateNewsfeed(id, newsfeedRequest, user);

        // then
        assertNotNull(newsfeedId);
        assertEquals(id, newsfeedId);
    }

    @Test
    @DisplayName("deleteNewsfeed() 게시글 삭제")
    void deleteNewsfeed() {
        // given
        Long id = 1L;
        Newsfeed newsfeed = new Newsfeed(id, mockUserSetup(), "자바의 정석", "자바는 재밌다", null);

        given(newsfeedRepository.findById(id))
                .willReturn(Optional.of(newsfeed));

        // when
        Long newsfeedId = newsfeedService.deleteNewsfeed(id, mockUserSetup());

        // then
        assertNotNull(newsfeedId);
        assertEquals(id, newsfeedId);
    }


    @Test
    @DisplayName("findNewsfeed() - 게시글 찾기")
    void findNewsfeedSuccess() {
        // given
        Long id = 1L;
        Newsfeed newsfeed = new Newsfeed(id, mockUserSetup(), "title", "description", null);

        given(newsfeedRepository.findByIdAndUser_UserStatus(id, UserStatus.ACTIVE))
                .willReturn(Optional.of(newsfeed));

        // when
        Newsfeed getNewsfeed = newsfeedService.getNewsfeed(id);

        //then
        assertNotNull(getNewsfeed);
        assertEquals(newsfeed, getNewsfeed);
    }


    @Test
    @DisplayName("checkValidatedNewsfeed() - 게시글 유효성 검사 및 조회")
    void checkValidatedNewsfeed() {
        // given
        Long id = 1L;
        User user = mockUserSetup();
        Newsfeed newsfeed = new Newsfeed(id, user, "제목", "내용", null);

        given(newsfeedRepository.findById(id)).willReturn(Optional.of(newsfeed));

        // when
        Newsfeed result = newsfeedService.checkValidatedNewsfeed(id, user);

        // then
        assertNotNull(result);
        assertEquals(newsfeed, result);
    }

    @Test
    @DisplayName("checkValidatedNewsfeed() - 게시글 유효성 검사 실패")
    void checkValidatedNewsfeedFail() {
        // given
        Long id = 1L;
        User user = mockUserSetup();

        given(newsfeedRepository.findById(id)).willReturn(Optional.empty());

        // when & then
        assertThrows(CustomException.class, () -> newsfeedService.checkValidatedNewsfeed(id, user));
    }

    @Test
    @DisplayName("getNewsfeed() - 게시글 조회 실패")
    void getNewsfeedFail() {
        // given
        Long postId = 1L;

        given(newsfeedRepository.findByIdAndUser_UserStatus(postId, UserStatus.ACTIVE))
                .willReturn(Optional.empty());

        // when & then
        assertThrows(CustomException.class, () -> newsfeedService.getNewsfeed(postId));
    }
}