package com.dearblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dearblog.entity.BlogVisitStat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface BlogVisitStatMapper extends BaseMapper<BlogVisitStat> {

    @Select("SELECT COALESCE(SUM(pv), 0) FROM blog_visit_stat")
    long sumTotalPv();

    @Select("SELECT COALESCE(SUM(uv), 0) FROM blog_visit_stat")
    long sumTotalUv();

    @Select("SELECT * FROM blog_visit_stat WHERE stat_date >= #{from} ORDER BY stat_date ASC")
    List<BlogVisitStat> selectSince(@Param("from") LocalDate from);
}
