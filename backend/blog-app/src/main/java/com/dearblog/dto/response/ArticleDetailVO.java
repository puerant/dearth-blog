package com.dearblog.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 文章详情 VO（含正文及标签 ID 列表）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleDetailVO extends ArticleListVO {

    private String content;

    /** 1=Markdown, 2=RichText */
    private Integer contentType;

    private Integer seriesSort;

    private List<Long> tagIds;
}
