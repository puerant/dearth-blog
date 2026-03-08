package com.dearblog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dearblog.common.exception.BusinessException;
import com.dearblog.common.result.ResultCode;
import com.dearblog.dto.response.MediaVO;
import com.dearblog.entity.BlogMedia;
import com.dearblog.mapper.BlogMediaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml");

    private final BlogMediaMapper mediaMapper;

    @Value("${blog.upload.path}")
    private String uploadPath;

    @Value("${blog.upload.access-prefix}")
    private String accessPrefix;

    public MediaVO upload(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BusinessException(ResultCode.FILE_TYPE_NOT_ALLOWED);
        }

        String originalName = file.getOriginalFilename();
        String ext = getExtension(originalName);
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String storageName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        String storagePath = dateDir + "/" + storageName;

        Path fullPath = Paths.get(uploadPath, dateDir, storageName);
        try {
            Files.createDirectories(fullPath.getParent());
            file.transferTo(fullPath.toFile());
        } catch (IOException e) {
            throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED);
        }

        BlogMedia media = new BlogMedia();
        media.setOriginalName(originalName);
        media.setStorageName(storageName);
        media.setStoragePath(storagePath);
        media.setAccessUrl(accessPrefix + "/" + storagePath);
        media.setMimeType(contentType);
        media.setFileSize(file.getSize());
        mediaMapper.insert(media);

        return toVO(media);
    }

    public IPage<MediaVO> list(int page, int size) {
        IPage<BlogMedia> mediaPage = mediaMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<BlogMedia>().orderByDesc(BlogMedia::getCreatedAt));
        return mediaPage.convert(this::toVO);
    }

    public void delete(Long id) {
        BlogMedia media = mediaMapper.selectById(id);
        if (media == null) throw new BusinessException(ResultCode.FILE_NOT_FOUND);

        Path fullPath = Paths.get(uploadPath, media.getStoragePath());
        try {
            Files.deleteIfExists(fullPath);
        } catch (IOException ignored) {
        }
        mediaMapper.deleteById(id);
    }

    private MediaVO toVO(BlogMedia media) {
        MediaVO vo = new MediaVO();
        vo.setId(media.getId());
        vo.setOriginalName(media.getOriginalName());
        vo.setAccessUrl(media.getAccessUrl());
        vo.setMimeType(media.getMimeType());
        vo.setFileSize(media.getFileSize());
        vo.setCreatedAt(media.getCreatedAt());
        return vo;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "bin";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
