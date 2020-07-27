package com.github.alinalobkova.reportbot;

import org.junit.jupiter.api.Test;
import com.github.alinalobkova.reportbot.config.DataSourceProperties;
import com.github.alinalobkova.reportbot.config.SourceConfig;
import com.github.alinalobkova.reportbot.service.DataSourceService;

import java.sql.SQLException;
import java.util.Collections;

import static com.github.alinalobkova.reportbot.RandomUtils.generateString;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataSourceServiceTest {

    private DataSourceService dataSourceService;


    @Test
    public void shouldSaveDataSources() throws SQLException {
        DataSourceProperties dataSourceProperties = new DataSourceProperties();

        SourceConfig sourceConfig = new SourceConfig();
        String name = generateString();
        sourceConfig.setName(name);
        sourceConfig.setUrl("jdbc:h2:mem:db");
        dataSourceProperties.setSources(Collections.singletonList(sourceConfig));

        dataSourceService = new DataSourceService(dataSourceProperties);

        assertEquals(dataSourceService.getSource(name).get().getConnection().getMetaData().getURL(), sourceConfig.getUrl());

    }
}
