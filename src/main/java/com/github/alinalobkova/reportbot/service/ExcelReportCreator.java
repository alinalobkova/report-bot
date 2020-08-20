package com.github.alinalobkova.reportbot.service;

import lombok.extern.log4j.Log4j2;
import com.github.alinalobkova.reportbot.entity.Report;
import com.github.alinalobkova.reportbot.utils.FileUtil;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Log4j2
public class ExcelReportCreator extends ReportCreator {
    private ExcelCreator excelCreator;

    private final List<Long> ids;
    private final TelegramSender telegramSender;


    public ExcelReportCreator(Report report, List<Long> ids, TelegramSender telegramSender, DataSource dataSource,
                              ExcelCreator excelCreator) {
        super(report, dataSource);
        this.excelCreator = excelCreator;
        this.ids = ids;
        this.telegramSender = telegramSender;
    }

    /**
     * Создание и отправка файлового отчета
     *
     * @param resultSet - данные
     * @throws IOException  - исключение файла
     * @throws SQLException - исключение базы
     */
    @Override
    void createAndSendReport(ResultSet resultSet) throws IOException, SQLException {
        ByteArrayOutputStream outputStream = createFile(resultSet, getTitle());
        File file = FileUtil.writeToTempFile(outputStream, report.getName());
        for (Long id : ids) {
            telegramSender.sendToTelegram(id, file);
            log.info("send report - " + report.getName() + " to" + id);
        }
    }

    private Optional<String> getTitle() {
        if (report.getTitle() != null) {
            try {
                Optional<String> resultSet = getTitleFromDb();
                if (resultSet.isPresent()) {
                    return resultSet;
                }
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
        return Optional.empty();
    }

    private Optional<String> getTitleFromDb() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(report.getTitle());
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return Optional.of(resultSet.getString(1));
            }
        }
        return Optional.empty();
    }

    /**
     * Формирование Excel файла отчета
     *
     * @param resultSet - данные для отчета
     * @return файл отчета
     * @throws SQLException - исключение из базы
     * @throws IOException  - исключение при создание файла
     */
    private ByteArrayOutputStream createFile(ResultSet resultSet, Optional<String> title) throws SQLException, IOException {
        return excelCreator.createReport(report, resultSet, title);
    }
}
