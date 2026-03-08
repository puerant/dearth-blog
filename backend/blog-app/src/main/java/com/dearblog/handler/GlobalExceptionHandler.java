package com.dearblog.handler;

import com.dearblog.common.exception.BusinessException;
import com.dearblog.common.result.Result;
import com.dearblog.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一捕获异常并转换为 Result 响应，HTTP 状态码始终为 200
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常：由代码主动抛出，属于预期内的错误
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: [{}] {}", e.getResultCode().getCode(), e.getMessage());
        return Result.fail(e.getResultCode(), e.getMessage());
    }

    /**
     * 参数校验失败（@Valid 注解触发）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("；"));
        log.warn("参数校验失败: {}", message);
        return Result.fail(ResultCode.BAD_REQUEST, message);
    }

    /**
     * 请求体解析失败（JSON 格式错误等）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("请求体解析失败: {}", e.getMessage());
        return Result.fail(ResultCode.BAD_REQUEST, "请求体格式错误");
    }

    /**
     * 缺少必要请求参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<?> handleMissingParam(MissingServletRequestParameterException e) {
        return Result.fail(ResultCode.BAD_REQUEST, "缺少必要参数: " + e.getParameterName());
    }

    /**
     * 请求方法不支持（如用 GET 访问 POST 接口）
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return Result.fail(ResultCode.METHOD_NOT_ALLOWED);
    }

    /**
     * 文件上传超过大小限制
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<?> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        return Result.fail(ResultCode.FILE_SIZE_EXCEEDED);
    }

    /**
     * 兜底：未预期的系统异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("未预期的系统异常", e);
        return Result.fail("服务器内部错误，请稍后再试");
    }
}
