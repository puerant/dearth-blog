package com.dearblog.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dearblog.common.exception.BusinessException;
import com.dearblog.common.result.ResultCode;
import com.dearblog.dto.request.ProjectSaveRequest;
import com.dearblog.dto.response.ProjectVO;
import com.dearblog.entity.BlogProject;
import com.dearblog.mapper.BlogProjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final BlogProjectMapper projectMapper;
    private final ObjectMapper objectMapper;

    public List<ProjectVO> listAll() {
        return projectMapper.selectList(
                new LambdaQueryWrapper<BlogProject>().orderByAsc(BlogProject::getSortOrder))
                .stream().map(this::toVO).collect(Collectors.toList());
    }

    public ProjectVO save(ProjectSaveRequest req) {
        BlogProject entity = toEntity(req);
        projectMapper.insert(entity);
        return toVO(entity);
    }

    public ProjectVO update(Long id, ProjectSaveRequest req) {
        BlogProject entity = projectMapper.selectById(id);
        if (entity == null) throw new BusinessException(ResultCode.NOT_FOUND);
        BeanUtil.copyProperties(toEntity(req), entity, "id");
        entity.setId(id);
        projectMapper.updateById(entity);
        return toVO(entity);
    }

    public void delete(Long id) {
        if (projectMapper.selectById(id) == null) throw new BusinessException(ResultCode.NOT_FOUND);
        projectMapper.deleteById(id);
    }

    private BlogProject toEntity(ProjectSaveRequest req) {
        BlogProject entity = new BlogProject();
        BeanUtil.copyProperties(req, entity, "techStack", "highlights");
        entity.setTechStack(toJson(req.getTechStack()));
        entity.setHighlights(toJson(req.getHighlights()));
        return entity;
    }

    private ProjectVO toVO(BlogProject entity) {
        ProjectVO vo = new ProjectVO();
        BeanUtil.copyProperties(entity, vo, "techStack", "highlights");
        vo.setTechStack(fromJson(entity.getTechStack()));
        vo.setHighlights(fromJson(entity.getHighlights()));
        return vo;
    }

    private String toJson(List<String> list) {
        try {
            return objectMapper.writeValueAsString(list == null ? new ArrayList<>() : list);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private List<String> fromJson(String json) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }
}
