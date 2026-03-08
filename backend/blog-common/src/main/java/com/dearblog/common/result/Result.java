package com.dearblog.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

/**
 * 统一 REST 响应体
 * <pre>
 * 成功：Result.ok() / Result.ok(data)
 * 失败：Result.fail(ResultCode) / Result.fail(ResultCode, message)
 * </pre>
 *
 * @param <T> 业务数据类型
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {

    private final int code;
    private final String message;
    private final T data;

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ── 成功 ──────────────────────────────────────────────

    public static <T> Result<T> ok() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    // ── 失败 ──────────────────────────────────────────────

    public static <T> Result<T> fail(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    public static <T> Result<T> fail(ResultCode resultCode, String message) {
        return new Result<>(resultCode.getCode(), message, null);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(ResultCode.INTERNAL_ERROR.getCode(), message, null);
    }

    // ── 工具方法 ───────────────────────────────────────────

    public boolean isSuccess() {
        return this.code == ResultCode.SUCCESS.getCode();
    }
}
