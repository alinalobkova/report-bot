package com.github.alinalobkova.reportbot.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import com.github.alinalobkova.reportbot.entity.Report;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Создатель отчетов, запускаемый по расписанию для каждого отчета
 */
@AllArgsConstructor
@Log4j2
public abstract class ReportCreator implements Runnable {

    protected final Report report;
    protected final DataSource dataSource;


    /**
     * Запуск формирования отчета
     * запускается в отдельном потоке по расписанию
     */
    @Override
    public void run() {
        log.info("Start report - " + report.getName());
        try {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(report.getSql());
                 ResultSet resultSet = statement.executeQuery()) {
                createAndSendReport(resultSet);
            }
        } catch (SQLException | IOException e) {
            log.error(e.getMessage(), e);
        }

    }

    abstract void createAndSendReport(ResultSet resultSet) throws IOException, SQLException;
}
