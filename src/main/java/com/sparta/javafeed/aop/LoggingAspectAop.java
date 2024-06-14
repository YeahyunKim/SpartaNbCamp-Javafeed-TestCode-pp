package com.sparta.javafeed.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

// 1. @Aspect 어노테이션을 사용하여, LoggingAspectAop 클래스가 AOP의 규칙에 따라 실행되도록 선언
// 2. @Component 어노테이션을 통해 Bean으로 Java Container에 추가 (@Aspect 어노테이션은 Bean 클래스에서만 사용이 가능하기 때문)
// 3. @Slf4j 어노테이션을 사용하여 클래스에 로깅 기능을 추가
@Slf4j(topic = "LoggingAspectAop")
@Aspect
@Component
public class LoggingAspectAop {

    // 모든 컨트롤러에서 작동시키기 위한 Pointcut
//    @Pointcut("execution(* com.sparta.javafeed.controller.UserController.*(..))")
//    private void allController() {}

    // 실행시킬 위치를 선택
    @Around("execution(* com.sparta.javafeed.controller..*.*(..))")
    public Object logRequestUrlHttpMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("실행");
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            log.info("Request URL: " + request.getRequestURL() + " HTTP Method: {}: " + request.getMethod());
        }
        // 메서드 실행
        Object result = joinPoint.proceed();

        // Response 정보 로깅
        log.info("Response: {}: " + result);

        return result;
    }
}
