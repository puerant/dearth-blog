package com.dearblog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${blog.upload.path}")
    private String uploadPath;

    @Value("${blog.upload.access-prefix}")
    private String accessPrefix;

    /**
     * 将本地上传目录映射为静态资源路径
     * 开发环境直接由 Spring 提供访问，生产环境由 Nginx 接管
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(accessPrefix + "/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
