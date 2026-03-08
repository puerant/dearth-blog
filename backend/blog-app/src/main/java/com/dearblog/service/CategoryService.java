package com.dearblog.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dearblog.common.exception.BusinessException;
import com.dearblog.common.result.ResultCode;
import com.dearblog.dto.request.CategorySaveRequest;
import com.dearblog.dto.response.CategoryVO;
import com.dearblog.entity.BlogArticle;
import com.dearblog.entity.BlogCategory;
import com.dearblog.mapper.BlogArticleMapper;
import com.dearblog.mapper.BlogCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final BlogCategoryMapper categoryMapper;
    private final BlogArticleMapper articleMapper;

    public List<CategoryVO> listTree() {
        List<BlogCategory> all = categoryMapper.selectList(
                new LambdaQueryWrapper<BlogCategory>().orderByAsc(BlogCategory::getSortOrder));
        return buildTree(all);
    }

    public CategoryVO save(CategorySaveRequest req) {
        checkSlugUnique(req.getSlug(), null);
        BlogCategory entity = new BlogCategory();
        BeanUtil.copyProperties(req, entity);
        categoryMapper.insert(entity);
        return toVO(entity);
    }

    public CategoryVO update(Long id, CategorySaveRequest req) {
        BlogCategory entity = getOrFail(id);
        checkSlugUnique(req.getSlug(), id);
        BeanUtil.copyProperties(req, entity);
        categoryMapper.updateById(entity);
        return toVO(entity);
    }

    public void delete(Long id) {
        long childCount = categoryMapper.selectCount(new LambdaQueryWrapper<BlogCategory>()
                .eq(BlogCategory::getParentId, id));
        if (childCount > 0) throw new BusinessException(ResultCode.CATEGORY_HAS_CHILDREN);

        long articleCount = articleMapper.selectCount(new LambdaQueryWrapper<BlogArticle>()
                .eq(BlogArticle::getCategoryId, id));
        if (articleCount > 0) throw new BusinessException(ResultCode.CATEGORY_HAS_ARTICLES);

        categoryMapper.deleteById(id);
    }

    /** 收集某分类及其所有后代分类的 ID（用于 Portal 文章过滤）*/
    public List<Long> collectCategoryIds(Long rootId) {
        List<BlogCategory> all = categoryMapper.selectList(null);
        List<Long> result = new ArrayList<>();
        collectIds(rootId, all, result);
        return result;
    }

    private void collectIds(Long parentId, List<BlogCategory> all, List<Long> result) {
        result.add(parentId);
        for (BlogCategory c : all) {
            if (parentId.equals(c.getParentId())) {
                collectIds(c.getId(), all, result);
            }
        }
    }

    private BlogCategory getOrFail(Long id) {
        BlogCategory entity = categoryMapper.selectById(id);
        if (entity == null) throw new BusinessException(ResultCode.NOT_FOUND);
        return entity;
    }

    private void checkSlugUnique(String slug, Long excludeId) {
        LambdaQueryWrapper<BlogCategory> w = new LambdaQueryWrapper<BlogCategory>()
                .eq(BlogCategory::getSlug, slug);
        if (excludeId != null) w.ne(BlogCategory::getId, excludeId);
        if (categoryMapper.selectCount(w) > 0) throw new BusinessException(ResultCode.SLUG_EXISTS);
    }

    private List<CategoryVO> buildTree(List<BlogCategory> all) {
        List<CategoryVO> allVOs = all.stream().map(this::toVO).collect(Collectors.toList());
        Map<Long, CategoryVO> map = allVOs.stream()
                .collect(Collectors.toMap(CategoryVO::getId, v -> v));
        List<CategoryVO> roots = new ArrayList<>();
        for (CategoryVO vo : allVOs) {
            if (vo.getParentId() == null || vo.getParentId() == 0) {
                roots.add(vo);
            } else {
                CategoryVO parent = map.get(vo.getParentId());
                if (parent != null) parent.getChildren().add(vo);
            }
        }
        return roots;
    }

    public CategoryVO toVO(BlogCategory entity) {
        CategoryVO vo = new CategoryVO();
        BeanUtil.copyProperties(entity, vo);
        return vo;
    }
}
