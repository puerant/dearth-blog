package com.dearblog.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章实体
 * contentType: 1=Markdown  2=富文本
 * status:      1=草稿  2=已发布  3=已归档
 */
@Data
@TableName("blog_article")
public class BlogArticle {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String slug;
    private String summary;

    /** 正文内容，LONGTEXT，列表查询时不加载此字段 */
    private String content;

    /** 1=Markdown  2=富文本 */
    private Integer contentType;

    private String coverImage;
    private Long categoryId;
    private Long seriesId;
    private Integer seriesSort;

    /** 是否精选 */
    private Boolean isFeatured;

    /** 1=草稿  2=已发布  3=已归档 */
    private Integer status;

    private LocalDateTime publishTime;
    private Integer viewCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
