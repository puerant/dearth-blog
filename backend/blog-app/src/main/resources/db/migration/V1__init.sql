-- ============================================================
--  Flyway 迁移脚本 V1：初始化所有表结构与基础数据
--  说明：Flyway 已指定数据库，此处不含 CREATE DATABASE / USE 语句
-- ============================================================

-- ============================================================
--  1. blog_user 博主账号表
-- ============================================================
CREATE TABLE `blog_user`
(
    `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`   VARCHAR(50)     NOT NULL                COMMENT '登录用户名',
    `password`   VARCHAR(255)    NOT NULL                COMMENT '密码（BCrypt 哈希）',
    `nickname`   VARCHAR(100)    NOT NULL DEFAULT ''     COMMENT '昵称（前台展示名）',
    `avatar`     VARCHAR(500)    NOT NULL DEFAULT ''     COMMENT '头像图片 URL',
    `bio`        VARCHAR(500)    NOT NULL DEFAULT ''     COMMENT '一句话简介',
    `email`      VARCHAR(200)    NOT NULL DEFAULT ''     COMMENT '联系邮箱',
    `created_at` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT = '博主账号表';

-- ============================================================
--  2. blog_site_config 站点配置表（Key-Value）
-- ============================================================
CREATE TABLE `blog_site_config`
(
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `config_key`  VARCHAR(100)    NOT NULL                COMMENT '配置键名',
    `config_val`  TEXT            NOT NULL                COMMENT '配置值',
    `description` VARCHAR(200)    NOT NULL DEFAULT ''     COMMENT '配置说明',
    `updated_at`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT = '站点配置表（Key-Value）';

-- ============================================================
--  3. blog_category 分类表（支持多级，parent_id=0 为根分类）
-- ============================================================
CREATE TABLE `blog_category`
(
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`          VARCHAR(100)    NOT NULL                COMMENT '分类名称',
    `slug`          VARCHAR(100)    NOT NULL                COMMENT '分类 Slug',
    `description`   VARCHAR(500)    NOT NULL DEFAULT ''     COMMENT '分类描述',
    `parent_id`     BIGINT UNSIGNED NOT NULL DEFAULT 0      COMMENT '父分类 ID，0 表示根分类',
    `sort_order`    INT             NOT NULL DEFAULT 0      COMMENT '排序权重',
    `article_count` INT UNSIGNED    NOT NULL DEFAULT 0      COMMENT '文章数量（冗余缓存）',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_slug` (`slug`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT = '分类表（支持多级）';

-- ============================================================
--  4. blog_tag 标签表
-- ============================================================
CREATE TABLE `blog_tag`
(
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`          VARCHAR(100)    NOT NULL                COMMENT '标签名称',
    `slug`          VARCHAR(100)    NOT NULL                COMMENT '标签 Slug',
    `description`   VARCHAR(500)    NOT NULL DEFAULT ''     COMMENT '标签描述',
    `article_count` INT UNSIGNED    NOT NULL DEFAULT 0      COMMENT '关联文章数（冗余缓存）',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_slug` (`slug`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT = '标签表';

-- ============================================================
--  5. blog_series 系列表
-- ============================================================
CREATE TABLE `blog_series`
(
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`          VARCHAR(200)    NOT NULL                COMMENT '系列名称',
    `slug`          VARCHAR(200)    NOT NULL                COMMENT '系列 Slug',
    `description`   TEXT            NOT NULL                COMMENT '系列简介',
    `cover_image`   VARCHAR(500)    NOT NULL DEFAULT ''     COMMENT '系列封面图 URL',
    `sort_order`    INT             NOT NULL DEFAULT 0      COMMENT '系列排序权重',
    `article_count` INT UNSIGNED    NOT NULL DEFAULT 0      COMMENT '系列内文章数（冗余缓存）',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_slug` (`slug`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT = '系列表';

-- ============================================================
--  6. blog_article 文章表
--  content_type: 1=Markdown  2=富文本
--  status:       1=草稿  2=已发布  3=已归档
-- ============================================================
CREATE TABLE `blog_article`
(
    `id`           BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title`        VARCHAR(500)     NOT NULL                COMMENT '文章标题',
    `slug`         VARCHAR(500)     NOT NULL                COMMENT '文章 Slug',
    `summary`      TEXT                                     COMMENT '文章摘要',
    `content`      LONGTEXT         NOT NULL                COMMENT '文章正文',
    `content_type` TINYINT UNSIGNED NOT NULL DEFAULT 1      COMMENT '内容格式：1=Markdown 2=富文本',
    `cover_image`  VARCHAR(500)     NOT NULL DEFAULT ''     COMMENT '封面图片 URL',
    `category_id`  BIGINT UNSIGNED  NOT NULL DEFAULT 0      COMMENT '所属分类 ID（0=未分类）',
    `series_id`    BIGINT UNSIGNED  NOT NULL DEFAULT 0      COMMENT '所属系列 ID（0=无系列）',
    `series_sort`  INT              NOT NULL DEFAULT 0      COMMENT '系列内排序',
    `is_featured`  TINYINT(1)       NOT NULL DEFAULT 0      COMMENT '是否精选：0=否 1=是',
    `status`       TINYINT UNSIGNED NOT NULL DEFAULT 1      COMMENT '状态：1=草稿 2=已发布 3=已归档',
    `publish_time` DATETIME                                 COMMENT '发布时间',
    `view_count`   INT UNSIGNED     NOT NULL DEFAULT 0      COMMENT '累计浏览次数',
    `created_at`   DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`   DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_slug` (`slug`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_series_id` (`series_id`),
    KEY `idx_status_publish` (`status`, `publish_time`),
    KEY `idx_is_featured` (`is_featured`),
    KEY `idx_created_at` (`created_at`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT = '文章表';

-- ============================================================
--  7. blog_article_tag 文章-标签关联表（多对多）
-- ============================================================
CREATE TABLE `blog_article_tag`
(
    `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `article_id` BIGINT UNSIGNED NOT NULL                COMMENT '文章 ID',
    `tag_id`     BIGINT UNSIGNED NOT NULL                COMMENT '标签 ID',
    `created_at` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`),
    KEY `idx_tag_id` (`tag_id`),
    KEY `idx_article_id` (`article_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT = '文章-标签关联表';

-- ============================================================
--  8. blog_media 媒体文件表
-- ============================================================
CREATE TABLE `blog_media`
(
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `original_name` VARCHAR(255)    NOT NULL                COMMENT '原始文件名',
    `storage_name`  VARCHAR(255)    NOT NULL                COMMENT '存储文件名',
    `storage_path`  VARCHAR(500)    NOT NULL                COMMENT '服务器存储路径',
    `access_url`    VARCHAR(500)    NOT NULL                COMMENT '对外访问 URL',
    `mime_type`     VARCHAR(100)    NOT NULL DEFAULT ''     COMMENT 'MIME 类型',
    `file_size`     BIGINT UNSIGNED NOT NULL DEFAULT 0      COMMENT '文件大小（字节）',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    PRIMARY KEY (`id`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_mime_type` (`mime_type`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT = '媒体文件表';

-- ============================================================
--  9. blog_visit_log 访问明细日志表
-- ============================================================
CREATE TABLE `blog_visit_log`
(
    `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `path`       VARCHAR(500)    NOT NULL                COMMENT '访问路径',
    `article_id` BIGINT UNSIGNED NOT NULL DEFAULT 0      COMMENT '文章 ID，非文章页为 0',
    `ip`         VARCHAR(64)     NOT NULL DEFAULT ''     COMMENT '访客 IP',
    `user_agent` VARCHAR(500)    NOT NULL DEFAULT ''     COMMENT 'User-Agent',
    `referer`    VARCHAR(500)    NOT NULL DEFAULT ''     COMMENT '来源 URL',
    `visit_date` DATE            NOT NULL                COMMENT '访问日期',
    `created_at` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
    PRIMARY KEY (`id`),
    KEY `idx_article_id` (`article_id`),
    KEY `idx_visit_date` (`visit_date`),
    KEY `idx_ip_date` (`ip`(32), `visit_date`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT = '访问明细日志表';

-- ============================================================
--  10. blog_visit_stat 访问统计聚合表（按天）
-- ============================================================
CREATE TABLE `blog_visit_stat`
(
    `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `stat_date`  DATE            NOT NULL                COMMENT '统计日期',
    `pv`         INT UNSIGNED    NOT NULL DEFAULT 0      COMMENT '当日 PV',
    `uv`         INT UNSIGNED    NOT NULL DEFAULT 0      COMMENT '当日 UV',
    `article_pv` INT UNSIGNED    NOT NULL DEFAULT 0      COMMENT '当日文章页 PV',
    `created_at` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_stat_date` (`stat_date`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT = '访问统计聚合表（按天）';

-- ============================================================
--  11. blog_project 项目展示表
-- ============================================================
CREATE TABLE `blog_project`
(
    `id`          BIGINT UNSIGNED   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_no`  VARCHAR(20)       NOT NULL                COMMENT '项目编号（如 P01）',
    `name`        VARCHAR(200)      NOT NULL                COMMENT '项目名称',
    `short_name`  VARCHAR(20)       NOT NULL DEFAULT ''     COMMENT '项目简称',
    `description` TEXT              NOT NULL                COMMENT '项目描述',
    `tech_stack`  JSON              NOT NULL                COMMENT '技术栈（JSON 数组）',
    `highlights`  JSON              NOT NULL                COMMENT '亮点列表（JSON 数组）',
    `start_year`  CHAR(4)           NOT NULL DEFAULT ''     COMMENT '项目起始年份',
    `status`      VARCHAR(20)       NOT NULL DEFAULT 'Active' COMMENT '状态：Active / Archive / WIP',
    `hue`         SMALLINT UNSIGNED NOT NULL DEFAULT 200    COMMENT '主题色调 Hue（0-360）',
    `github_url`  VARCHAR(500)      NOT NULL DEFAULT ''     COMMENT 'GitHub 仓库链接',
    `sort_order`  INT               NOT NULL DEFAULT 0      COMMENT '展示排序权重',
    `created_at`  DATETIME          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME          NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_project_no` (`project_no`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT = '项目展示表';

-- ============================================================
--  初始数据
-- ============================================================

-- 默认博主账号（密码明文: admin123，上线后请立即修改）
INSERT INTO `blog_user` (`username`, `password`, `nickname`, `bio`, `email`)
VALUES ('admin',
        '$2a$10$GiRyilrm2tNELn0m8UQCkOLDbsg/UFf2/FjX.0W5Tj5swMHX87dcS',
        '博主',
        '记录技术与生活的交汇处',
        '');

-- 默认站点配置
INSERT INTO `blog_site_config` (`config_key`, `config_val`, `description`)
VALUES ('site_name', 'dear·blog', '站点名称'),
       ('site_tagline', '记录技术与生活的交汇处', '站点一句话简介'),
       ('site_since', '2024', '建站年份'),
       ('site_location', '', '博主所在城市'),
       ('github_url', '', 'GitHub 主页链接'),
       ('email', '', '联系邮箱'),
       ('about_content', '', '关于我页面内容（Markdown 格式）'),
       ('skills', '[]', '技能标签列表（JSON 字符串数组）');
