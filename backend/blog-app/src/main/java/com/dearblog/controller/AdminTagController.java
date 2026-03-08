package com.dearblog.controller;

import com.dearblog.common.result.Result;
import com.dearblog.dto.request.TagSaveRequest;
import com.dearblog.dto.response.TagVO;
import com.dearblog.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "管理 - 标签")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/admin/tag")
@RequiredArgsConstructor
public class AdminTagController {

    private final TagService tagService;

    @Operation(summary = "获取所有标签")
    @GetMapping
    public Result<List<TagVO>> listAll() {
        return Result.ok(tagService.listAll());
    }

    @Operation(summary = "创建标签")
    @PostMapping
    public Result<TagVO> save(@Valid @RequestBody TagSaveRequest req) {
        return Result.ok(tagService.save(req));
    }

    @Operation(summary = "更新标签")
    @PutMapping("/{id}")
    public Result<TagVO> update(@PathVariable Long id, @Valid @RequestBody TagSaveRequest req) {
        return Result.ok(tagService.update(id, req));
    }

    @Operation(summary = "删除标签")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return Result.ok();
    }
}
