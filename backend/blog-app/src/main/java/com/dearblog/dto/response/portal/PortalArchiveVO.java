package com.dearblog.dto.response.portal;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Portal 时间归档 VO
 */
@Data
public class PortalArchiveVO {

    private Integer year;

    private List<MonthEntry> months;

    @Data
    public static class MonthEntry {
        private Integer month;
        private List<ArticleLinkVO> articles;
    }

    @Data
    public static class ArticleLinkVO {
        private Long id;
        private String title;
        private String slug;
        private LocalDateTime publishTime;
        private String categoryName;
    }
}
