package com.dearblog.controller;

import com.dearblog.common.result.Result;
import com.dearblog.service.SiteConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "管理 - 站点配置")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/admin/config")
@RequiredArgsConstructor
public class AdminConfigController {

    private final SiteConfigService configService;

    @Operation(summary = "获取所有站点配置")
    @GetMapping
    public Result<Map<String, String>> getAll() {
        return Result.ok(configService.getAll());
    }

    @Operation(summary = "批量更新站点配置")
    @PutMapping
    public Result<Map<String, String>> saveAll(@RequestBody Map<String, String> configMap) {
        return Result.ok(configService.saveAll(configMap));
    }
}
