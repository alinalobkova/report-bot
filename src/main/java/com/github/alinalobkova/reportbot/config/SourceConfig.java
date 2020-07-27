package com.github.alinalobkova.reportbot.config;

import com.zaxxer.hikari.HikariConfig;
import lombok.*;
import com.github.alinalobkova.reportbot.enums.DB;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString
public class SourceConfig extends HikariConfig {

    /**
     * Name of the connection pool.
     */
    @Getter
    @Setter
    private String name = "";

    /**
     * Type of DB
     */
    @Getter
    private DB db;

    /**
     * Datasource URL
     */
    @Getter
    private String url = "";

    public void setDb(DB db) {
        this.db = db;
        this.setDriverClassName(db.getDriverClassName());
    }

    public void setUrl(String url) {
        this.url = url;
        this.setJdbcUrl(url);
    }
}
