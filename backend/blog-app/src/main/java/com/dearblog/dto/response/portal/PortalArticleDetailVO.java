package com.dearblog.dto.response.portal;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Portal 文章详情 VO（含正文）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PortalArticleDetailVO extends PortalArticleListVO {

    private String content;

    /** 系列内 slug，用于导航（列表页无此字段） */
    private String seriesSlug;

    /** 系列上一篇 */
    private ArticleLinkVO prevArticle;

    /** 系列下一篇 */
    private ArticleLinkVO nextArticle;

    @Data
    public static class ArticleLinkVO {
        private Long id;
        private String title;
        private String slug;
    }
}
