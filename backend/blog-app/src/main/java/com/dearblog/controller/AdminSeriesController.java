package com.dearblog.controller;

import com.dearblog.common.result.Result;
import com.dearblog.dto.request.SeriesSaveRequest;
import com.dearblog.dto.response.SeriesVO;
import com.dearblog.service.SeriesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "管理 - 系列")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/admin/series")
@RequiredArgsConstructor
public class AdminSeriesController {

    private final SeriesService seriesService;

    @Operation(summary = "获取所有系列")
    @GetMapping
    public Result<List<SeriesVO>> listAll() {
        return Result.ok(seriesService.listAll());
    }

    @Operation(summary = "创建系列")
    @PostMapping
    public Result<SeriesVO> save(@Valid @RequestBody SeriesSaveRequest req) {
        return Result.ok(seriesService.save(req));
    }

    @Operation(summary = "更新系列")
    @PutMapping("/{id}")
    public Result<SeriesVO> update(@PathVariable Long id,
                                   @Valid @RequestBody SeriesSaveRequest req) {
        return Result.ok(seriesService.update(id, req));
    }

    @Operation(summary = "删除系列")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        seriesService.delete(id);
        return Result.ok();
    }
}
