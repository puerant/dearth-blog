-- ============================================================
--  Flyway 迁移脚本 V2：演示数据
--  包含：站点配置、分类、标签、系列、文章、项目、访问统计
-- ============================================================

-- ============================================================
--  站点配置（更新为完整内容）
-- ============================================================
UPDATE `blog_site_config` SET `config_val` = 'TH'           WHERE `config_key` = 'site_name';
UPDATE `blog_site_config` SET `config_val` = 'DEAR'    WHERE `config_key` = 'site_tagline';
UPDATE `blog_site_config` SET `config_val` = '2024'                 WHERE `config_key` = 'site_since';
UPDATE `blog_site_config` SET `config_val` = 'SHANGHAI'                 WHERE `config_key` = 'site_location';
UPDATE `blog_site_config` SET `config_val` = 'https://github.com/dearblog' WHERE `config_key` = 'github_url';
UPDATE `blog_site_config` SET `config_val` = 'hi@dearblog.dev'      WHERE `config_key` = 'email';
UPDATE `blog_site_config` SET `config_val` = '## 关于我

一个喜欢写代码和写字的开发者。白天敲 Java，晚上折腾 Vue，周末看书喝咖啡。

这里记录技术探索的过程、生活里的碎片感悟，以及那些值得分享的工具和方法。

不追热点，只写真正用过、想过、觉得有意思的东西。' WHERE `config_key` = 'about_content';
UPDATE `blog_site_config` SET `config_val` = '["Java","Vue 3","Spring Boot","TypeScript","MySQL","Docker","Linux","设计模式"]' WHERE `config_key` = 'skills';

-- ============================================================
--  分类（3 个根分类 + 5 个子分类）
-- ============================================================
INSERT INTO `blog_category` (`id`, `name`, `slug`, `description`, `parent_id`, `sort_order`, `article_count`)
VALUES
    (1, '技术',     'tech',        '编程、架构与工程实践',   0, 1, 8),
    (2, '生活',     'life',        '阅读、随笔与日常记录',   0, 2, 3),
    (3, '开源',     'open-source', '开源项目与工具分享',     0, 3, 1),
    (4, '前端开发', 'frontend',    'Vue、TypeScript、CSS 等前端技术', 1, 1, 4),
    (5, '后端开发', 'backend',     'Java、Spring Boot、数据库等后端技术', 1, 2, 4),
    (6, '工具效率', 'tools',       '开发工具、效率方法、工作流',        1, 3, 0),
    (7, '读书',     'reading',     '读书笔记与书单推荐',     2, 1, 2),
    (8, '随笔',     'essay',       '生活感悟与碎碎念',       2, 2, 1);

-- ============================================================
--  标签
-- ============================================================
INSERT INTO `blog_tag` (`id`, `name`, `slug`, `article_count`)
VALUES
    (1,  'Vue 3',       'vue3',          4),
    (2,  'TypeScript',  'typescript',    3),
    (3,  'Vite',        'vite',          2),
    (4,  'Java',        'java',          3),
    (5,  'Spring Boot', 'spring-boot',   3),
    (6,  'MyBatis',     'mybatis',       2),
    (7,  'MySQL',       'mysql',         2),
    (8,  'Docker',      'docker',        1),
    (9,  '设计模式',    'design-pattern',2),
    (10, '效率工具',    'productivity',  1),
    (11, '读书笔记',    'reading-notes', 2),
    (12, 'Git',         'git',           1);

-- ============================================================
--  系列
-- ============================================================
INSERT INTO `blog_series` (`id`, `name`, `slug`, `description`, `sort_order`, `article_count`)
VALUES
    (1, 'Vue 3 实战指南',
        'vue3-guide',
        '从零开始系统学习 Vue 3，涵盖 Composition API、状态管理、路由、工程化等核心主题，结合实际项目案例讲解。',
        1, 3),
    (2, 'Spring Boot 从入门到部署',
        'springboot-guide',
        '面向 Java 后端开发者的 Spring Boot 实战系列，覆盖项目搭建、数据库集成、安全认证、容器化部署全流程。',
        2, 3);

-- ============================================================
--  文章（12 篇：5 精选 + 3 Vue 系列 + 3 Spring Boot 系列 + 1 随笔）
-- ============================================================
INSERT INTO `blog_article`
    (`id`, `title`, `slug`, `summary`, `content`, `content_type`,
     `category_id`, `series_id`, `series_sort`, `is_featured`, `status`,
     `publish_time`, `view_count`)
VALUES

-- ── 精选：前端 ────────────────────────────────────────────────

(1,
 '用 VitePress 搭建自己的博客系统',
 'build-blog-with-vitepress',
 'VitePress 1.x 正式版发布后，凭借极快的构建速度和对 Vue 3 的原生支持，成为静态博客的优质选择。本文记录完整搭建过程。',
 '## 为什么选 VitePress

市面上静态站生成器不少，Hexo 老牌稳定，Hugo 构建快，Jekyll 生态成熟。但我最终选择 VitePress，原因很简单：

- 基于 Vite，冷启动 < 1s，HMR 即时
- 天然支持 Vue 3 组件嵌入 Markdown
- 主题系统干净，自定义成本低
- TypeScript 友好

## 核心概念

VitePress 的核心是「以文件为路由」。`docs/index.md` 对应根路径，`docs/guide/intro.md` 对应 `/guide/intro`。

### 自定义主题

在 `.vitepress/theme/index.ts` 中导出主题对象：

```typescript
import type { Theme } from ''vitepress''
import DefaultTheme from ''vitepress/theme''
import MyLayout from ''./Layout.vue''

export default {
  extends: DefaultTheme,
  Layout: MyLayout,
} satisfies Theme
```

### 运行时数据获取

VitePress 支持在 Vue 组件中调用 API，实现真正的前后端分离：

```vue
<script setup lang="ts">
import { onMounted, ref } from ''vue''

const posts = ref([])
onMounted(async () => {
  const res = await fetch(''/api/portal/articles'')
  posts.value = (await res.json()).data.records
})
</script>
```

## 构建与部署

```bash
# 安装依赖
pnpm install

# 本地开发
pnpm dev

# 生产构建
pnpm build
```

构建产物在 `.vitepress/dist`，直接用 Nginx 托管即可。

## 总结

整套方案下来，开发体验比预期好得多。VitePress 把复杂度藏得很深，留给你的接口干净而直觉。',
 1, 4, 0, 0, 1, 1, '2025-03-01 10:00:00', 312),

(2,
 'Vue 3 Composition API 设计哲学',
 'vue3-composition-api-design',
 'Composition API 不只是一套新 API，它背后有一套完整的设计哲学：以逻辑为单元组织代码，而不是以选项为单元。',
 '## Options API 的局限

用过 Vue 2 的人都熟悉 Options API 的写法：`data`、`methods`、`computed`、`watch` 各司其职。这种方式初学友好，但当组件逻辑复杂后，同一个功能的代码会被分散在各个选项里。

想象一个「搜索+分页+排序」组件，它的状态和方法会散落在 `data`、`computed`、`methods`、`watch` 四处，阅读时需要反复跳转。

## Composition API 的解法

Composition API 以「逻辑关注点」为单元组织代码：

```typescript
// useSearch.ts
export function useSearch() {
  const keyword = ref(''''  )
  const results = ref([])
  const loading = ref(false)

  async function search() {
    loading.value = true
    results.value = await fetchResults(keyword.value)
    loading.value = false
  }

  return { keyword, results, loading, search }
}
```

相关代码放在一起，可以抽成独立文件复用，也可以独立测试。

## `setup()` 的执行时机

`setup()` 在组件实例创建之前执行，因此内部无法访问 `this`。这是设计上有意为之——让逻辑与组件实例解耦。

## 响应式的本质

Vue 3 的响应式基于 ES Proxy：

```typescript
const state = reactive({ count: 0 })
// 访问 state.count → 触发 get trap → 收集依赖
// 修改 state.count → 触发 set trap → 通知更新
```

`ref` 是对 `reactive` 的封装，通过 `.value` 统一访问，方便类型推断。

## 什么时候用 `ref`，什么时候用 `reactive`

- 基础类型（string、number、boolean）：用 `ref`
- 对象/数组：两者都行，但 `reactive` 解构会丢失响应性，需要用 `toRefs`

```typescript
const state = reactive({ x: 0, y: 0 })
const { x, y } = toRefs(state) // 保持响应性
```

## 小结

Composition API 的核心价值是**逻辑复用**和**代码组织**。它不是 Options API 的替代，而是对其的补充和升级。',
 1, 4, 1, 1, 1, 2, '2025-01-15 09:00:00', 891),

-- ── 精选：后端 ────────────────────────────────────────────────

(3,
 'Spring Security + JWT 实现无状态认证',
 'spring-security-jwt-auth',
 '基于 Spring Boot 3 和 Spring Security 6，实现 JWT 无状态认证，包含 Token 签发、校验、刷新和黑名单注销全流程。',
 '## 为什么选 JWT

传统 Session 认证需要服务端存储会话状态，横向扩展时需要共享 Session（Redis 集群）。JWT 把状态放在 Token 本身，服务端无需存储，天然支持无状态水平扩展。

当然 JWT 也有代价：Token 不能主动失效（除非引入黑名单）。

## 核心依赖

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
```

## JwtUtil 实现

```java
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getKey())
                .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}
```

## JWT Filter

在 Spring Security 过滤链中插入自定义过滤器，解析每个请求的 Token：

```java
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        String token = header.substring(7);
        String username = jwtUtil.extractUsername(token);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails user = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        chain.doFilter(request, response);
    }
}
```

## Token 黑名单（Redis）

注销时将 Token 存入 Redis，有效期与 Token 过期时间对齐：

```java
public void logout(String token) {
    long remaining = jwtUtil.getRemainingMillis(token);
    redisTemplate.opsForValue().set("blacklist:" + token, "1",
        remaining, TimeUnit.MILLISECONDS);
}
```

Filter 中增加黑名单检查：

```java
if (redisTemplate.hasKey("blacklist:" + token)) {
    chain.doFilter(request, response);
    return;
}
```

## SecurityConfig 配置

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**", "/api/portal/**").permitAll()
            .anyRequest().authenticated())
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
}
```

## 小结

整套方案实现了真正的无状态认证。Redis 黑名单是唯一有状态的部分，但它只在注销时写入，查询压力极低。',
 1, 5, 0, 0, 1, 1, '2025-02-10 14:00:00', 743),

(4,
 'MyBatis Plus 使用笔记：从基础到自定义',
 'mybatis-plus-notes',
 'MyBatis Plus 是 MyBatis 的增强工具，在不改变原有用法的基础上，提供了 CRUD 封装、条件构造器、代码生成器等实用功能。',
 '## 为什么是 MyBatis Plus

MyBatis 的优势是 SQL 可控，但写大量重复的简单 CRUD 很烦。MyBatis Plus 解决了这个问题：

- 内置通用 Mapper（`BaseMapper<T>`），基础操作零 SQL
- 条件构造器（`LambdaQueryWrapper`），类型安全的动态查询
- 分页插件，一行代码实现分页

## 基本使用

继承 `BaseMapper<T>` 即可获得全套 CRUD：

```java
@Mapper
public interface ArticleMapper extends BaseMapper<BlogArticle> {
    // selectById、selectList、insert、updateById、deleteById 全部内置
}
```

## LambdaQueryWrapper

避免字符串硬编码字段名，重构安全：

```java
LambdaQueryWrapper<BlogArticle> wrapper = new LambdaQueryWrapper<BlogArticle>()
    .eq(BlogArticle::getStatus, 2)
    .like(StringUtils::hasText(keyword), BlogArticle::getTitle, keyword)
    .orderByDesc(BlogArticle::getPublishTime);

List<BlogArticle> articles = articleMapper.selectList(wrapper);
```

## 分页查询

配置 `MybatisPlusInterceptor`，然后：

```java
Page<BlogArticle> page = articleMapper.selectPage(
    new Page<>(pageNum, pageSize), wrapper);

page.getRecords(); // 当前页数据
page.getTotal();   // 总记录数
page.getPages();   // 总页数
```

## 字段自动填充

通过 `MetaObjectHandler` 自动填充 `created_at`、`updated_at`：

```java
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }
}
```

## 自定义 SQL

简单查询用构造器，复杂关联用 XML：

```xml
<select id="selectArticleWithTags" resultMap="ArticleWithTagsMap">
    SELECT a.*, t.id AS tag_id, t.name AS tag_name
    FROM blog_article a
    LEFT JOIN blog_article_tag at ON a.id = at.article_id
    LEFT JOIN blog_tag t ON at.tag_id = t.id
    WHERE a.slug = #{slug}
</select>
```

## 小结

MyBatis Plus 是一个克制的增强框架，它扩展了 MyBatis 但没有掩盖它。在项目中混用内置方法和自定义 SQL 完全没有问题。',
 1, 5, 0, 0, 1, 1, '2025-01-28 11:00:00', 518),

-- ── 精选：随笔 ────────────────────────────────────────────────

(5,
 '关于「把事情做完」这件事',
 'about-getting-things-done',
 '完美主义是拖延症最体面的借口。很多时候，把事情做完比把事情做好更重要。',
 '## 一个困扰我很久的问题

我的硬盘里躺着很多半成品：写了一半的文章、搭了框架没有内容的项目、规划好却没执行的计划。

它们有一个共同特点：我当时觉得还不够好，所以没发出去。

## 完美主义的陷阱

追求完美本没有错，但「等到完美再发」是一个永远等不到的条件。

软件工程里有个说法：**Done is better than perfect**。不是鼓励粗制滥造，而是说：一个能用的东西比一个不存在的完美方案更有价值。

用户可以给你反馈，时间会告诉你哪些细节真正重要，而这些都需要你先把东西发出去。

## 我的转变

今年我做了一个决定：写完就发，项目做到可用就上线。

结果出乎意料的好。几篇「觉得还不够好」的文章收到了不少反馈；一个「功能还不完整」的小工具有人在用。

而那些「等完美了再发」的内容，现在依然躺在草稿箱里。

## 怎么对抗完美主义

1. **设定「足够好」的标准**，而不是「完美」的标准。文章能把核心观点说清楚就发，不必字斟句酌每一句。

2. **缩小第一版的范围**。不要试图一次做完所有功能，先做一个可用的最小集合。

3. **给自己一个 deadline**，到期发出去，不管完不完美。

4. **接受迭代**。没有什么是不能修改的，先发出去，后续再完善。

## 写在最后

这篇文章也是这样写完发出去的。它可能不够完美，但它存在了。

这就够了。',
 1, 8, 0, 0, 1, 1, '2025-02-20 20:00:00', 284),

-- ── Vue 3 系列（3 篇）────────────────────────────────────────

(6,
 'Vue 3 系列（一）：从 CDN 到工程化项目',
 'vue3-guide-01-setup',
 '系列第一篇，从最简单的 CDN 引入开始，逐步过渡到 Vite + TypeScript 的现代工程化项目结构。',
 '## 系列简介

这个系列面向有一定前端基础、想系统学习 Vue 3 的开发者。不会逐一讲解每个 API，而是围绕实际项目建立完整的知识体系。

## 第一步：CDN 引入，感受语法

最快的方式是直接用 CDN，不需要任何构建工具：

```html
<script type="module">
import { createApp, ref } from ''https://unpkg.com/vue@3/dist/vue.esm-browser.js''

createApp({
  setup() {
    const count = ref(0)
    return { count }
  },
  template: `<button @click="count++">{{ count }}</button>`
}).mount(''#app'')
</script>
```

这样就能感受 Vue 3 的基本语法，不需要任何配置。

## 第二步：Vite 项目

```bash
pnpm create vite my-app -- --template vue-ts
cd my-app
pnpm install
pnpm dev
```

Vite 使用 ES modules，冷启动极快，是目前 Vue 3 项目的首选工具链。

## 项目结构约定

```
src/
├── components/     # 通用组件
├── views/          # 页面级组件
├── composables/    # 可复用的 Composition 函数
├── stores/         # Pinia 状态管理
├── api/            # 接口请求
├── types/          # TypeScript 类型定义
└── router/         # 路由配置
```

## TypeScript 配置要点

`tsconfig.app.json` 中开启严格模式：

```json
{
  "compilerOptions": {
    "strict": true,
    "moduleResolution": "bundler"
  }
}
```

Vue 文件中组件 props 用 `defineProps<T>()` 泛型写法，获得完整的类型推断。

## 下一篇

下一篇会深入 Composition API，讲解 `ref`、`reactive`、`computed`、`watch` 的使用场景与区别。',
 1, 4, 1, 1, 0, 0, '2024-11-10 10:00:00', 445),

(7,
 'Vue 3 系列（二）：响应式系统深度解析',
 'vue3-guide-02-reactivity',
 'Vue 3 响应式系统基于 ES Proxy 重写，本篇深入剖析 ref、reactive、computed、watch 的实现原理与使用边界。',
 '## 响应式的本质

Vue 3 响应式的核心是 `effect`（副作用）+ `track`（依赖收集）+ `trigger`（依赖触发）：

```
读取响应式数据 → track → 记录当前 effect 依赖该数据
写入响应式数据 → trigger → 重新执行所有依赖该数据的 effect
```

这个机制让视图更新、`computed` 缓存失效、`watch` 回调触发，都统一在同一套系统下运行。

## ref vs reactive

### ref

```typescript
const count = ref(0)       // Ref<number>
count.value++              // 通过 .value 访问
const doubled = computed(() => count.value * 2)
```

- 适合基础类型
- 在模板中自动解包（不需要 `.value`）
- 函数返回、props 传递时不会丢失响应性

### reactive

```typescript
const state = reactive({ x: 0, y: 0 })
state.x++                  // 直接访问，无需 .value
```

- 适合对象类型
- **解构会丢失响应性**，需要配合 `toRefs`

```typescript
const { x, y } = toRefs(state) // x、y 仍然是响应式的
```

## computed

`computed` 是带缓存的 `effect`，只有依赖变化时才重新计算：

```typescript
const total = computed(() => items.value.reduce((s, i) => s + i.price, 0))
// 多次访问 total.value，不会重复执行回调
```

## watch vs watchEffect

```typescript
// watchEffect：立即执行，自动追踪依赖
watchEffect(() => {
  console.log(count.value) // 访问 count → 自动追踪
})

// watch：显式指定依赖，有旧值
watch(count, (newVal, oldVal) => {
  console.log(newVal, oldVal)
})
```

`watchEffect` 适合副作用，`watch` 适合需要旧值或精确控制触发时机的场景。

## 常见陷阱

1. `reactive` 解构 → 用 `toRefs`
2. 将 `reactive` 对象整体替换 → 用 `ref` 包裹
3. `watch` 监听 `reactive` 对象属性 → 用 getter 形式 `() => state.x`

## 下一篇

下一篇讲 Vue Router 4 的组合式 API 用法和动态路由。',
 1, 4, 1, 2, 0, 0, '2024-12-01 10:00:00', 628),

(8,
 'Vue 3 系列（三）：Pinia 状态管理实战',
 'vue3-guide-03-pinia',
 'Pinia 是 Vue 3 官方推荐的状态管理库，比 Vuex 更轻量、更符合 Composition API 的思维方式。',
 '## 为什么 Pinia 取代了 Vuex

Vuex 4 对 Vue 3 支持不够友好：没有良好的 TypeScript 推断，`mutations` 概念冗余（改状态为什么要分 action 和 mutation？）。

Pinia 解决了这些问题：

- 完整的 TypeScript 支持
- 无 `mutations`，action 直接修改状态
- 支持 Composition API 风格写法
- DevTools 集成

## 定义 Store

```typescript
// stores/counter.ts
import { defineStore } from ''pinia''
import { ref, computed } from ''vue''

export const useCounterStore = defineStore(''counter'', () => {
  const count = ref(0)
  const doubled = computed(() => count.value * 2)

  function increment() {
    count.value++
  }

  return { count, doubled, increment }
})
```

这就是 Composition API 风格的 Store，和写普通 composable 几乎一样。

## 在组件中使用

```vue
<script setup lang="ts">
import { useCounterStore } from ''@/stores/counter''

const counter = useCounterStore()
</script>

<template>
  <p>{{ counter.count }}</p>
  <button @click="counter.increment()">+1</button>
</template>
```

## 异步 Action

```typescript
export const useArticleStore = defineStore(''article'', () => {
  const articles = ref<Article[]>([])
  const loading = ref(false)

  async function fetchArticles() {
    loading.value = true
    try {
      articles.value = await api.getArticles()
    } finally {
      loading.value = false
    }
  }

  return { articles, loading, fetchArticles }
})
```

## 持久化

配合 `pinia-plugin-persistedstate` 可以自动将状态持久化到 `localStorage`：

```typescript
const useAuthStore = defineStore(''auth'', () => {
  const token = ref(''''  )
  return { token }
}, { persist: true }) // token 自动持久化
```

## Store 间通信

Store 之间可以直接相互引用，无需特殊处理：

```typescript
export const useUserStore = defineStore(''user'', () => {
  const authStore = useAuthStore()
  // 直接使用 authStore.token
})
```

## 系列总结

三篇写完，覆盖了 Vue 3 的核心：工程化搭建、响应式原理、状态管理。后续会继续写 Vue Router、组件封装、性能优化等主题。',
 1, 4, 1, 3, 0, 0, '2024-12-20 10:00:00', 512),

-- ── Spring Boot 系列（3 篇）──────────────────────────────────

(9,
 'Spring Boot 系列（一）：从零搭建 RESTful API',
 'springboot-guide-01-rest',
 '手把手从 Spring Initializr 创建项目开始，到实现完整的 RESTful API，包含统一响应格式、全局异常处理和接口文档。',
 '## 创建项目

访问 [start.spring.io](https://start.spring.io)，选择：

- Project: Maven
- Language: Java
- Spring Boot: 3.x
- Java: 17+
- Dependencies: Spring Web, Lombok

## 项目结构

```
src/main/java/com/example/
├── controller/    # REST 控制器
├── service/       # 业务逻辑
├── mapper/        # 数据库操作（MyBatis）
├── entity/        # 数据库实体
├── dto/           # 数据传输对象
│   ├── request/   # 请求 DTO
│   └── response/  # 响应 VO
├── common/        # 通用组件
│   ├── result/    # 统一响应封装
│   └── exception/ # 自定义异常
```

## 统一响应格式

所有接口返回统一的 `Result<T>`：

```java
@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.message = "success";
        r.data = data;
        return r;
    }

    public static Result<Void> ok() {
        return ok(null);
    }

    public static <T> Result<T> fail(int code, String msg) {
        Result<T> r = new Result<>();
        r.code = code;
        r.message = msg;
        return r;
    }
}
```

## 全局异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleUnknown(Exception e) {
        log.error("未知异常", e);
        return Result.fail(500, "系统繁忙");
    }
}
```

## Controller 示例

```java
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping
    public Result<IPage<ArticleListVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(articleService.listArticles(page, size));
    }

    @GetMapping("/{id}")
    public Result<ArticleDetailVO> detail(@PathVariable Long id) {
        return Result.ok(articleService.getById(id));
    }
}
```

## 接口文档（SpringDoc OpenAPI）

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.x.x</version>
</dependency>
```

启动后访问 `http://localhost:8080/swagger-ui.html` 查看文档。

## 下一篇

下一篇讲 MyBatis Plus 集成与分页查询。',
 1, 5, 2, 1, 0, 0, '2024-10-15 10:00:00', 687),

(10,
 'Spring Boot 系列（二）：MyBatis Plus 集成与分页',
 'springboot-guide-02-mybatis',
 '在 Spring Boot 项目中集成 MyBatis Plus，配置多数据源、分页插件，实现类型安全的动态查询。',
 '## 依赖配置

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.7</version>
</dependency>
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

## application.yml 配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/my_db?useUnicode=true&characterEncoding=utf8
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis-plus:
  mapper-locations: classpath*:mapper/**/*.xml
  type-aliases-package: com.example.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

## 实体类

```java
@Data
@TableName("blog_article")
public class BlogArticle {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String slug;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
```

## 分页插件配置

```java
@Configuration
public class MyBatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

## Service 层查询

```java
public IPage<ArticleListVO> listArticles(int page, int size, String keyword) {
    LambdaQueryWrapper<BlogArticle> wrapper = new LambdaQueryWrapper<BlogArticle>()
        .eq(BlogArticle::getStatus, 2)
        .like(StringUtils.hasText(keyword), BlogArticle::getTitle, keyword)
        .orderByDesc(BlogArticle::getPublishTime);

    return articleMapper.selectPage(new Page<>(page, size), wrapper)
        .convert(this::toVO);
}
```

## 数据库迁移（Flyway）

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>
```

在 `src/main/resources/db/migration/` 下创建 SQL 文件，命名规则：`V{版本}__{描述}.sql`，如 `V1__init.sql`。

Flyway 在启动时自动按版本顺序执行未运行的迁移脚本，保证多环境数据库结构一致。

## 下一篇

下一篇讲 Spring Security + JWT 实现接口认证。',
 1, 5, 2, 2, 0, 0, '2024-11-05 10:00:00', 534),

(11,
 'Spring Boot 系列（三）：Docker 容器化部署',
 'springboot-guide-03-docker',
 '将 Spring Boot 应用打包为 Docker 镜像，配合 docker-compose 编排 MySQL、Redis、Nginx，实现一键部署。',
 '## Dockerfile

```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

多阶段构建可以减小镜像体积：

```dockerfile
# 构建阶段
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## docker-compose.yml

```yaml
version: ''3.8''
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: my_db
      MYSQL_ROOT_PASSWORD: ${DB_PASSWORD}
    volumes:
      - mysql_data:/var/lib/mysql
    restart: unless-stopped

  redis:
    image: redis:7-alpine
    restart: unless-stopped

  app:
    build: ./backend
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/my_db
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      SPRING_REDIS_HOST: redis
    depends_on:
      - mysql
      - redis
    restart: unless-stopped

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./frontend/dist:/usr/share/nginx/html
    depends_on:
      - app
    restart: unless-stopped

volumes:
  mysql_data:
```

## Nginx 反向代理配置

```nginx
server {
    listen 80;
    server_name yourdomain.com;

    # 前端静态文件
    location / {
        root /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;
    }

    # 后端 API 代理
    location /api/ {
        proxy_pass http://app:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

## 部署流程

```bash
# 1. 在服务器上克隆仓库
git clone https://github.com/xxx/project.git

# 2. 配置环境变量
cp .env.example .env
vim .env  # 填入 DB_PASSWORD 等敏感配置

# 3. 一键启动
docker-compose up -d

# 4. 查看日志
docker-compose logs -f app
```

## 系列总结

三篇内容构成了 Spring Boot 项目从开发到部署的完整闭环。实际项目中还需要考虑 CI/CD 流水线、健康检查、日志收集等，后续会专门写。',
 1, 5, 2, 3, 0, 0, '2024-12-10 10:00:00', 421),

-- ── 生活/读书 ─────────────────────────────────────────────────

(12,
 '读《深度工作》：专注是这个时代的稀缺能力',
 'deep-work-notes',
 '卡尔·纽波特在书里提出一个令人不舒服的真相：我们每天忙碌，但真正有价值的深度思考越来越少。',
 '## 什么是深度工作

卡尔·纽波特把工作分为两类：

**深度工作**：在无干扰状态下进行的、推动认知能力极限的专业活动，能创造新价值、提升技能、难以复制。

**浮浅工作**：非高认知要求的后勤型任务，往往在干扰下完成，很容易复制。

他的核心论点：深度工作能力正在变得稀缺，同时也越来越有价值。

## 让我印象深刻的几个观点

### 1. 注意力残留

当你从任务 A 切换到任务 B 后，你对 A 的注意力不会立刻消失，而是「残留」在那里，干扰你处理 B。

这解释了为什么多任务切换的效率如此低下。每次切换都有成本。

### 2. 社交媒体是专注的天敌

书里有个思想实验：如果一项工具的益处大于弊处，大多数人会使用它。但我们对社交媒体的使用逻辑不是这样的——我们使用它，仅仅因为它带来了某种好处，而完全不考虑它的弊处。

### 3. 拥抱无聊

大多数人在无聊时立刻拿出手机，这会训练大脑在任何无聊时刻都寻求刺激，让它越来越难以专注。

纽波特的建议：偶尔刻意保持无聊。在排队、等人、坐地铁时，什么都不做，让大脑休息和漫游。

## 书里的实践方法

- **深度工作时间表**：把深度工作时间块化，固定下来，像会议一样对待
- **结束仪式**：每天工作结束有一个明确的仪式，让大脑真正关机
- **网络隔离**：浏览器、社交媒体，在深度工作时段完全关闭

## 我的实践

看完书之后我做了几件事：

1. 上午 9-12 点设为深度工作时段，关掉一切通知
2. 手机从桌面移到抽屉，减少触手可及的干扰
3. 番茄工作法配合使用，25 分钟专注块

效果很明显。同样的时间，产出提高了很多，而且下班后不再有那种「忙了一天什么都没做完」的空洞感。

## 推荐给谁

- 觉得自己总是很忙但产出不高的人
- 想提升专业技能但找不到时间的人
- 刷了很多资讯却感觉越来越焦虑的人

值得一读。',
 1, 7, 0, 0, 1, 1, '2025-03-01 21:00:00', 198);

-- ── 标签关联 ─────────────────────────────────────────────────

INSERT INTO `blog_article_tag` (`article_id`, `tag_id`)
VALUES
    -- 文章1：VitePress 博客
    (1, 1), (1, 3), (1, 2),
    -- 文章2：Vue 3 Composition API
    (2, 1), (2, 2),
    -- 文章3：Spring Security JWT
    (3, 4), (3, 5),
    -- 文章4：MyBatis Plus
    (4, 4), (4, 5), (4, 6), (4, 7),
    -- 文章5：关于把事情做完
    (5, 10),
    -- 文章6：Vue 3 系列一
    (6, 1), (6, 3),
    -- 文章7：Vue 3 系列二
    (7, 1), (7, 2),
    -- 文章8：Vue 3 系列三
    (8, 1), (8, 2),
    -- 文章9：Spring Boot 系列一
    (9, 4), (9, 5),
    -- 文章10：Spring Boot 系列二
    (10, 4), (10, 5), (10, 6), (10, 7),
    -- 文章11：Spring Boot 系列三
    (11, 4), (11, 5), (11, 8),
    -- 文章12：深度工作
    (12, 11);

-- ============================================================
--  项目（5 个）
-- ============================================================
INSERT INTO `blog_project`
    (`project_no`, `name`, `short_name`, `description`, `tech_stack`, `highlights`,
     `start_year`, `status`, `hue`, `github_url`, `sort_order`)
VALUES
(
    'P01', 'Dear Blog', 'Blog',
    '这个博客本身。从零搭建的个人博客系统，前后端分离，Portal 使用 VitePress 静态生成，Admin 使用 Vue 3，后端 Spring Boot 3。',
    '["Vue 3", "VitePress", "Spring Boot", "MySQL", "Redis"]',
    '["前后端分离架构", "JWT 无状态认证", "自定义 VitePress 主题", "Flyway 数据库版本管理"]',
    '2024', 'Active', 200, 'https://github.com/dearblog/dear-blog', 1
),
(
    'P02', 'Mini Kanban', 'Kanban',
    '轻量级看板工具，专注于个人任务管理。无账号系统，数据存储在本地 IndexedDB，零后端依赖。',
    '["Vue 3", "TypeScript", "Vite", "IndexedDB"]',
    '["纯前端无后端", "拖拽排序", "本地数据持久化", "PWA 支持离线使用"]',
    '2024', 'Active', 145, 'https://github.com/dearblog/mini-kanban', 2
),
(
    'P03', 'Flux CLI', 'Flux',
    '命令行效率工具集合，包含代码片段管理、API 测试、JSON 格式化等常用功能，用 Rust 编写。',
    '["Rust", "Clap", "Tokio", "Serde"]',
    '["单二进制文件", "跨平台", "极低内存占用", "插件化架构"]',
    '2023', 'WIP', 30, 'https://github.com/dearblog/flux-cli', 3
),
(
    'P04', 'Spring Boot Starter Kit', 'Starter',
    '个人 Spring Boot 项目模板，集成了常用的配置和工具，一键生成符合规范的项目结构，减少重复搭建成本。',
    '["Java", "Spring Boot 3", "MyBatis Plus", "Flyway", "SpringDoc"]',
    '["统一响应格式", "全局异常处理", "JWT 认证集成", "Docker 部署模板"]',
    '2023', 'Archive', 280, 'https://github.com/dearblog/springboot-starter', 4
),
(
    'P05', 'Note Sync', 'Sync',
    '多设备笔记同步工具，使用 WebDAV 协议将 Markdown 笔记同步到任意支持 WebDAV 的存储后端（如 Nextcloud）。',
    '["TypeScript", "Node.js", "WebDAV", "Electron"]',
    '["端到端加密", "冲突自动合并", "支持 Obsidian 目录结构", "跨平台桌面客户端"]',
    '2024', 'WIP', 60, 'https://github.com/dearblog/note-sync', 5
);

-- ============================================================
--  访问统计（近 14 天模拟数据）
-- ============================================================
INSERT INTO `blog_visit_stat` (`stat_date`, `pv`, `uv`, `article_pv`)
VALUES
    (DATE_SUB(CURDATE(), INTERVAL 13 DAY), 47,  31, 28),
    (DATE_SUB(CURDATE(), INTERVAL 12 DAY), 63,  42, 40),
    (DATE_SUB(CURDATE(), INTERVAL 11 DAY), 38,  25, 22),
    (DATE_SUB(CURDATE(), INTERVAL 10 DAY), 82,  55, 61),
    (DATE_SUB(CURDATE(), INTERVAL  9 DAY), 104, 68, 78),
    (DATE_SUB(CURDATE(), INTERVAL  8 DAY), 71,  49, 53),
    (DATE_SUB(CURDATE(), INTERVAL  7 DAY), 56,  37, 41),
    (DATE_SUB(CURDATE(), INTERVAL  6 DAY), 93,  62, 70),
    (DATE_SUB(CURDATE(), INTERVAL  5 DAY), 128, 84, 97),
    (DATE_SUB(CURDATE(), INTERVAL  4 DAY), 87,  58, 65),
    (DATE_SUB(CURDATE(), INTERVAL  3 DAY), 112, 74, 85),
    (DATE_SUB(CURDATE(), INTERVAL  2 DAY), 76,  51, 58),
    (DATE_SUB(CURDATE(), INTERVAL  1 DAY), 143, 96, 112),
    (CURDATE(),                            68,  45, 52);
