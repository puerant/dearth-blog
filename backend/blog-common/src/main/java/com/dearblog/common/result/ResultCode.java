package com.dearblog.common.result;

import lombok.Getter;

/**
 * 统一业务状态码
 * 1xx-5xx 段复用 HTTP 语义；1000+ 段为业务自定义码
 */
@Getter
public enum ResultCode {

    // ── 通用 ──────────────────────────────────────────────
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // ── 认证相关 ───────────────────────────────────────────
    LOGIN_FAILED(1001, "用户名或密码错误"),
    TOKEN_INVALID(1002, "Token 无效或已过期"),
    TOKEN_BLACKLISTED(1003, "Token 已失效，请重新登录"),
    PASSWORD_WRONG(1004, "原密码错误"),

    // ── 内容相关 ───────────────────────────────────────────
    SLUG_EXISTS(2001, "Slug 已被占用，请换一个"),
    CATEGORY_HAS_CHILDREN(2002, "请先删除或迁移该分类的子分类"),
    CATEGORY_HAS_ARTICLES(2003, "请先将该分类下的文章移至其他分类"),
    ARTICLE_STATUS_INVALID(2004, "文章状态流转不合法"),

    // ── 文件相关 ───────────────────────────────────────────
    FILE_UPLOAD_FAILED(3001, "文件上传失败"),
    FILE_TYPE_NOT_ALLOWED(3002, "不支持的文件类型"),
    FILE_SIZE_EXCEEDED(3003, "文件大小超出限制"),
    FILE_NOT_FOUND(3004, "文件不存在");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
