package com.dearblog.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SeriesVO {

    private Long id;

    private String name;

    private String slug;

    private String description;

    private String coverImage;

    private Integer sortOrder;

    private Integer articleCount;

    private LocalDateTime createdAt;
}
