package com.dearblog.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dearblog.common.exception.BusinessException;
import com.dearblog.common.result.ResultCode;
import com.dearblog.dto.request.ArticleSaveRequest;
import com.dearblog.dto.response.ArticleDetailVO;
import com.dearblog.dto.response.ArticleListVO;
import com.dearblog.dto.response.TagVO;
import com.dearblog.entity.*;
import com.dearblog.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final BlogArticleMapper articleMapper;
    private final BlogArticleTagMapper articleTagMapper;
    private final BlogCategoryMapper categoryMapper;
    private final BlogTagMapper tagMapper;
    private final BlogSeriesMapper seriesMapper;

    // ── Admin: 分页列表 ────────────────────────────────────────

    public IPage<ArticleListVO> adminList(int page, int size,
                                          String title, Integer status,
                                          Long categoryId, String tagSlug) {
        // 标签过滤：先取文章 ID 列表
        List<Long> tagArticleIds = null;
        if (StringUtils.hasText(tagSlug)) {
            tagArticleIds = articleTagMapper.selectArticleIdsByTagSlug(tagSlug);
            if (tagArticleIds.isEmpty()) return new Page<>(page, size);
        }

        final List<Long> finalTagIds = tagArticleIds;
        LambdaQueryWrapper<BlogArticle> wrapper = new LambdaQueryWrapper<BlogArticle>()
                .like(StringUtils.hasText(title), BlogArticle::getTitle, title)
                .eq(status != null, BlogArticle::getStatus, status)
                .eq(categoryId != null, BlogArticle::getCategoryId, categoryId)
                .in(finalTagIds != null, BlogArticle::getId, finalTagIds)
                .orderByDesc(BlogArticle::getUpdatedAt);

        IPage<BlogArticle> articlePage = articleMapper.selectPage(new Page<>(page, size), wrapper);
        return convertPageToListVO(articlePage);
    }

    // ── Admin: 获取详情 ────────────────────────────────────────

    public ArticleDetailVO getDetail(Long id) {
        BlogArticle article = articleMapper.selectById(id);
        if (article == null) throw new BusinessException(ResultCode.NOT_FOUND);
        return buildDetailVO(article);
    }

    // ── Admin: 保存（创建/更新）────────────────────────────────

    @Transactional
    public ArticleDetailVO save(ArticleSaveRequest req) {
        // 生成 slug
        if (!StringUtils.hasText(req.getSlug())) {
            req.setSlug(generateSlug());
        }
        checkSlugUnique(req.getSlug(), null);

        BlogArticle article = new BlogArticle();
        BeanUtil.copyProperties(req, article, "tagIds");
        if (article.getStatus() == 2 && article.getPublishTime() == null) {
            article.setPublishTime(java.time.LocalDateTime.now());
        }
        articleMapper.insert(article);

        saveArticleTags(article.getId(), req.getTagIds());
        recalcCounts(article, null, req.getTagIds(), null);
        return buildDetailVO(article);
    }

    @Transactional
    public ArticleDetailVO update(Long id, ArticleSaveRequest req) {
        BlogArticle old = articleMapper.selectById(id);
        if (old == null) throw new BusinessException(ResultCode.NOT_FOUND);

        if (StringUtils.hasText(req.getSlug())) {
            checkSlugUnique(req.getSlug(), id);
        } else {
            req.setSlug(old.getSlug());
        }

        // 记录旧状态（用于计数刷新）
        Integer oldStatus = old.getStatus();
        Long oldCategoryId = old.getCategoryId();
        Long oldSeriesId = old.getSeriesId();
        List<Long> oldTagIds = articleTagMapper.selectTagsByArticleIds(List.of(id))
                .stream().map(BlogArticleTagMapper.ArticleTagView::getTagId)
                .collect(Collectors.toList());

        BeanUtil.copyProperties(req, old, "tagIds");
        old.setId(id);
        if (old.getStatus() == 2 && old.getPublishTime() == null) {
            old.setPublishTime(java.time.LocalDateTime.now());
        }
        articleMapper.updateById(old);

        // 更新标签关联
        saveArticleTags(id, req.getTagIds());

        // 刷新所有受影响实体的文章计数
        Set<Long> affectedCategories = new HashSet<>();
        if (oldCategoryId != null) affectedCategories.add(oldCategoryId);
        if (old.getCategoryId() != null) affectedCategories.add(old.getCategoryId());
        affectedCategories.forEach(this::recalcCategoryCount);

        Set<Long> affectedSeries = new HashSet<>();
        if (oldSeriesId != null) affectedSeries.add(oldSeriesId);
        if (old.getSeriesId() != null) affectedSeries.add(old.getSeriesId());
        affectedSeries.forEach(this::recalcSeriesCount);

        Set<Long> affectedTags = new HashSet<>();
        affectedTags.addAll(oldTagIds);
        affectedTags.addAll(req.getTagIds());
        affectedTags.forEach(this::recalcTagCount);

        return buildDetailVO(old);
    }

    // ── Admin: 删除 ───────────────────────────────────────────

    @Transactional
    public void delete(Long id) {
        BlogArticle article = articleMapper.selectById(id);
        if (article == null) throw new BusinessException(ResultCode.NOT_FOUND);

        List<Long> tagIds = articleTagMapper.selectTagsByArticleIds(List.of(id))
                .stream().map(BlogArticleTagMapper.ArticleTagView::getTagId)
                .collect(Collectors.toList());

        articleTagMapper.deleteByArticleId(id);
        articleMapper.deleteById(id);

        if (article.getCategoryId() != null) recalcCategoryCount(article.getCategoryId());
        if (article.getSeriesId() != null) recalcSeriesCount(article.getSeriesId());
        tagIds.forEach(this::recalcTagCount);
    }

    // ── 内部辅助 ───────────────────────────────────────────────

    private void saveArticleTags(Long articleId, List<Long> tagIds) {
        articleTagMapper.deleteByArticleId(articleId);
        if (tagIds != null) {
            tagIds.forEach(tagId -> {
                BlogArticleTag at = new BlogArticleTag();
                at.setArticleId(articleId);
                at.setTagId(tagId);
                articleTagMapper.insert(at);
            });
        }
    }

    private void recalcCounts(BlogArticle article, Long oldCategoryId,
                               List<Long> newTagIds, Long oldSeriesId) {
        if (article.getCategoryId() != null) recalcCategoryCount(article.getCategoryId());
        if (oldCategoryId != null && !oldCategoryId.equals(article.getCategoryId()))
            recalcCategoryCount(oldCategoryId);
        if (article.getSeriesId() != null) recalcSeriesCount(article.getSeriesId());
        if (oldSeriesId != null && !oldSeriesId.equals(article.getSeriesId()))
            recalcSeriesCount(oldSeriesId);
        if (newTagIds != null) newTagIds.forEach(this::recalcTagCount);
    }

    private void recalcCategoryCount(Long categoryId) {
        long count = articleMapper.selectCount(new LambdaQueryWrapper<BlogArticle>()
                .eq(BlogArticle::getCategoryId, categoryId)
                .eq(BlogArticle::getStatus, 2));
        categoryMapper.update(null, new LambdaUpdateWrapper<BlogCategory>()
                .set(BlogCategory::getArticleCount, count)
                .eq(BlogCategory::getId, categoryId));
    }

    private void recalcTagCount(Long tagId) {
        int count = articleTagMapper.countPublishedByTagId(tagId);
        tagMapper.update(null, new LambdaUpdateWrapper<BlogTag>()
                .set(BlogTag::getArticleCount, count)
                .eq(BlogTag::getId, tagId));
    }

    private void recalcSeriesCount(Long seriesId) {
        long count = articleMapper.selectCount(new LambdaQueryWrapper<BlogArticle>()
                .eq(BlogArticle::getSeriesId, seriesId)
                .eq(BlogArticle::getStatus, 2));
        seriesMapper.update(null, new LambdaUpdateWrapper<BlogSeries>()
                .set(BlogSeries::getArticleCount, count)
                .eq(BlogSeries::getId, seriesId));
    }

    private void checkSlugUnique(String slug, Long excludeId) {
        LambdaQueryWrapper<BlogArticle> w = new LambdaQueryWrapper<BlogArticle>()
                .eq(BlogArticle::getSlug, slug);
        if (excludeId != null) w.ne(BlogArticle::getId, excludeId);
        if (articleMapper.selectCount(w) > 0) throw new BusinessException(ResultCode.SLUG_EXISTS);
    }

    private String generateSlug() {
        return LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-"
                + RandomUtil.randomString(6).toLowerCase();
    }

    private IPage<ArticleListVO> convertPageToListVO(IPage<BlogArticle> articlePage) {
        if (articlePage.getRecords().isEmpty()) {
            return articlePage.convert(a -> new ArticleListVO());
        }

        // 批量加载分类名称
        Set<Long> categoryIds = articlePage.getRecords().stream()
                .map(BlogArticle::getCategoryId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> categoryNameMap = new HashMap<>();
        if (!categoryIds.isEmpty()) {
            categoryMapper.selectBatchIds(categoryIds)
                    .forEach(c -> categoryNameMap.put(c.getId(), c.getName()));
        }

        // 批量加载标签
        List<Long> articleIds = articlePage.getRecords().stream()
                .map(BlogArticle::getId).collect(Collectors.toList());
        Map<Long, List<TagVO>> tagMap = buildTagMap(articleIds);

        return articlePage.convert(article -> toListVO(article, categoryNameMap, tagMap));
    }

    private ArticleDetailVO buildDetailVO(BlogArticle article) {
        BlogCategory category = article.getCategoryId() != null
                ? categoryMapper.selectById(article.getCategoryId()) : null;

        List<BlogArticleTagMapper.ArticleTagView> tagViews =
                articleTagMapper.selectTagsByArticleIds(List.of(article.getId()));
        List<TagVO> tags = tagViews.stream().map(tv -> {
            TagVO t = new TagVO();
            t.setId(tv.getTagId());
            t.setName(tv.getName());
            t.setSlug(tv.getSlug());
            return t;
        }).collect(Collectors.toList());

        ArticleDetailVO vo = new ArticleDetailVO();
        BeanUtil.copyProperties(article, vo);
        vo.setCategoryName(category != null ? category.getName() : null);
        vo.setTags(tags);
        vo.setTagIds(tags.stream().map(TagVO::getId).collect(Collectors.toList()));
        return vo;
    }

    private ArticleListVO toListVO(BlogArticle article,
                                   Map<Long, String> categoryNameMap,
                                   Map<Long, List<TagVO>> tagMap) {
        ArticleListVO vo = new ArticleListVO();
        BeanUtil.copyProperties(article, vo);
        vo.setCategoryName(categoryNameMap.getOrDefault(article.getCategoryId(), null));
        vo.setTags(tagMap.getOrDefault(article.getId(), Collections.emptyList()));
        return vo;
    }

    private Map<Long, List<TagVO>> buildTagMap(List<Long> articleIds) {
        if (articleIds.isEmpty()) return Collections.emptyMap();
        return articleTagMapper.selectTagsByArticleIds(articleIds).stream()
                .collect(Collectors.groupingBy(
                        BlogArticleTagMapper.ArticleTagView::getArticleId,
                        Collectors.mapping(tv -> {
                            TagVO t = new TagVO();
                            t.setId(tv.getTagId());
                            t.setName(tv.getName());
                            t.setSlug(tv.getSlug());
                            return t;
                        }, Collectors.toList())));
    }
}
