package com.dearblog.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目展示实体
 * techStack / highlights 字段在数据库中为 JSON 类型，实体中存为 JSON 字符串
 */
@Data
@TableName("blog_project")
public class BlogProject {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String projectNo;
    private String name;
    private String shortName;
    private String description;

    /** JSON 字符串数组，如 ["Vue.js","Spring Boot"] */
    private String techStack;

    /** JSON 字符串数组，如 ["亮点一","亮点二"] */
    private String highlights;

    private String startYear;
    private String status;
    private Integer hue;
    private String githubUrl;
    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
