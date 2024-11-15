package com.example.currencyexchange.config.cache;


import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CustomKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        var methodName = method.getName();
        var parameterString = Arrays.stream(params)
                .filter(Objects::nonNull)
                .map(this::serializeParams)
                .collect(Collectors.joining("-"));

        return String.format("%s_%s_%s", methodName, parameterString, cacheName(method));
    }

    private String serializeParams(Object param) {
        if (param instanceof List<?> listParam) {
            return listParam.stream()
                    .map(Object::toString)
                    .sorted()
                    .collect(Collectors.joining("_"));
        }
        return param.toString();
    }

    private String cacheName(Method method) {
        Cacheable cacheable = AnnotationUtils.findAnnotation(method, Cacheable.class);
        return (cacheable != null ? Arrays.toString(cacheable.value()) : "");
    }
}
