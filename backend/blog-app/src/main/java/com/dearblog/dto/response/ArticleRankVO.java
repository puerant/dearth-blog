package com.dearblog.dto.response;

import lombok.Data;

@Data
public class ArticleRankVO {

    private Long id;

    private String title;

    private String slug;

    private Integer viewCount;
}
