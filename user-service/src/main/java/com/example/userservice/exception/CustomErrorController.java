package com.example.userservice.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@RestController // вместо @Controller
@RequiredArgsConstructor
public class CustomErrorController implements ErrorController {
    private static final String PATH = "/error";

    private final ErrorAttributes errorAttributes; // теперь final, инжектится через конструктор

    @RequestMapping(PATH)
    public ResponseEntity<ErrorDTO> error(WebRequest request) {
        Map<String, Object> attribute = errorAttributes.getErrorAttributes(
                request,
                ErrorAttributeOptions.of(ErrorAttributeOptions.Include.EXCEPTION, ErrorAttributeOptions.Include.MESSAGE)
        );

        return ResponseEntity
                .status((Integer) attribute.get("status"))
                .body(ErrorDTO.builder()
                        .error((String) attribute.get("error"))
                        .errorDescription((String) attribute.get("message"))
                        .build()
                );
    }
}