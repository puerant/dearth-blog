package com.dearblog.dto.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CategoryVO {

    private Long id;

    private String name;

    private String slug;

    private String description;

    private Long parentId;

    private Integer sortOrder;

    private Integer articleCount;

    private List<CategoryVO> children = new ArrayList<>();
}
