# dear-blog

个人博客系统，技术+生活混合内容定位。极简亮色风格，前后端分离，单博主账号体系。

## 技术栈

| 层级 | 技术 |
|------|------|
| 门户前端 | VitePress + Tailwind CSS + Vue 3 + Pinia |
| 管理后台 | Vue 3 + Vite + Element Plus + ByteMD |
| 后端 | Spring Boot 3 + MyBatis Plus + Java 17 |
| 数据库 | MySQL 8 + Redis 7 |
| 认证 | JWT + Spring Security |
| 部署 | Nginx + Docker Compose |

## 项目结构

```
dear-blog/
├── backend/          # Spring Boot 后端
│   └── blog-app/
├── frontend/
│   ├── portal/       # 门户网站（VitePress SSG）
│   └── admin/        # 管理后台（Vue CSR）
└── docs/             # 设计文档 & PRD
```

## 本地启动

**前置条件：** JDK 17、Node.js 18+、pnpm、MySQL 8、Redis 7

```bash
# 后端
cd backend/blog-app
mvn spring-boot:run

# 门户前端
cd frontend/portal
pnpm install && pnpm dev

# 管理后台
cd frontend/admin
pnpm install && pnpm dev
```

后端默认端口 `8080`，首次启动 Flyway 自动建表，默认账号 `admin / admin123`。

## 主要功能

- 文章管理（Markdown / 富文本双模式）
- 分类（多级）、标签、系列
- 时间归档
- 项目展示（首页时钟轨道动画）
- 访问统计
- 媒体文件管理
- JWT 认证 + Redis Token 黑名单

## 许可

MIT
