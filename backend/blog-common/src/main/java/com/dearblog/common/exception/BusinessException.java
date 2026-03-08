package com.dearblog.common.exception;

import com.dearblog.common.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 * 抛出此异常会被 GlobalExceptionHandler 捕获并返回对应的错误响应
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ResultCode resultCode;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    public BusinessException(String message) {
        super(message);
        this.resultCode = ResultCode.INTERNAL_ERROR;
    }
}
