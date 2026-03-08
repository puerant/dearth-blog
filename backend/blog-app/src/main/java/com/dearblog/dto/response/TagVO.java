package com.dearblog.dto.response;

import lombok.Data;

@Data
public class TagVO {

    private Long id;

    private String name;

    private String slug;

    private String description;

    private Integer articleCount;
}
