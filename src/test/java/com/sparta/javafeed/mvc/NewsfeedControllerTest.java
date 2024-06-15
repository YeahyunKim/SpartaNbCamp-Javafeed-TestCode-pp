package com.sparta.javafeed.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.javafeed.config.SecurityConfig;
import com.sparta.javafeed.controller.NewsfeedController;
import com.sparta.javafeed.controller.UserController;
import com.sparta.javafeed.dto.NewsfeedRequestDto;
import com.sparta.javafeed.dto.SignupRequestDto;
import com.sparta.javafeed.entity.User;
import com.sparta.javafeed.jwt.JwtUtil;
import com.sparta.javafeed.security.UserDetailsImpl;
import com.sparta.javafeed.service.NewsfeedService;
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
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = NewsfeedController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )
        })
class NewsfeedControllerTest {
    private MockMvc mockMvc; // 컨트롤러 테스트를 위해 MockMvc 객체 생성 // 가짜 http요청을 보내기 위해 생성

    private Principal mockPrincipal; //가짜 인증을 위해 가짜 Principal 생성 //Principal은 현재 로그인된 사용자

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    NewsfeedService newsfeedService;

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
    @DisplayName("[POST] 게시물 작성")
    void createNewsfeed() throws Exception {
        //given
        this.mockUserSetup();

        NewsfeedRequestDto requestDto = NewsfeedRequestDto.builder()
                .title("자바의 정석")
                .description("자바는 재밌다.")
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);

        //when, then
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                        .content(requestDtoJson)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @DisplayName("[GET] 게시물 목록 조회")
    void updateNewsfeed() throws Exception {
        //given
        String page = "1";
        String searchStartDate = "20240612";
        String searchEndDate = "20240614";

        //when, then
        mockMvc.perform(get("/posts")
                        .param("page", page)
                        .param("searchStartDate", searchStartDate)
                        .param("searchEndDate", searchEndDate)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("[PUT] 게시물 업데이트")
    void getNewsfeed() throws Exception {
        //given
        this.mockUserSetup();
        NewsfeedRequestDto requestDto = NewsfeedRequestDto.builder()
                .title("자바의 정석2")
                .description("자바는 재밌다2")
                .build();

        String requestDtoJson = objectMapper.writeValueAsString(requestDto);

        //when, then
        mockMvc.perform(put("/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDtoJson)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }



    @Test
    @DisplayName("[DELETE] 게시물 삭제")
    void deleteNewsfeed() throws Exception {
        //given
        this.mockUserSetup();

        //when, then
        mockMvc.perform(delete("/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

}