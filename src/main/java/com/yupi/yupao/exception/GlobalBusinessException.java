package com.yupi.yupao.exception;

import com.yupi.yupao.common.BaseResponse;
import com.yupi.yupao.common.ErrorCode;
import com.yupi.yupao.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
@Slf4j
public class GlobalBusinessException {


    @ExceptionHandler(WxException.class)
    public BaseResponse wxExceptionHandler(WxException e) {
        log.error("WxException",e.getMessage(), e);
        return Result.error(e.getCode(),e.getMessage(),e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse RuntimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return Result.error(ErrorCode.SYSTEM_ERROR,e.getMessage(),"");

    }
}
