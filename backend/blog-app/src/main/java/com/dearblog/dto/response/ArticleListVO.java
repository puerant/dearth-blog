package com.dearblog.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章列表 VO（不含正文内容）
 */
@Data
public class ArticleListVO {

    private Long id;

    private String title;

    private String slug;

    private String summary;

    private String coverImage;

    private Long categoryId;

    private String categoryName;

    private Long seriesId;

    private Boolean isFeatured;

    /** 1=草稿, 2=已发布, 3=已归档 */
    private Integer status;

    private Integer viewCount;

    private LocalDateTime publishTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<TagVO> tags;
}
