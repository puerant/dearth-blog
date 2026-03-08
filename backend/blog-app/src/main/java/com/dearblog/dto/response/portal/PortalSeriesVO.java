package com.dearblog.dto.response.portal;

import lombok.Data;

import java.util.List;

@Data
public class PortalSeriesVO {

    private Long id;

    private String name;

    private String slug;

    private String description;

    private String coverImage;

    private Integer articleCount;

    private Integer sortOrder;

    /** 系列详情页使用，列表页为 null */
    private List<PortalArticleListVO> articles;
}
