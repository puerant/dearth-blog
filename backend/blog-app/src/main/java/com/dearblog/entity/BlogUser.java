package com.dearblog.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 博主账号实体
 * 对应数据库表 blog_user（单用户系统）
 */
@Data
@TableName("blog_user")
public class BlogUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    /** BCrypt 加密密码，禁止在响应中返回此字段 */
    private String password;

    private String nickname;

    private String avatar;

    private String bio;

    private String email;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
