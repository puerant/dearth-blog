package com.dearblog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategorySaveRequest {

    @NotBlank(message = "分类名称不能为空")
    private String name;

    @NotBlank(message = "Slug 不能为空")
    private String slug;

    private String description = "";

    /** 父分类 ID，0 表示根分类 */
    private Long parentId = 0L;

    private Integer sortOrder = 0;
}
