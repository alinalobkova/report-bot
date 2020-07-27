package com.github.alinalobkova.reportbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(value = "reportbot.datasource")
@Data
public class DataSourceProperties {

    private List<SourceConfig> sources = new ArrayList<>();
}

