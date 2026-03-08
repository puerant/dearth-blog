package com.dearblog.dto.response.portal;

import com.dearblog.dto.response.ProjectVO;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Portal 首页数据 VO
 */
@Data
public class PortalHomeVO {

    private List<PortalArticleListVO> featuredArticles;

    private List<PortalArticleListVO> recentArticles;

    /** 站点配置键值对 */
    private Map<String, String> siteConfig;

    private Long totalArticles;

    private Long totalCategories;

    private Long totalTags;

    private Long totalSeries;

    /** 项目展示列表 */
    private List<ProjectVO> projects;

    // 以下字段保留（部分页面可能复用）
    private List<PortalCategoryVO> categories;

    private List<PortalTagVO> tags;

    private List<PortalSeriesVO> recentSeries;
}
