package com.dearblog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dearblog.common.result.Result;
import com.dearblog.dto.request.VisitRecordRequest;
import com.dearblog.dto.response.portal.*;
import com.dearblog.service.PortalService;
import com.dearblog.service.VisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Portal - 公开接口")
@RestController
@RequestMapping("/api/portal")
@RequiredArgsConstructor
public class PortalController {

    private final PortalService portalService;
    private final VisitService visitService;

    // ── 首页 ──────────────────────────────────────────────────

    @Operation(summary = "首页聚合数据")
    @GetMapping("/home")
    public Result<PortalHomeVO> home() {
        return Result.ok(portalService.getHome());
    }

    // ── 文章 ──────────────────────────────────────────────────

    @Operation(summary = "文章列表（支持分类/标签/关键词过滤）")
    @GetMapping("/articles")
    public Result<IPage<PortalArticleListVO>> listArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String categorySlug,
            @RequestParam(required = false) String tagSlug,
            @RequestParam(required = false) String keyword) {
        return Result.ok(portalService.listArticles(page, size, categorySlug, tagSlug, keyword));
    }

    @Operation(summary = "获取文章详情")
    @GetMapping("/articles/{slug}")
    public Result<PortalArticleDetailVO> getArticle(@PathVariable String slug) {
        return Result.ok(portalService.getArticleBySlug(slug));
    }

    // ── 分类 ──────────────────────────────────────────────────

    @Operation(summary = "分类树")
    @GetMapping("/categories")
    public Result<List<PortalCategoryVO>> listCategories() {
        return Result.ok(portalService.listCategories());
    }

    // ── 标签 ──────────────────────────────────────────────────

    @Operation(summary = "标签列表")
    @GetMapping("/tags")
    public Result<List<PortalTagVO>> listTags() {
        return Result.ok(portalService.listTags());
    }

    // ── 系列 ──────────────────────────────────────────────────

    @Operation(summary = "系列列表")
    @GetMapping("/series")
    public Result<List<PortalSeriesVO>> listSeries() {
        return Result.ok(portalService.listSeries());
    }

    @Operation(summary = "系列详情（含文章列表）")
    @GetMapping("/series/{slug}")
    public Result<PortalSeriesVO> getSeries(@PathVariable String slug) {
        return Result.ok(portalService.getSeriesBySlug(slug));
    }

    // ── 归档 ──────────────────────────────────────────────────

    @Operation(summary = "时间归档")
    @GetMapping("/archive")
    public Result<List<PortalArchiveVO>> archive() {
        return Result.ok(portalService.listArchive());
    }

    // ── 访问记录 ───────────────────────────────────────────────

    @Operation(summary = "记录页面访问（由前端 JS 调用）")
    @PostMapping("/visit")
    public Result<Void> recordVisit(@RequestBody VisitRecordRequest req,
                                    HttpServletRequest httpRequest) {
        String ip = resolveClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        visitService.recordVisit(req.getPath(), req.getArticleId(), ip, userAgent, req.getReferer());
        return Result.ok();
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
