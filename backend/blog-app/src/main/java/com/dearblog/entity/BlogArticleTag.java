package com.dearblog.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("blog_article_tag")
public class BlogArticleTag {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long articleId;
    private Long tagId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
