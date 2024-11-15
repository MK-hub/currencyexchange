package com.example.currencyexchange.logging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Aspect
@Configuration
@RequiredArgsConstructor
public class TimeTrackerAspect {

    @Value("${logging.threshold.time}")
    private long loggingThresholdTime;

    @Around("@annotation(trackTime)")
    public Object trackTime(ProceedingJoinPoint joinPoint, TrackTime trackTime) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        var object = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;

        if (time > loggingThresholdTime)
            log.info("Method: {} execution time: {}", methodName, time);

        return object;
    }
}
