package com.dearblog.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VisitTrendVO {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private Long pv;

    private Long uv;
}
