package com.dearblog.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dearblog.common.exception.BusinessException;
import com.dearblog.common.result.ResultCode;
import com.dearblog.dto.request.TagSaveRequest;
import com.dearblog.dto.response.TagVO;
import com.dearblog.entity.BlogTag;
import com.dearblog.mapper.BlogTagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final BlogTagMapper tagMapper;

    public List<TagVO> listAll() {
        return tagMapper.selectList(null).stream().map(this::toVO).collect(Collectors.toList());
    }

    public TagVO save(TagSaveRequest req) {
        checkSlugUnique(req.getSlug(), null);
        BlogTag entity = new BlogTag();
        BeanUtil.copyProperties(req, entity);
        tagMapper.insert(entity);
        return toVO(entity);
    }

    public TagVO update(Long id, TagSaveRequest req) {
        BlogTag entity = getOrFail(id);
        checkSlugUnique(req.getSlug(), id);
        BeanUtil.copyProperties(req, entity);
        tagMapper.updateById(entity);
        return toVO(entity);
    }

    public void delete(Long id) {
        getOrFail(id);
        tagMapper.deleteById(id);
    }

    private BlogTag getOrFail(Long id) {
        BlogTag entity = tagMapper.selectById(id);
        if (entity == null) throw new BusinessException(ResultCode.NOT_FOUND);
        return entity;
    }

    private void checkSlugUnique(String slug, Long excludeId) {
        LambdaQueryWrapper<BlogTag> w = new LambdaQueryWrapper<BlogTag>()
                .eq(BlogTag::getSlug, slug);
        if (excludeId != null) w.ne(BlogTag::getId, excludeId);
        if (tagMapper.selectCount(w) > 0) throw new BusinessException(ResultCode.SLUG_EXISTS);
    }

    public TagVO toVO(BlogTag entity) {
        TagVO vo = new TagVO();
        BeanUtil.copyProperties(entity, vo);
        return vo;
    }
}
