package com.github.alinalobkova.reportbot.service;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import com.github.alinalobkova.reportbot.config.DataSourceProperties;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
public class DataSourceService {

    private Map<String, DataSource> dataSourceMap;

    public DataSourceService(DataSourceProperties dataSourceProperties) {
        dataSourceMap = new HashMap<>();
        dataSourceProperties.getSources().forEach(source -> {
                log.info("Create dataSource " + source);
                DataSource dataSource = new HikariDataSource(source);
                dataSourceMap.put(source.getName(), dataSource);
            }
        );
    }

    public Optional<DataSource> getSource(String source) {
        return Optional.of(dataSourceMap.get(source));
    }
}
