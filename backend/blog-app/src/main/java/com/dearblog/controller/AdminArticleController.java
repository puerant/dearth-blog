package com.dearblog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dearblog.common.result.Result;
import com.dearblog.dto.request.ArticleSaveRequest;
import com.dearblog.dto.response.ArticleDetailVO;
import com.dearblog.dto.response.ArticleListVO;
import com.dearblog.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理 - 文章")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/admin/article")
@RequiredArgsConstructor
public class AdminArticleController {

    private final ArticleService articleService;

    @Operation(summary = "文章分页列表")
    @GetMapping
    public Result<IPage<ArticleListVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String tagSlug) {
        return Result.ok(articleService.adminList(page, size, title, status, categoryId, tagSlug));
    }

    @Operation(summary = "获取文章详情")
    @GetMapping("/{id}")
    public Result<ArticleDetailVO> getDetail(@PathVariable Long id) {
        return Result.ok(articleService.getDetail(id));
    }

    @Operation(summary = "创建文章")
    @PostMapping
    public Result<ArticleDetailVO> save(@Valid @RequestBody ArticleSaveRequest req) {
        return Result.ok(articleService.save(req));
    }

    @Operation(summary = "更新文章")
    @PutMapping("/{id}")
    public Result<ArticleDetailVO> update(@PathVariable Long id,
                                          @Valid @RequestBody ArticleSaveRequest req) {
        return Result.ok(articleService.update(id, req));
    }

    @Operation(summary = "更新文章状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        articleService.updateStatus(id, body.get("status"));
        return Result.ok();
    }

    @Operation(summary = "删除文章")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return Result.ok();
    }
}
