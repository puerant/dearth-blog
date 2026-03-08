package com.dearblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dearblog.entity.BlogArticleTag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BlogArticleTagMapper extends BaseMapper<BlogArticleTag> {

    /**
     * 批量查询多篇文章的标签信息
     * 返回结果包含 articleId 和 tagId，供服务层 Map 化
     */
    @Select("<script>" +
            "SELECT at.article_id, t.id as tag_id, t.name, t.slug FROM blog_article_tag at " +
            "JOIN blog_tag t ON at.tag_id = t.id " +
            "WHERE at.article_id IN " +
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    List<ArticleTagView> selectTagsByArticleIds(@Param("ids") List<Long> ids);

    /** 删除某篇文章的所有标签关联 */
    @Delete("DELETE FROM blog_article_tag WHERE article_id = #{articleId}")
    int deleteByArticleId(@Param("articleId") Long articleId);

    /** 统计某标签下已发布文章数 */
    @Select("SELECT COUNT(*) FROM blog_article_tag at " +
            "JOIN blog_article a ON at.article_id = a.id " +
            "WHERE at.tag_id = #{tagId} AND a.status = 2")
    int countPublishedByTagId(@Param("tagId") Long tagId);

    /** 查询含某标签的所有文章 ID（不限状态，用于 Admin 筛选）*/
    @Select("SELECT at.article_id FROM blog_article_tag at " +
            "JOIN blog_tag t ON at.tag_id = t.id WHERE t.slug = #{tagSlug}")
    List<Long> selectArticleIdsByTagSlug(@Param("tagSlug") String tagSlug);

    @lombok.Data
    class ArticleTagView {
        private Long articleId;
        private Long tagId;
        private String name;
        private String slug;
    }
}
