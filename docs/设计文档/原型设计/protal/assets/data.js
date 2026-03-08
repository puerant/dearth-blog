/**
 * dear·blog — Mock Data
 * 所有原型页面共享此数据
 */

window.SITE = {
  name: "dear·blog",
  author: "林一",
  tagline: "记录技术与生活的交汇处",
  bio: "喜欢用代码解决问题，用文字整理思路。写技术也写生活，这里没有宏大叙事，只有真实的日常。",
  location: "北京",
  since: "2022",
  email: "hello@dearblog.dev",
  github: "https://github.com",
  skills: [
    "Vue.js", "TypeScript", "React", "Vite",
    "Spring Boot", "Java", "MyBatis",
    "MySQL", "Redis", "Docker",
    "Linux", "Nginx", "Git"
  ]
};

window.CATEGORIES = [
  {
    id: 1, name: "技术思考", slug: "tech-thoughts", count: 4,
    children: []
  },
  {
    id: 2, name: "前端开发", slug: "frontend", count: 5,
    children: [
      { id: 21, name: "Vue.js", slug: "vue", count: 3, parentId: 2 },
      { id: 22, name: "工程化", slug: "engineering", count: 2, parentId: 2 }
    ]
  },
  {
    id: 3, name: "后端架构", slug: "backend", count: 4,
    children: [
      { id: 31, name: "Java", slug: "java", count: 3, parentId: 3 },
      { id: 32, name: "数据库", slug: "database", count: 2, parentId: 3 }
    ]
  },
  {
    id: 4, name: "生活随笔", slug: "life", count: 3,
    children: []
  },
  {
    id: 5, name: "读书笔记", slug: "reading", count: 2,
    children: []
  }
];

window.TAGS = [
  { id: 1,  name: "Vue.js",       slug: "vuejs",       count: 4 },
  { id: 2,  name: "TypeScript",   slug: "typescript",  count: 5 },
  { id: 3,  name: "JavaScript",   slug: "javascript",  count: 3 },
  { id: 4,  name: "Spring Boot",  slug: "springboot",  count: 3 },
  { id: 5,  name: "Java",         slug: "java",        count: 4 },
  { id: 6,  name: "MySQL",        slug: "mysql",       count: 2 },
  { id: 7,  name: "Redis",        slug: "redis",       count: 2 },
  { id: 8,  name: "Docker",       slug: "docker",      count: 2 },
  { id: 9,  name: "CSS",          slug: "css",         count: 2 },
  { id: 10, name: "性能优化",     slug: "performance", count: 2 },
  { id: 11, name: "读书",         slug: "reading",     count: 2 },
  { id: 12, name: "旅行",         slug: "travel",      count: 1 },
  { id: 13, name: "架构",         slug: "architecture",count: 2 },
  { id: 14, name: "工程化",       slug: "engineering", count: 2 },
  { id: 15, name: "VitePress",    slug: "vitepress",   count: 1 }
];

window.ARTICLES = [
  {
    id: 1,
    title: "为什么我选择用 VitePress 搭建博客",
    slug: "why-vitepress-for-blog",
    category: "技术思考",
    categorySlug: "tech-thoughts",
    tags: ["VitePress", "Vue.js", "工程化"],
    date: "2024-03-08",
    readingTime: 6,
    featured: true,
    excerpt: "在内容平台遍地都是的今天，我还是决定自己搭一个博客。选型过程踩了不少坑，最终选择了 VitePress。记录一下决策过程和背后的思考。",
    content: `
      <p>在这个信息过剩的时代，公众号、知乎、掘金……随便拿一个平台都能发文章。那我为什么还要折腾自己搭一个博客呢？</p>
      <h2 id="why">为什么要自建博客</h2>
      <p>简单说三点：数据主权、设计自由度、以及一种隐约的仪式感。</p>
      <h3 id="data">数据主权</h3>
      <p>把内容托管在平台上，本质上是在把自己的思考寄存在别人的服务器里。平台改规则、倒闭、封号……这些风险都是真实存在的。自建博客，数据在自己手里。</p>
      <h3 id="design">设计自由度</h3>
      <p>我对排版和阅读体验有些执念。主流平台的编辑器和展示样式很难深度定制，而自建博客可以把每一个细节调到自己满意为止。</p>
      <h2 id="vitepress">为什么选 VitePress</h2>
      <p>对比了 Hexo、Hugo、Astro 和 VitePress，最终选择了 VitePress。核心原因是它与 Vue 生态无缝集成，而且构建速度极快。</p>
      <h3 id="ssg">SSG 的优势</h3>
      <p>博客内容变更频率低，非常适合静态预渲染。构建一次，所有页面都是静态 HTML，部署简单，访问速度快，不需要维护复杂的服务端逻辑。</p>
      <h3 id="ecosystem">生态与扩展性</h3>
      <p>VitePress 基于 Vite 构建，插件生态完整。需要代码高亮、数学公式、自定义组件，全都有现成方案可以集成。</p>
      <h2 id="arch">架构设计</h2>
      <p>Portal 使用 VitePress 做 SSG，Admin 后台用 Vue 3 + Vite 做 CSR，后端用 Spring Boot 3 提供 RESTful API。</p>
      <p>这样的分层架构让前后端完全解耦，Portal 可以独立部署为纯静态文件，Admin 和后端各自演进。</p>
      <h2 id="summary">总结</h2>
      <p>技术选型没有绝对正确答案，适合自己的才是最好的。VitePress 对我来说，在开发体验、性能和可维护性之间找到了很好的平衡点。</p>
      <p>后续会写系列文章，记录从零搭建这个博客的全过程。如果你也在考虑自建博客，希望这篇文章对你有所帮助。</p>
    `
  },
  {
    id: 2,
    title: "TypeScript 类型体操：从入门到放弃",
    slug: "typescript-type-gymnastics",
    category: "前端开发",
    categorySlug: "frontend",
    tags: ["TypeScript", "JavaScript"],
    date: "2024-02-20",
    readingTime: 10,
    featured: true,
    excerpt: "用了一年多的 TypeScript，发现最让人头疼的不是类型本身，而是那些复杂的类型操作——条件类型、映射类型、infer……整理一下我的学习路径。",
    content: `<p>TypeScript 的类型系统远比大多数人想象的强大。普通使用不需要了解太深，但一旦开始写工具类型或给库写类型声明，就会进入「类型体操」的世界。</p>`
  },
  {
    id: 3,
    title: "Spring Boot 3 与 Spring Security 6 的变化",
    slug: "spring-boot3-security6",
    category: "后端架构",
    categorySlug: "backend",
    tags: ["Spring Boot", "Java", "架构"],
    date: "2024-02-05",
    readingTime: 8,
    featured: true,
    excerpt: "Spring Boot 3 正式发布已经有一段时间了，随之而来的还有 Spring Security 6 的大变革。WebSecurityConfigurerAdapter 被移除，配置方式彻底改变……",
    content: `<p>Spring Security 6 的最大变化是废弃了 WebSecurityConfigurerAdapter，转而推荐使用函数式配置风格。</p>`
  },
  {
    id: 4,
    title: "Vue 3 Composition API 最佳实践",
    slug: "vue3-composition-api-best-practices",
    category: "前端开发",
    categorySlug: "frontend",
    tags: ["Vue.js", "TypeScript"],
    date: "2024-01-18",
    readingTime: 7,
    featured: false,
    excerpt: "从 Vue 2 迁移到 Vue 3 已经一年多，Composition API 用顺手之后确实比 Options API 灵活很多。整理一些实际项目中总结出来的最佳实践。",
    content: `<p>Composition API 最大的优势是逻辑复用和代码组织。</p>`
  },
  {
    id: 5,
    title: "Docker Compose 实践：本地开发环境搭建",
    slug: "docker-compose-local-dev",
    category: "后端架构",
    categorySlug: "backend",
    tags: ["Docker", "工程化"],
    date: "2024-01-08",
    readingTime: 6,
    featured: false,
    excerpt: "本地开发环境的一致性问题困扰了我很久，直到用上 Docker Compose。现在一条命令就能起整套服务，MySQL、Redis、应用服务全部容器化管理。",
    content: `<p>Docker Compose 解决了「在我机器上是好的」这个经典问题。</p>`
  },
  {
    id: 6,
    title: "MySQL 索引原理与优化实践",
    slug: "mysql-index-optimization",
    category: "后端架构",
    categorySlug: "backend",
    tags: ["MySQL", "性能优化", "数据库"],
    date: "2023-12-14",
    readingTime: 12,
    featured: false,
    excerpt: "面试必问的 MySQL 索引，很多人背过八股文却说不清楚底层原理。这篇文章从 B+ 树数据结构出发，结合实际案例讲清楚索引是如何工作的。",
    content: `<p>MySQL InnoDB 的索引底层使用 B+ 树实现，理解这个数据结构是理解索引的关键。</p>`
  },
  {
    id: 7,
    title: "《人月神话》读后感：软件工程的本质",
    slug: "mythical-man-month-review",
    category: "读书笔记",
    categorySlug: "reading",
    tags: ["读书", "架构"],
    date: "2023-11-28",
    readingTime: 9,
    featured: false,
    excerpt: "这本写于 1975 年的书，放到今天来读依然振聋发聩。「没有银弹」「焦油坑」「第二个系统效应」……布鲁克斯的洞见穿越了将近半个世纪。",
    content: `<p>《人月神话》是软件工程领域的经典著作，其中的观点至今仍然适用。</p>`
  },
  {
    id: 8,
    title: "前端性能优化：从瀑布图看加载过程",
    slug: "frontend-performance-optimization",
    category: "前端开发",
    categorySlug: "frontend",
    tags: ["性能优化", "工程化"],
    date: "2023-11-10",
    readingTime: 11,
    featured: false,
    excerpt: "性能优化不是玄学，打开 Chrome DevTools 的 Network 面板，一切都在瀑布图里。这篇文章从分析真实加载过程入手，讲解常见的性能优化手段。",
    content: `<p>性能优化要从测量开始，不要凭感觉优化。</p>`
  },
  {
    id: 9,
    title: "用 Redis 实现分布式锁",
    slug: "redis-distributed-lock",
    category: "后端架构",
    categorySlug: "backend",
    tags: ["Redis", "Java", "架构"],
    date: "2023-10-22",
    readingTime: 8,
    featured: false,
    excerpt: "分布式环境下，多个服务实例同时操作共享资源，需要分布式锁来保证互斥。用 Redis 实现分布式锁看似简单，但细节很多，坑也不少。",
    content: `<p>分布式锁需要满足互斥性、可重入性、避免死锁等特性。</p>`
  },
  {
    id: 10,
    title: "Tailwind CSS 使用心得",
    slug: "tailwind-css-experience",
    category: "前端开发",
    categorySlug: "frontend",
    tags: ["CSS", "工程化"],
    date: "2023-10-05",
    readingTime: 5,
    featured: false,
    excerpt: "用 Tailwind CSS 写了几个项目之后，从最初的排斥到现在的习惯，逐渐理解了原子化 CSS 的设计哲学。聊聊我的使用心得和一些实践技巧。",
    content: `<p>Tailwind 的原子化 CSS 理念起初让很多人不适应，但用久了就会发现它的高效。</p>`
  },
  {
    id: 11,
    title: "一个人去西藏",
    slug: "solo-trip-to-tibet",
    category: "生活随笔",
    categorySlug: "life",
    tags: ["旅行"],
    date: "2023-09-01",
    readingTime: 7,
    featured: false,
    excerpt: "请了两周假，背着包一个人去了西藏。高原反应、寺庙、藏民、牦牛……记录那些让我安静下来的瞬间。",
    content: `<p>很多事情，不去做就永远不知道自己能不能做到。</p>`
  },
  {
    id: 12,
    title: "2023 年终总结：代码之外",
    slug: "2023-year-end-review",
    category: "生活随笔",
    categorySlug: "life",
    tags: ["读书"],
    date: "2023-12-31",
    readingTime: 6,
    featured: false,
    excerpt: "2023 年就要结束了。写代码之外，读了二十多本书，去了三个城市，开始练字，换了一个更喜欢的工作。记录一下这一年的收获与遗憾。",
    content: `<p>回顾这一年，最大的收获不是技术上的进步，而是找到了生活的节奏。</p>`
  }
];

window.SERIES = [
  {
    id: 1,
    name: "从零搭建博客系统",
    slug: "build-blog-from-scratch",
    desc: "记录从需求分析到最终上线的完整过程，涵盖技术选型、架构设计、前后端开发和服务器部署，适合想自建博客的开发者参考。",
    articleCount: 3,
    articles: [
      { id: 1,  title: "为什么我选择用 VitePress 搭建博客",       date: "2024-03-08" },
      { id: 13, title: "从零搭建博客系统（二）：数据库设计",       date: "2024-03-20" },
      { id: 14, title: "从零搭建博客系统（三）：前端工程化配置",   date: "2024-04-01" }
    ]
  },
  {
    id: 2,
    name: "Java 进阶系列",
    slug: "java-advanced",
    desc: "深入 Java 并发、JVM、设计模式等进阶话题，结合实际工作场景，避免纯理论讲解。",
    articleCount: 3,
    articles: [
      { id: 9,  title: "用 Redis 实现分布式锁",         date: "2023-10-22" },
      { id: 3,  title: "Spring Boot 3 与 Spring Security 6 的变化", date: "2024-02-05" },
      { id: 6,  title: "MySQL 索引原理与优化实践",       date: "2023-12-14" }
    ]
  },
  {
    id: 3,
    name: "前端工程化实践",
    slug: "frontend-engineering",
    desc: "从开发环境搭建到 CI/CD 流水线，记录前端工程化的各个环节，让团队协作更高效。",
    articleCount: 2,
    articles: [
      { id: 10, title: "Tailwind CSS 使用心得",             date: "2023-10-05" },
      { id: 8,  title: "前端性能优化：从瀑布图看加载过程",  date: "2023-11-10" }
    ]
  }
];

/* ── 工具函数 ── */

window.getArticlesByCategory = function(slug) {
  return ARTICLES.filter(a => a.categorySlug === slug ||
    CATEGORIES.flatMap(c => c.children).find(ch => ch.slug === slug && a.categorySlug === CATEGORIES.find(c => c.children.some(cc => cc.slug === slug))?.slug)
  );
};

window.getArticlesByTag = function(slug) {
  return ARTICLES.filter(a => a.tags.some(t =>
    t.toLowerCase().replace(/[.\s]/g, '') === slug.replace(/-/g, '').toLowerCase() ||
    TAGS.find(tag => tag.slug === slug)?.name === t
  ));
};

window.formatDate = function(dateStr) {
  const d = new Date(dateStr);
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${y}.${m}.${day}`;
};

window.formatDateShort = function(dateStr) {
  const d = new Date(dateStr);
  return `${d.getMonth() + 1}月${d.getDate()}日`;
};

window.groupByYear = function(articles) {
  const groups = {};
  articles.forEach(a => {
    const year = a.date.slice(0, 4);
    if (!groups[year]) groups[year] = {};
    const month = a.date.slice(5, 7);
    if (!groups[year][month]) groups[year][month] = [];
    groups[year][month].push(a);
  });
  return groups;
};

window.totalArticles = ARTICLES.length;
window.totalCategories = CATEGORIES.length;
window.totalTags = TAGS.length;

window.PROJECTS = [
  {
    id: 'P01',
    name: 'dear·blog',
    short: 'BLOG',
    desc: '个人博客系统。从选型、设计到上线全程手搓，VitePress 静态生成 + Spring Boot 后端，记录每一次折腾的过程。',
    tech: ['VitePress', 'Spring Boot 3', 'MySQL'],
    year: '2022',
    status: 'Active',
    hue: 38,
    github: 'https://github.com',
    highlights: [
      'SSG + CSR 双端架构，Portal 静态预渲染极速访问，Admin 独立管理后台',
      'Spring Boot 3 REST API，JWT 认证，Redis Token 黑名单',
      'Markdown + 富文本双写作模式，分类 / 标签 / 系列三维内容组织',
      'Docker Compose 一键部署，Nginx 反向代理与静态文件托管'
    ]
  },
  {
    id: 'P02',
    name: 'vue-component-kit',
    short: 'KIT',
    desc: '基于 Vue 3 + TypeScript 的轻量 UI 组件库，覆盖后台管理系统的常用场景，支持按需引入。',
    tech: ['Vue 3', 'TypeScript', 'Vite'],
    year: '2023',
    status: 'Archive',
    hue: 210,
    github: 'https://github.com',
    highlights: [
      '30+ 业务组件，覆盖表单、表格、弹窗等后台管理常见场景',
      'TypeScript 完整类型声明，提供 IDE 智能提示与类型安全',
      '支持按需引入，Tree Shaking 友好，不引入冗余代码',
      'Props / Events / Slots 设计规范统一，文档自动生成'
    ]
  },
  {
    id: 'P03',
    name: 'spring-scaffold',
    short: 'BOOT',
    desc: 'Spring Boot 3 项目脚手架。集成 JWT 认证、统一异常处理、MyBatis Plus、日志链路追踪，开箱即用。',
    tech: ['Java 17', 'MyBatis Plus', 'Redis'],
    year: '2023',
    status: 'Active',
    hue: 150,
    github: 'https://github.com',
    highlights: [
      'JWT + Spring Security 6 认证体系，开箱即用，无需重复造轮子',
      '全局统一异常处理与响应体封装，规范 API 返回格式',
      'MyBatis Plus 集成，Flyway 数据库版本迁移管理',
      'Hutool 工具集 + MDC 链路追踪日志，排查问题更高效'
    ]
  },
  {
    id: 'P04',
    name: 'docker-workshop',
    short: 'DOCK',
    desc: '本地开发环境 Docker Compose 配置集。一条命令启动 MySQL、Redis、Nginx 等所有开发依赖。',
    tech: ['Docker', 'Nginx', 'Shell'],
    year: '2024',
    status: 'Active',
    hue: 195,
    github: 'https://github.com',
    highlights: [
      '一条命令启动完整本地开发环境，告别环境配置地狱',
      '涵盖 MySQL 8、Redis 7、Nginx 多服务编排配置',
      '挂载卷持久化，配置热重载，开发体验流畅',
      '附带常用 Shell 脚本，简化日常运维与数据初始化操作'
    ]
  },
  {
    id: 'P05',
    name: 'md-toolkit',
    short: 'MKDN',
    desc: 'Markdown 写作辅助 CLI 工具。自动格式化、检查死链、统计字数与阅读时间，支持批量处理。',
    tech: ['Node.js', 'TypeScript', 'CLI'],
    year: '2024',
    status: 'Active',
    hue: 270,
    github: 'https://github.com',
    highlights: [
      '自动格式化 Markdown，统一文档风格与排版规范',
      '批量检查死链，本地与远程链接均支持',
      '字数与阅读时长精确统计，支持目录级批量处理',
      'Node.js CLI，npm 全局安装即用，无其他运行时依赖'
    ]
  },
  {
    id: 'P06',
    name: 'perf-lab',
    short: 'PERF',
    desc: 'JVM 与 MySQL 性能调优实验记录。包含可复现的 Benchmark 案例，配套详细的分析文章。',
    tech: ['Java', 'JMH', 'MySQL'],
    year: '2023',
    status: 'Archive',
    hue: 28,
    github: 'https://github.com',
    highlights: [
      'JVM GC 调优实验，包含可复现的 Benchmark 基准测试',
      'MySQL 慢查询分析与索引优化实践，从执行计划出发',
      '每个实验配套详细的分析文章，数据真实可信',
      'JMH 基准测试框架集成，结果稳定可复现'
    ]
  },
  {
    id: 'P07',
    name: 'linux-notes',
    short: 'LNUX',
    desc: '服务器运维笔记集。Nginx 配置、Shell 脚本、定时任务、日志分析，踩坑记录与最佳实践。',
    tech: ['Linux', 'Bash', 'Nginx'],
    year: '2022',
    status: 'Archive',
    hue: 60,
    github: 'https://github.com',
    highlights: [
      'Nginx 配置实战：反向代理、SSL 证书、静态文件托管',
      'Shell 脚本库：自动化部署、数据备份、日志分析',
      'crontab 定时任务设计与监控告警配置',
      '服务器安全加固基线配置，防火墙与 SSH 安全规范'
    ]
  },
  {
    id: 'P08',
    name: 'idea-plugin-exp',
    short: 'IDEA',
    desc: '自用 IntelliJ IDEA 插件实验项目。探索 PSI 树操作与自动生成代码的可能性，还在折腾中。',
    tech: ['Java', 'IntelliJ SDK', 'Gradle'],
    year: '2024',
    status: 'WIP',
    hue: 330,
    github: 'https://github.com',
    highlights: [
      'IntelliJ SDK PSI 树操作探索，理解 IDE 的代码分析机制',
      '自动生成 DTO / VO 样板代码，减少重复劳动',
      'Action 与 Inspection 插件扩展点实验',
      '持续折腾中，边学边记录踩过的坑'
    ]
  }
];
