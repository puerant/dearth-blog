package com.dearblog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ArticleSaveRequest {

    @NotBlank(message = "文章标题不能为空")
    private String title;

    /** 留空则由服务端根据标题自动生成 */
    private String slug;

    private String summary = "";

    @NotBlank(message = "文章内容不能为空")
    private String content;

    /** 1=Markdown, 2=RichText */
    private Integer contentType = 1;

    private String coverImage;

    private Long categoryId;

    private List<Long> tagIds = new ArrayList<>();

    private Long seriesId;

    private Integer seriesSort = 0;

    private Boolean isFeatured = false;

    /** 1=草稿, 2=已发布, 3=已归档 */
    private Integer status = 1;

    /** 定时发布时间，status=2 且此字段有值时按此时间发布 */
    private LocalDateTime publishTime;
}
