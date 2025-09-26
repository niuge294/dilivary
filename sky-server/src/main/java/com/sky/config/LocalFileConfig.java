package com.sky.config;

import com.sky.utils.LocalFileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 本地文件工具类的配置类，用于创建Spring管理的Bean
 */
@Configuration
public class LocalFileConfig {

    @Value("${file.local.base-path}")
    private String basePath;

    @Value("${file.local.base-url}")
    private String baseUrl;

    @Bean
    public LocalFileUtil localFileUtil() {
        return new LocalFileUtil(basePath, baseUrl);
    }
}
