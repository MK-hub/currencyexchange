package com.example.currencyexchange.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Aspect
@Component
@Slf4j
public class EndpointAspect {

    private final ObjectMapper mapper = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setSerializationInclusion(NON_NULL);
        mapper.setSerializationInclusion(NON_EMPTY);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        mapper.setDateFormat(dateFormat);
        return mapper;
    }

    @Before("within(com.example.currencyexchange.controller..*)")
    public void endpointBefore(JoinPoint p) {
        Object[] signatureArgs = p.getArgs();
        try {
            List<String> argList = new ArrayList<>();
            for (Object arg : signatureArgs) {
                argList.add(arg != null ? mapper.writeValueAsString(arg) : "null");
            }
            if (signatureArgs.length != 0 && signatureArgs[0] != null) {
                log.info("START {} {}\nRequest object: \n{}", p.getTarget().getClass().getSimpleName(),
                        p.getSignature().getName(), argList);
            } else {
                log.info("START {} {}", p.getTarget().getClass().getSimpleName(), p.getSignature().getName());
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    @AfterReturning(value = ("within(com.example.currencyexchange.controller..*)"),
            returning = "returnValue")
    public void endpointAfterReturning(JoinPoint p, Object returnValue) {
        try {
            log.info("END {} {}\nResponse object: \n{}", p.getTarget().getClass().getSimpleName(),
                    p.getSignature().getName(), mapper.writeValueAsString(returnValue));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }
}
