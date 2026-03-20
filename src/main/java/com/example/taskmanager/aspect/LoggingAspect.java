package com.example.taskmanager.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.example.taskmanager.service.TaskService.*(..))")
    public void taskServiceMethods(){}

    @Before("execution(* com.example.taskmanager.service.TaskService.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("{} called with args: {}",
                joinPoint.getSignature().getName(),
                joinPoint.getArgs());
    }

    @After("execution(* com.example.taskmanager.service.TaskService.*(..))")
    public void logAfter(JoinPoint joinPoint) {
        log.info("{} finished",
                joinPoint.getSignature().getName());
    }

    @Around("execution(* com.example.taskmanager.service.TaskService.*(..))")
    public Object measureTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        String methodName = pjp.getSignature().getName();

        try {
            Object result = pjp.proceed();

            long elapsed = System.currentTimeMillis() - start;
            log.info("{} completed seccessfully in {}ms", methodName, elapsed);

            return result;

        }catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            log.error(" {} failed after {}ms: {}",  methodName, elapsed, e.getMessage());
            throw e;
        }
    }

    @AfterThrowing(pointcut = "taskServiceMethods()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Exception ex) {
        log.error("Exception in {}: {}", joinPoint.getSignature().getName(),
                ex.getMessage());
    }
}
