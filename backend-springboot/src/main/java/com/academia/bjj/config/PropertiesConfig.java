package com.academia.bjj.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Ativa o binding de {@link AppProperties}. */
@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class PropertiesConfig {
}
