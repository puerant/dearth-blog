package com.dearblog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectSaveRequest {

    @NotBlank(message = "项目编号不能为空")
    private String projectNo;

    @NotBlank(message = "项目名称不能为空")
    private String name;

    @NotBlank(message = "项目简称不能为空")
    private String shortName;

    private String description = "";

    private List<String> techStack = new ArrayList<>();

    private List<String> highlights = new ArrayList<>();

    private String startYear;

    /** Active / Archive / WIP */
    private String status = "WIP";

    /** 主题色 HSL Hue 值 0-360 */
    private Integer hue = 0;

    private String githubUrl = "";

    private Integer sortOrder = 0;
}
