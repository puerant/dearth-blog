package com.dearblog.controller;

import com.dearblog.common.result.Result;
import com.dearblog.dto.response.ArticleRankVO;
import com.dearblog.dto.response.VisitSummaryVO;
import com.dearblog.dto.response.VisitTrendVO;
import com.dearblog.service.VisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "管理 - 访问统计")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/admin/visit")
@RequiredArgsConstructor
public class AdminVisitController {

    private final VisitService visitService;

    @Operation(summary = "访问统计摘要（总 PV/UV、今日 PV/UV）")
    @GetMapping("/summary")
    public Result<VisitSummaryVO> summary() {
        return Result.ok(visitService.getSummary());
    }

    @Operation(summary = "访问趋势（最近 N 天）")
    @GetMapping("/trend")
    public Result<List<VisitTrendVO>> trend(
            @RequestParam(defaultValue = "30") int days) {
        return Result.ok(visitService.getTrend(days));
    }

    @Operation(summary = "文章浏览量排行")
    @GetMapping("/article-rank")
    public Result<List<ArticleRankVO>> articleRank(
            @RequestParam(defaultValue = "10") int limit) {
        return Result.ok(visitService.getArticleRank(limit));
    }
}
