package com.example.currencyexchange.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.annotation.AliasFor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@RequestMapping(method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
@ApiResponse(responseCode = "200", description = "OK")
@ApiResponse(responseCode = "400", description = "Invalid Request", content = @Content)
@ApiResponse(responseCode = "500", description = "Internal Error", content = @Content)
@ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
public @interface ApiDelete {

    @AliasFor(annotation = RequestMapping.class)
    String[] value() default {};
}
