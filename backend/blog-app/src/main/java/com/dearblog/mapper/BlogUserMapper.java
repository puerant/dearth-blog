package com.dearblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dearblog.entity.BlogUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 博主账号 Mapper
 * 基础 CRUD 由 MyBatis Plus BaseMapper 提供
 */
@Mapper
public interface BlogUserMapper extends BaseMapper<BlogUser> {
}
