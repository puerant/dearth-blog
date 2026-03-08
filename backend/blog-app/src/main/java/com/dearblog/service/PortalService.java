package com.dearblog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dearblog.common.exception.BusinessException;
import com.dearblog.common.result.ResultCode;
import com.dearblog.dto.response.ProjectVO;
import com.dearblog.dto.response.portal.*;
import com.dearblog.entity.*;
import com.dearblog.mapper.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortalService {

    private final BlogArticleMapper articleMapper;
    private final BlogArticleTagMapper articleTagMapper;
    private final BlogCategoryMapper categoryMapper;
    private final BlogTagMapper tagMapper;
    private final BlogSeriesMapper seriesMapper;
    private final BlogProjectMapper projectMapper;
    private final CategoryService categoryService;
    private final SiteConfigService siteConfigService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // ── 首页数据 ──────────────────────────────────────────────

    public PortalHomeVO getHome() {
        PortalHomeVO vo = new PortalHomeVO();

        // 精选文章（最多 5 篇）
        List<BlogArticle> featured = articleMapper.selectList(
                new LambdaQueryWrapper<BlogArticle>()
                        .eq(BlogArticle::getStatus, 2)
                        .eq(BlogArticle::getIsFeatured, true)
                        .le(BlogArticle::getPublishTime, java.time.LocalDateTime.now())
                        .orderByDesc(BlogArticle::getPublishTime)
                        .last("LIMIT 5"));
        vo.setFeaturedArticles(toArticleListVOs(featured));

        // 最近文章（最多 10 篇）
        List<BlogArticle> recent = articleMapper.selectList(
                new LambdaQueryWrapper<BlogArticle>()
                        .eq(BlogArticle::getStatus, 2)
                        .le(BlogArticle::getPublishTime, java.time.LocalDateTime.now())
                        .orderByDesc(BlogArticle::getPublishTime)
                        .last("LIMIT 10"));
        vo.setRecentArticles(toArticleListVOs(recent));

        // 站点配置
        vo.setSiteConfig(siteConfigService.getAll());

        // 统计数
        vo.setTotalArticles(articleMapper.selectCount(
                new LambdaQueryWrapper<BlogArticle>().eq(BlogArticle::getStatus, 2)));
        vo.setTotalCategories(categoryMapper.selectCount(null));
        vo.setTotalTags(tagMapper.selectCount(null));
        vo.setTotalSeries(seriesMapper.selectCount(null));

        // 项目列表
        List<BlogProject> projects = projectMapper.selectList(
                new LambdaQueryWrapper<BlogProject>().orderByAsc(BlogProject::getSortOrder));
        vo.setProjects(projects.stream().map(this::toProjectVO).collect(Collectors.toList()));

        // 分类树（供部分场景复用）
        vo.setCategories(listCategories());

        // 全部标签
        vo.setTags(listTags());

        // 最近系列（最多 5 个）
        List<BlogSeries> recentSeries = seriesMapper.selectList(
                new LambdaQueryWrapper<BlogSeries>()
                        .orderByDesc(BlogSeries::getCreatedAt)
                        .last("LIMIT 5"));
        vo.setRecentSeries(recentSeries.stream().map(this::toSeriesVO).collect(Collectors.toList()));

        return vo;
    }

    // ── 文章列表（分页）────────────────────────────────────────

    public IPage<PortalArticleListVO> listArticles(int page, int size,
                                                   String categorySlug, String tagSlug,
                                                   String keyword) {
        List<Long> idFilter = null;

        if (StringUtils.hasText(categorySlug)) {
            BlogCategory category = categoryMapper.selectOne(new LambdaQueryWrapper<BlogCategory>()
                    .eq(BlogCategory::getSlug, categorySlug));
            if (category == null) return new Page<>(page, size);
            List<Long> categoryIds = categoryService.collectCategoryIds(category.getId());
            List<Long> ids = articleMapper.selectPublishedIdsByCategoryIds(categoryIds);
            idFilter = ids;
        }

        if (StringUtils.hasText(tagSlug)) {
            List<Long> ids = articleMapper.selectPublishedIdsByTagSlug(tagSlug);
            idFilter = (idFilter == null) ? ids : intersect(idFilter, ids);
        }

        if (idFilter != null && idFilter.isEmpty()) return new Page<>(page, size);

        final List<Long> finalFilter = idFilter;
        LambdaQueryWrapper<BlogArticle> wrapper = new LambdaQueryWrapper<BlogArticle>()
                .eq(BlogArticle::getStatus, 2)
                .le(BlogArticle::getPublishTime, java.time.LocalDateTime.now())
                .in(finalFilter != null, BlogArticle::getId, finalFilter)
                .like(StringUtils.hasText(keyword), BlogArticle::getTitle, keyword)
                .orderByDesc(BlogArticle::getPublishTime);

        IPage<BlogArticle> articlePage = articleMapper.selectPage(new Page<>(page, size), wrapper);
        return articlePage.convert(this::toArticleListVO);
    }

    // ── 文章详情 ───────────────────────────────────────────────

    public PortalArticleDetailVO getArticleBySlug(String slug) {
        BlogArticle article = articleMapper.selectOne(new LambdaQueryWrapper<BlogArticle>()
                .eq(BlogArticle::getSlug, slug)
                .eq(BlogArticle::getStatus, 2));
        if (article == null) throw new BusinessException(ResultCode.NOT_FOUND);

        // 增加浏览量
        articleMapper.update(null, new LambdaUpdateWrapper<BlogArticle>()
                .setSql("view_count = view_count + 1")
                .eq(BlogArticle::getId, article.getId()));
        article.setViewCount(article.getViewCount() + 1);

        // 构建详情 VO
        BlogCategory category = article.getCategoryId() != null
                ? categoryMapper.selectById(article.getCategoryId()) : null;
        BlogSeries series = article.getSeriesId() != null
                ? seriesMapper.selectById(article.getSeriesId()) : null;

        List<BlogArticleTagMapper.ArticleTagView> tagViews =
                articleTagMapper.selectTagsByArticleIds(List.of(article.getId()));
        List<PortalTagVO> tags = tagViews.stream().map(tv -> {
            PortalTagVO t = new PortalTagVO();
            t.setId(tv.getTagId());
            t.setName(tv.getName());
            t.setSlug(tv.getSlug());
            return t;
        }).collect(Collectors.toList());

        PortalArticleDetailVO vo = new PortalArticleDetailVO();
        copyArticleFields(article, vo, category, series, tags);
        vo.setContent(article.getContent());
        vo.setContentType(article.getContentType());

        if (series != null) {
            vo.setSeriesId(series.getId());
            vo.setSeriesName(series.getName());
            vo.setSeriesSlug(series.getSlug());
            vo.setSeriesSort(article.getSeriesSort());
            setPrevNext(vo, article);
        }
        return vo;
    }

    // ── 分类 ──────────────────────────────────────────────────

    public List<PortalCategoryVO> listCategories() {
        List<BlogCategory> all = categoryMapper.selectList(
                new LambdaQueryWrapper<BlogCategory>().orderByAsc(BlogCategory::getSortOrder));
        return buildCategoryTree(all);
    }

    // ── 标签 ──────────────────────────────────────────────────

    public List<PortalTagVO> listTags() {
        return tagMapper.selectList(null).stream().map(t -> {
            PortalTagVO vo = new PortalTagVO();
            vo.setId(t.getId());
            vo.setName(t.getName());
            vo.setSlug(t.getSlug());
            vo.setArticleCount(t.getArticleCount());
            return vo;
        }).collect(Collectors.toList());
    }

    // ── 系列 ──────────────────────────────────────────────────

    public List<PortalSeriesVO> listSeries() {
        return seriesMapper.selectList(
                new LambdaQueryWrapper<BlogSeries>().orderByAsc(BlogSeries::getSortOrder))
                .stream().map(this::toSeriesVO).collect(Collectors.toList());
    }

    public PortalSeriesVO getSeriesBySlug(String slug) {
        BlogSeries series = seriesMapper.selectOne(new LambdaQueryWrapper<BlogSeries>()
                .eq(BlogSeries::getSlug, slug));
        if (series == null) throw new BusinessException(ResultCode.NOT_FOUND);

        PortalSeriesVO vo = toSeriesVO(series);
        List<BlogArticle> articles = articleMapper.selectPublishedBySeriesId(series.getId());
        vo.setArticles(toArticleListVOs(articles));
        return vo;
    }

    // ── 归档 ──────────────────────────────────────────────────

    public List<PortalArchiveVO> listArchive() {
        List<BlogArticle> articles = articleMapper.selectList(
                new LambdaQueryWrapper<BlogArticle>()
                        .select(BlogArticle::getId, BlogArticle::getTitle,
                                BlogArticle::getSlug, BlogArticle::getPublishTime,
                                BlogArticle::getCategoryId)
                        .eq(BlogArticle::getStatus, 2)
                        .le(BlogArticle::getPublishTime, java.time.LocalDateTime.now())
                        .orderByDesc(BlogArticle::getPublishTime));

        // 批量加载分类名
        Set<Long> catIds = articles.stream().map(BlogArticle::getCategoryId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> catNameMap = new HashMap<>();
        if (!catIds.isEmpty()) {
            categoryMapper.selectBatchIds(catIds)
                    .forEach(c -> catNameMap.put(c.getId(), c.getName()));
        }

        // 按年份分组，再按月份分组
        Map<Integer, Map<Integer, List<BlogArticle>>> grouped = new TreeMap<>(Comparator.reverseOrder());
        for (BlogArticle a : articles) {
            if (a.getPublishTime() == null) continue;
            int year = a.getPublishTime().getYear();
            int month = a.getPublishTime().getMonthValue();
            grouped.computeIfAbsent(year, k -> new TreeMap<>(Comparator.reverseOrder()))
                    .computeIfAbsent(month, k -> new ArrayList<>())
                    .add(a);
        }

        List<PortalArchiveVO> result = new ArrayList<>();
        grouped.forEach((year, months) -> {
            PortalArchiveVO archiveVO = new PortalArchiveVO();
            archiveVO.setYear(year);
            List<PortalArchiveVO.MonthEntry> monthList = new ArrayList<>();
            months.forEach((month, monthArticles) -> {
                PortalArchiveVO.MonthEntry entry = new PortalArchiveVO.MonthEntry();
                entry.setMonth(month);
                entry.setArticles(monthArticles.stream().map(a -> {
                    PortalArchiveVO.ArticleLinkVO link = new PortalArchiveVO.ArticleLinkVO();
                    link.setId(a.getId());
                    link.setTitle(a.getTitle());
                    link.setSlug(a.getSlug());
                    link.setPublishTime(a.getPublishTime());
                    link.setCategoryName(catNameMap.get(a.getCategoryId()));
                    return link;
                }).collect(Collectors.toList()));
                monthList.add(entry);
            });
            archiveVO.setMonths(monthList);
            result.add(archiveVO);
        });
        return result;
    }

    // ── 私有辅助 ───────────────────────────────────────────────

    private List<PortalArticleListVO> toArticleListVOs(List<BlogArticle> articles) {
        if (articles.isEmpty()) return Collections.emptyList();

        List<Long> ids = articles.stream().map(BlogArticle::getId).collect(Collectors.toList());
        Map<Long, List<PortalTagVO>> tagMap = buildTagMap(ids);

        Set<Long> categoryIds = articles.stream().map(BlogArticle::getCategoryId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, BlogCategory> categoryMap = new HashMap<>();
        if (!categoryIds.isEmpty()) {
            categoryMapper.selectBatchIds(categoryIds).forEach(c -> categoryMap.put(c.getId(), c));
        }

        Set<Long> seriesIds = articles.stream().map(BlogArticle::getSeriesId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, BlogSeries> seriesMap = new HashMap<>();
        if (!seriesIds.isEmpty()) {
            seriesMapper.selectBatchIds(seriesIds).forEach(s -> seriesMap.put(s.getId(), s));
        }

        return articles.stream()
                .map(a -> toArticleListVO(a, categoryMap.get(a.getCategoryId()),
                        seriesMap.get(a.getSeriesId()), tagMap))
                .collect(Collectors.toList());
    }

    private PortalArticleListVO toArticleListVO(BlogArticle article) {
        BlogCategory category = article.getCategoryId() != null
                ? categoryMapper.selectById(article.getCategoryId()) : null;
        BlogSeries series = article.getSeriesId() != null
                ? seriesMapper.selectById(article.getSeriesId()) : null;
        List<BlogArticleTagMapper.ArticleTagView> tagViews =
                articleTagMapper.selectTagsByArticleIds(List.of(article.getId()));
        List<PortalTagVO> tags = tagViews.stream().map(tv -> {
            PortalTagVO t = new PortalTagVO();
            t.setId(tv.getTagId());
            t.setName(tv.getName());
            t.setSlug(tv.getSlug());
            return t;
        }).collect(Collectors.toList());
        PortalArticleListVO vo = new PortalArticleListVO();
        copyArticleFields(article, vo, category, series, tags);
        return vo;
    }

    private PortalArticleListVO toArticleListVO(BlogArticle article, BlogCategory category,
                                                BlogSeries series,
                                                Map<Long, List<PortalTagVO>> tagMap) {
        PortalArticleListVO vo = new PortalArticleListVO();
        copyArticleFields(article, vo, category, series,
                tagMap.getOrDefault(article.getId(), Collections.emptyList()));
        return vo;
    }

    private void copyArticleFields(BlogArticle article, PortalArticleListVO vo,
                                   BlogCategory category, BlogSeries series,
                                   List<PortalTagVO> tags) {
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setSlug(article.getSlug());
        vo.setSummary(article.getSummary());
        vo.setCoverImage(article.getCoverImage());
        vo.setContentType(article.getContentType());
        vo.setPublishTime(article.getPublishTime());
        vo.setViewCount(article.getViewCount());
        vo.setIsFeatured(article.getIsFeatured());
        vo.setStatus(article.getStatus());
        vo.setSeriesId(article.getSeriesId());
        vo.setSeriesSort(article.getSeriesSort());
        vo.setTags(tags);
        if (category != null) {
            vo.setCategoryId(category.getId());
            vo.setCategoryName(category.getName());
            vo.setCategorySlug(category.getSlug());
        }
        if (series != null) {
            vo.setSeriesName(series.getName());
        }
        String content = article.getContent();
        if (content != null) {
            vo.setReadingTime(Math.max(1, (int) Math.ceil((double) content.length() / 400)));
        }
    }

    private void setPrevNext(PortalArticleDetailVO vo, BlogArticle article) {
        List<BlogArticle> seriesArticles = articleMapper.selectPublishedBySeriesId(article.getSeriesId());
        for (int i = 0; i < seriesArticles.size(); i++) {
            if (seriesArticles.get(i).getId().equals(article.getId())) {
                if (i > 0) {
                    BlogArticle prev = seriesArticles.get(i - 1);
                    PortalArticleDetailVO.ArticleLinkVO link = new PortalArticleDetailVO.ArticleLinkVO();
                    link.setId(prev.getId());
                    link.setTitle(prev.getTitle());
                    link.setSlug(prev.getSlug());
                    vo.setPrevArticle(link);
                }
                if (i < seriesArticles.size() - 1) {
                    BlogArticle next = seriesArticles.get(i + 1);
                    PortalArticleDetailVO.ArticleLinkVO link = new PortalArticleDetailVO.ArticleLinkVO();
                    link.setId(next.getId());
                    link.setTitle(next.getTitle());
                    link.setSlug(next.getSlug());
                    vo.setNextArticle(link);
                }
                break;
            }
        }
    }

    private Map<Long, List<PortalTagVO>> buildTagMap(List<Long> articleIds) {
        return articleTagMapper.selectTagsByArticleIds(articleIds).stream()
                .collect(Collectors.groupingBy(
                        BlogArticleTagMapper.ArticleTagView::getArticleId,
                        Collectors.mapping(tv -> {
                            PortalTagVO t = new PortalTagVO();
                            t.setId(tv.getTagId());
                            t.setName(tv.getName());
                            t.setSlug(tv.getSlug());
                            return t;
                        }, Collectors.toList())));
    }

    private List<PortalCategoryVO> buildCategoryTree(List<BlogCategory> all) {
        Map<Long, PortalCategoryVO> map = new LinkedHashMap<>();
        for (BlogCategory c : all) {
            PortalCategoryVO vo = new PortalCategoryVO();
            vo.setId(c.getId());
            vo.setName(c.getName());
            vo.setSlug(c.getSlug());
            vo.setDescription(c.getDescription());
            vo.setParentId(c.getParentId());
            vo.setArticleCount(c.getArticleCount());
            map.put(c.getId(), vo);
        }
        List<PortalCategoryVO> roots = new ArrayList<>();
        for (BlogCategory c : all) {
            PortalCategoryVO vo = map.get(c.getId());
            if (c.getParentId() == null || c.getParentId() == 0) {
                roots.add(vo);
            } else {
                PortalCategoryVO parent = map.get(c.getParentId());
                if (parent != null) parent.getChildren().add(vo);
            }
        }
        return roots;
    }

    private PortalSeriesVO toSeriesVO(BlogSeries s) {
        PortalSeriesVO vo = new PortalSeriesVO();
        vo.setId(s.getId());
        vo.setName(s.getName());
        vo.setSlug(s.getSlug());
        vo.setDescription(s.getDescription());
        vo.setCoverImage(s.getCoverImage());
        vo.setArticleCount(s.getArticleCount());
        vo.setSortOrder(s.getSortOrder());
        return vo;
    }

    private ProjectVO toProjectVO(BlogProject p) {
        ProjectVO vo = new ProjectVO();
        vo.setId(p.getId());
        vo.setProjectNo(p.getProjectNo());
        vo.setName(p.getName());
        vo.setShortName(p.getShortName());
        vo.setDescription(p.getDescription());
        vo.setStartYear(p.getStartYear());
        vo.setStatus(p.getStatus());
        vo.setHue(p.getHue());
        vo.setGithubUrl(p.getGithubUrl());
        vo.setSortOrder(p.getSortOrder());
        try {
            if (p.getTechStack() != null) {
                vo.setTechStack(objectMapper.readValue(p.getTechStack(),
                        new TypeReference<List<String>>() {}));
            }
            if (p.getHighlights() != null) {
                vo.setHighlights(objectMapper.readValue(p.getHighlights(),
                        new TypeReference<List<String>>() {}));
            }
        } catch (Exception ignored) {}
        return vo;
    }

    private <T> List<T> intersect(List<T> a, List<T> b) {
        Set<T> setB = new HashSet<>(b);
        return a.stream().filter(setB::contains).collect(Collectors.toList());
    }
}
