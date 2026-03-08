package com.dearblog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dearblog.common.result.Result;
import com.dearblog.dto.response.MediaVO;
import com.dearblog.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "管理 - 媒体")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/admin/media")
@RequiredArgsConstructor
public class AdminMediaController {

    private final MediaService mediaService;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<MediaVO> upload(@RequestParam("file") MultipartFile file) {
        return Result.ok(mediaService.upload(file));
    }

    @Operation(summary = "媒体文件分页列表")
    @GetMapping
    public Result<IPage<MediaVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(mediaService.list(page, size));
    }

    @Operation(summary = "删除媒体文件")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        mediaService.delete(id);
        return Result.ok();
    }
}
