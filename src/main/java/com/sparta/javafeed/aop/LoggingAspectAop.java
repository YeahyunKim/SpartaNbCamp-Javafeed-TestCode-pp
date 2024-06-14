package com.sparta.javafeed.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.annotations.Comment;
import org.springframework.stereotype.Component;

// 1. @Aspect 어노테이션을 사용하여, LoggingAspectAop 클래스가 AOP의 규칙에 따라 실행되도록 선언
// 2. @Component 어노테이션을 통해 Bean으로 Java Container에 추가 (@Aspect 어노테이션은 Bean 클래스에서만 사용이 가능하기 때문)
// 3. @Slf4j 어노테이션을 사용하여 클래스에 로깅 기능을 추가
@Slf4j(topic = "LoggingAspectAop")
@Aspect
@Component
public class LoggingAspectAop {

}
