package com.github.alinalobkova.reportbot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DB {
    MYSQL("com.mysql.jdbc.Driver", "org.hibernate.dialect.MySQLDialect"),
    ORACLE("oracle.jdbc.driver.OracleDriver", "org.hibernate.dialect.OracleDialect"),
    POSTGRESQL("org.postgresql.Driver", "org.hibernate.dialect.ProgressDialect"),
    CLICKHOUSE("ru.yandex.clickhouse.ClickHouseDriver", "com.github.alinalobkova.reportbot.utils.ClickHouseDialect"),
    H2("org.h2.Driver", "org.hibernate.dialect.H2Dialect");

    private String driverClassName;
    private String hibernateDialect;

}
