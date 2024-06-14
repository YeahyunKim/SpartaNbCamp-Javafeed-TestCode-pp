package com.sparta.javafeed.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.javafeed.config.SecurityConfig;
import com.sparta.javafeed.controller.UserController;
import com.sparta.javafeed.dto.PasswordReqeustDto;
import com.sparta.javafeed.dto.PasswordUpdateDto;
import com.sparta.javafeed.dto.SignupRequestDto;
import com.sparta.javafeed.dto.UserInfoRequestDto;
import com.sparta.javafeed.entity.User;
import com.sparta.javafeed.jwt.JwtUtil;
import com.sparta.javafeed.security.UserDetailsImpl;
import com.sparta.javafeed.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(value = UserController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )
        })
public class UserControllerTest {
    private MockMvc mockMvc; // 컨트롤러 테스트를 위해 MockMvc 객체 생성 // 가짜 http요청을 보내기 위해 생성

    private Principal mockPrincipal; //가짜 인증을 위해 가짜 Principal 생성 //Principal은 현재 로그인된 사용자

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @MockBean
    JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
    }

    private void mockUserSetup() {
        SignupRequestDto requestDto = SignupRequestDto.builder()
                .accountId("insidesy123")
                .password("Insidesyt123!@#")
                .email("insidesy123@gmail.com")
                .name("김예현")
                .build();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        User user = new User(requestDto, encodedPassword);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        mockPrincipal = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }


    @Test
    @DisplayName("[POST] 회원 가입")
    void signupUser() throws Exception {
        // given
        SignupRequestDto requestDto = SignupRequestDto.builder()
                .accountId("insidesy123")
                .password("Insidesyt123!@#")
                .email("insidesy123@gmail.com")
                .name("김예현")
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto); //requestDto객체를 JSON형태로 변환

        // when, then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDtoJson)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @DisplayName("[PATCH] 회원 탈퇴")
    void deactiveUser() throws Exception {
        // given
        this.mockUserSetup();
        PasswordReqeustDto requestDto = new PasswordReqeustDto("Insidesyt123!@#");
        String requestDtoJson = objectMapper.writeValueAsString(requestDto);

        // when, then
        mockMvc.perform(patch("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDtoJson)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isAccepted())
                .andDo(print());
    }

    @Test
    @DisplayName("[POST] 로그아웃")
    void logout() throws Exception {
        // given
        this.mockUserSetup();

        // when, then
        mockMvc.perform(post("/users/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @DisplayName("[GET] 유저조회")
    void getUser() throws Exception {
        // given
        this.mockUserSetup();

        // when, then
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @DisplayName("[PUT] 유저 업데이트")
    void updateUser() throws Exception {
        // given
        this.mockUserSetup();
        UserInfoRequestDto requestDto = UserInfoRequestDto.builder()
                .name("김예현2")
                .email("insidesy9999@gmail.com")
                .intro("자바는 너무 재밌습니다.")
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);

        // when, then
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDtoJson)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }



    @Test
    @DisplayName("[PATCH] 비밀번호 수정")
    void updatePasswordSuccess() throws Exception {
        // given
        this.mockUserSetup();
        PasswordUpdateDto requestDto = new PasswordUpdateDto("Insidesyt123!@#", "iNSidesyt123!@#!@#");
        String passwordInfo = objectMapper.writeValueAsString(requestDto);

        // when, then
        mockMvc.perform(patch("/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passwordInfo)
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
