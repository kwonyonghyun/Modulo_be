package com.example.Modulo.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LogAop {


    @Around("(execution(* com.example.Modulo.controller..*(..)) || execution(* com.example.Modulo..service..*(..))) && !execution(* com.example.Modulo..controller.TestController.*(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("START: {}", joinPoint.toString());
        try {
            return joinPoint.proceed();
        } finally {
            log.info("END: {}", joinPoint.toString());
        }
    }
}