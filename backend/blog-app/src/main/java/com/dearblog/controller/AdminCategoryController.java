package com.dearblog.controller;

import com.dearblog.common.result.Result;
import com.dearblog.dto.request.CategorySaveRequest;
import com.dearblog.dto.response.CategoryVO;
import com.dearblog.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "管理 - 分类")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "获取分类树")
    @GetMapping
    public Result<List<CategoryVO>> listTree() {
        return Result.ok(categoryService.listTree());
    }

    @Operation(summary = "创建分类")
    @PostMapping
    public Result<CategoryVO> save(@Valid @RequestBody CategorySaveRequest req) {
        return Result.ok(categoryService.save(req));
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    public Result<CategoryVO> update(@PathVariable Long id,
                                     @Valid @RequestBody CategorySaveRequest req) {
        return Result.ok(categoryService.update(id, req));
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.ok();
    }
}
