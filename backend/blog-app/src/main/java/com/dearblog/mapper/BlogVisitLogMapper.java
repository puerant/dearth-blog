package com.dearblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dearblog.dto.response.VisitTrendVO;
import com.dearblog.entity.BlogVisitLog;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface BlogVisitLogMapper extends BaseMapper<BlogVisitLog> {

    @Select("SELECT COUNT(*) FROM blog_visit_log WHERE visit_date = #{date}")
    long countPvByDate(LocalDate date);

    @Select("SELECT COUNT(DISTINCT ip) FROM blog_visit_log WHERE visit_date = #{date}")
    long countUvByDate(LocalDate date);

    @Select("SELECT COUNT(*) FROM blog_visit_log WHERE visit_date = #{date} AND article_id > 0")
    long countArticlePvByDate(LocalDate date);

    @Select("SELECT COUNT(*) FROM blog_visit_log")
    long countTotalPv();

    @Select("SELECT COUNT(DISTINCT ip) FROM blog_visit_log")
    long countTotalUv();

    @Results({
        @Result(property = "date", column = "date"),
        @Result(property = "pv",   column = "pv"),
        @Result(property = "uv",   column = "uv")
    })
    @Select("SELECT visit_date AS date, COUNT(*) AS pv, COUNT(DISTINCT ip) AS uv " +
            "FROM blog_visit_log WHERE visit_date >= #{from} " +
            "GROUP BY visit_date ORDER BY visit_date")
    List<VisitTrendVO> selectTrendSince(@Param("from") LocalDate from);
}
