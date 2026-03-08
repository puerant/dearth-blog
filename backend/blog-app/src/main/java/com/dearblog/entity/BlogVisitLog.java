package com.dearblog.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("blog_visit_log")
public class BlogVisitLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String path;
    private Long articleId;
    private String ip;
    private String userAgent;
    private String referer;
    private LocalDate visitDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
