package com.dearblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dearblog.entity.BlogArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BlogArticleMapper extends BaseMapper<BlogArticle> {

    /**
     * 查询某系列下已发布的文章（按 series_sort 排序）
     */
    @Select("SELECT id, title, slug, publish_time, series_sort FROM blog_article " +
            "WHERE series_id = #{seriesId} AND status = 2 AND publish_time <= NOW() " +
            "ORDER BY series_sort ASC")
    List<BlogArticle> selectPublishedBySeriesId(@Param("seriesId") Long seriesId);

    /**
     * 查询某标签下已发布的文章 ID 列表
     */
    @Select("SELECT a.id FROM blog_article a " +
            "JOIN blog_article_tag at ON a.id = at.article_id " +
            "JOIN blog_tag t ON at.tag_id = t.id " +
            "WHERE t.slug = #{tagSlug} AND a.status = 2 AND a.publish_time <= NOW()")
    List<Long> selectPublishedIdsByTagSlug(@Param("tagSlug") String tagSlug);

    /**
     * 查询某分类（含其所有子分类）下已发布文章 ID 列表
     */
    @Select("<script>SELECT id FROM blog_article WHERE status = 2 AND publish_time &lt;= NOW() " +
            "AND category_id IN <foreach item='id' collection='ids' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    List<Long> selectPublishedIdsByCategoryIds(@Param("ids") List<Long> ids);
}
