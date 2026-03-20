package com.example.taskmanager.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AnnotationLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(AnnotationLoggingAspect.class);

    @Pointcut("@annotation(com.example.taskmanager.annotation.Loggable)")
    public void loggableMethods() {}

    @Around("loggableMethods()")
    public Object logExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        String methodName = pjp.getSignature().toShortString();

        try {
            log.info("[@Loggable] Starting: {}",  methodName);
            Object result = pjp.proceed();
            long elapsed = System.currentTimeMillis() - start;
            log.info(" [@Loggable] {} completed in {}ms", methodName, elapsed);
            return result;
        }catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            log.error(" [@Loggable] {} failed after {}ms: {}", methodName, elapsed, e.getMessage());
            throw e;
        }
    }

}
