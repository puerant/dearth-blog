package com.dearblog.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dearblog.common.exception.BusinessException;
import com.dearblog.common.result.ResultCode;
import com.dearblog.dto.request.SeriesSaveRequest;
import com.dearblog.dto.response.SeriesVO;
import com.dearblog.entity.BlogSeries;
import com.dearblog.mapper.BlogSeriesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeriesService {

    private final BlogSeriesMapper seriesMapper;

    public List<SeriesVO> listAll() {
        return seriesMapper.selectList(
                new LambdaQueryWrapper<BlogSeries>().orderByAsc(BlogSeries::getSortOrder))
                .stream().map(this::toVO).collect(Collectors.toList());
    }

    public SeriesVO save(SeriesSaveRequest req) {
        checkSlugUnique(req.getSlug(), null);
        BlogSeries entity = new BlogSeries();
        BeanUtil.copyProperties(req, entity);
        seriesMapper.insert(entity);
        return toVO(entity);
    }

    public SeriesVO update(Long id, SeriesSaveRequest req) {
        BlogSeries entity = getOrFail(id);
        checkSlugUnique(req.getSlug(), id);
        BeanUtil.copyProperties(req, entity);
        seriesMapper.updateById(entity);
        return toVO(entity);
    }

    public void delete(Long id) {
        getOrFail(id);
        seriesMapper.deleteById(id);
    }

    private BlogSeries getOrFail(Long id) {
        BlogSeries entity = seriesMapper.selectById(id);
        if (entity == null) throw new BusinessException(ResultCode.NOT_FOUND);
        return entity;
    }

    private void checkSlugUnique(String slug, Long excludeId) {
        LambdaQueryWrapper<BlogSeries> w = new LambdaQueryWrapper<BlogSeries>()
                .eq(BlogSeries::getSlug, slug);
        if (excludeId != null) w.ne(BlogSeries::getId, excludeId);
        if (seriesMapper.selectCount(w) > 0) throw new BusinessException(ResultCode.SLUG_EXISTS);
    }

    public SeriesVO toVO(BlogSeries entity) {
        SeriesVO vo = new SeriesVO();
        BeanUtil.copyProperties(entity, vo);
        return vo;
    }
}
