package com.dearblog.dto.response.portal;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Portal 文章列表 VO（不含正文）
 */
@Data
public class PortalArticleListVO {

    private Long id;

    private String title;

    private String slug;

    private String summary;

    private String coverImage;

    private Long categoryId;

    private String categoryName;

    private String categorySlug;

    private List<PortalTagVO> tags;

    /** 内容格式：1=Markdown  2=富文本 */
    private Integer contentType;

    private Long seriesId;

    private String seriesName;

    private Integer seriesSort;

    private Boolean isFeatured;

    /** 文章状态：1=草稿  2=已发布  3=已归档 */
    private Integer status;

    /** 预估阅读时间（分钟） */
    private Integer readingTime;

    private LocalDateTime publishTime;

    private Integer viewCount;
}
