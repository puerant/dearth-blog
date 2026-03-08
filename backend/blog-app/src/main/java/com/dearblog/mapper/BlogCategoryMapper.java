package com.dearblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dearblog.entity.BlogCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogCategoryMapper extends BaseMapper<BlogCategory> {
}
