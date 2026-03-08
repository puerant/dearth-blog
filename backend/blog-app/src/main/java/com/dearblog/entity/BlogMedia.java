package com.dearblog.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("blog_media")
public class BlogMedia {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String originalName;
    private String storageName;
    private String storagePath;
    private String accessUrl;
    private String mimeType;
    private Long fileSize;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
