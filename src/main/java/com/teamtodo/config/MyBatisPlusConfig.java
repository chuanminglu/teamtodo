package com.teamtodo.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus Configuration
 * Only active when not using test profile
 */
@Configuration
@MapperScan("com.teamtodo.mapper")
@ConditionalOnProperty(name = "mybatis-plus.enabled", havingValue = "true", matchIfMissing = true)
public class MyBatisPlusConfig {
}
