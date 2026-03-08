package com.dearblog.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MediaVO {

    private Long id;

    private String originalName;

    private String accessUrl;

    private String mimeType;

    private Long fileSize;

    private LocalDateTime createdAt;
}
