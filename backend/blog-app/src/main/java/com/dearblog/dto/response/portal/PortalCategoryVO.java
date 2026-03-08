package com.dearblog.dto.response.portal;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PortalCategoryVO {

    private Long id;

    private String name;

    private String slug;

    private String description;

    private Long parentId;

    private Integer articleCount;

    private List<PortalCategoryVO> children = new ArrayList<>();
}
