package com.dearblog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TagSaveRequest {

    @NotBlank(message = "标签名称不能为空")
    private String name;

    @NotBlank(message = "Slug 不能为空")
    private String slug;

    private String description = "";
}
