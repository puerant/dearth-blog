package com.dearblog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SeriesSaveRequest {

    @NotBlank(message = "系列名称不能为空")
    private String name;

    @NotBlank(message = "Slug 不能为空")
    private String slug;

    private String description = "";

    private String coverImage;

    private Integer sortOrder = 0;
}
