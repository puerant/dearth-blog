package com.dearblog.dto.request;

import lombok.Data;

@Data
public class VisitRecordRequest {

    /** 访问路径 */
    private String path;

    /** 文章 ID（非文章页时为 null） */
    private Long articleId;

    /** Referer 地址 */
    private String referer;
}
