package com.dearblog.dto.response;

import lombok.Data;

@Data
public class VisitSummaryVO {

    private Long totalPv;

    private Long totalUv;

    private Long todayPv;

    private Long todayUv;
}
