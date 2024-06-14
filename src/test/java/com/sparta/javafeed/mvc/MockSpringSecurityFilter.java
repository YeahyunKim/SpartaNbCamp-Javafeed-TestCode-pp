package com.sparta.myselectshop.mvc;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

//Security가 동작하면 테스트를 하기 매우 까다롭다. 따라서 우리는 가짜 SecurityFilter를 생성하여 이 필터에서 Security를 통한 테스트를 진행할 수 있다.
public class MockSpringSecurityFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        //SecurityContextHolder = 인증 객체를 담고있는 컨텍스트를 담는 공간, 따라서 SecurityContext에 점근하기 위해서는 SecurityContextHolder 가 반드시 필요하다.
        SecurityContextHolder.getContext()
                .setAuthentication((Authentication) ((HttpServletRequest) req).getUserPrincipal());//SecurityContext 안에 가짜 인증 코드를 넣어줌
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
        SecurityContextHolder.clearContext();
    }
}