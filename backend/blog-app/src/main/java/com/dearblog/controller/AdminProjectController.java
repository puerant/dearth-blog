package com.dearblog.controller;

import com.dearblog.common.result.Result;
import com.dearblog.dto.request.ProjectSaveRequest;
import com.dearblog.dto.response.ProjectVO;
import com.dearblog.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "管理 - 项目")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/admin/project")
@RequiredArgsConstructor
public class AdminProjectController {

    private final ProjectService projectService;

    @Operation(summary = "获取所有项目")
    @GetMapping
    public Result<List<ProjectVO>> listAll() {
        return Result.ok(projectService.listAll());
    }

    @Operation(summary = "创建项目")
    @PostMapping
    public Result<ProjectVO> save(@Valid @RequestBody ProjectSaveRequest req) {
        return Result.ok(projectService.save(req));
    }

    @Operation(summary = "更新项目")
    @PutMapping("/{id}")
    public Result<ProjectVO> update(@PathVariable Long id,
                                    @Valid @RequestBody ProjectSaveRequest req) {
        return Result.ok(projectService.update(id, req));
    }

    @Operation(summary = "删除项目")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return Result.ok();
    }
}
