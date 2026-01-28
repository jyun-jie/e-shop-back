package com.shop.component;

import com.shop.Exception.AccessDeniedException;
import com.shop.Exception.BusinessException;
import com.shop.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {
        log.error("系統執行異常: ", e);
        return Result.error(e.getMessage()); // 自動轉成 JSON 給前端
    }

    @ExceptionHandler(NoSuchElementException.class)
    public Result handleNotFound(NoSuchElementException e) {
        return Result.error("查無資料: " + e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        return Result.error("申請錯誤" + e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result handleAccessDeniedException(AccessDeniedException e) {return Result.error("不符合角色"+ e.getMessage());}
}
