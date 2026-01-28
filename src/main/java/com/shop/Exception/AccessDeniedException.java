package com.shop.Exception;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Data
public class AccessDeniedException extends RuntimeException{

    public AccessDeniedException(String message) {
            super(message);
        }

}
