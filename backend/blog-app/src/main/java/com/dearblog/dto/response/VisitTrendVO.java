package com.dearblog.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VisitTrendVO {

    private LocalDate date;

    private Long pv;

    private Long uv;
}
