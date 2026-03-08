package com.dearblog.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dearblog.entity.BlogSiteConfig;
import com.dearblog.mapper.BlogSiteConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SiteConfigService {

    private final BlogSiteConfigMapper configMapper;

    public Map<String, String> getAll() {
        List<BlogSiteConfig> list = configMapper.selectList(null);
        return list.stream().collect(Collectors.toMap(
                BlogSiteConfig::getConfigKey,
                BlogSiteConfig::getConfigVal));
    }

    @Transactional
    public Map<String, String> saveAll(Map<String, String> configMap) {
        configMap.forEach((key, value) ->
                configMapper.update(null, new LambdaUpdateWrapper<BlogSiteConfig>()
                        .set(BlogSiteConfig::getConfigVal, value)
                        .eq(BlogSiteConfig::getConfigKey, key)));
        return getAll();
    }
}
