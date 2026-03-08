package com.dearblog.dto.response.portal;

import lombok.Data;

@Data
public class PortalTagVO {

    private Long id;

    private String name;

    private String slug;

    private Integer articleCount;
}
