package com.dearblog.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectVO {

    private Long id;

    private String projectNo;

    private String name;

    private String shortName;

    private String description;

    private List<String> techStack;

    private List<String> highlights;

    private String startYear;

    /** Active / Archive / WIP */
    private String status;

    private Integer hue;

    private String githubUrl;

    private Integer sortOrder;

    private LocalDateTime createdAt;
}
