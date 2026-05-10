package com.todo.exception;

import com.todo.utils.Result;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result<?> handleValidation(Exception e) {
        String msg = "参数校验失败";
        if (e instanceof MethodArgumentNotValidException m) {
            var f = m.getBindingResult().getFieldError();
            if (f != null) {
                msg = f.getDefaultMessage() != null ? f.getDefaultMessage() : msg;
            }
        } else if (e instanceof BindException b) {
            var f = b.getBindingResult().getFieldError();
            if (f != null) {
                msg = f.getDefaultMessage() != null ? f.getDefaultMessage() : msg;
            }
        }
        return Result.fail(400, msg);
    }

    @ExceptionHandler({
        MethodArgumentTypeMismatchException.class,
        HttpMessageNotReadableException.class
    })
    public Result<?> handleBadRequest(Exception e) {
        return Result.fail(400, "请求参数不合法");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgument(IllegalArgumentException e) {
        return Result.fail(400, e.getMessage() != null ? e.getMessage() : "参数错误");
    }

    @ExceptionHandler(IllegalStateException.class)
    public Result<?> handleIllegalState(IllegalStateException e) {
        return Result.fail(502, e.getMessage() != null ? e.getMessage() : "服务暂不可用");
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleAny(Exception e) {
        return Result.fail(500, e.getMessage() != null ? e.getMessage() : "服务器内部错误");
    }
}
