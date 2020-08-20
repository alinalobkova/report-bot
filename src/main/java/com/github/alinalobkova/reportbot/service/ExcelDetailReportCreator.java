package com.github.alinalobkova.reportbot.service;

import lombok.extern.log4j.Log4j2;
import com.github.alinalobkova.reportbot.entity.Report;
import com.github.alinalobkova.reportbot.utils.FileUtil;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Log4j2
public class ExcelDetailReportCreator extends ReportCreator {

    private final List<Long> ids;
    private final TelegramSender telegramSender;

    public ExcelDetailReportCreator(Report report, List<Long> ids, TelegramSender telegramSender, DataSource dataSource,
                                    ExcelCreator excelCreator) {
        super(report, dataSource);
        this.excelCreator = excelCreator;
        this.telegramSender = telegramSender;
        this.ids = ids;

    }

    private ExcelCreator excelCreator;

    /**
     * Создание и отправка файлового отчета детализации по дням
     *
     * @param resultSet - данные
     * @throws IOException  - исключение файла
     * @throws SQLException - исключение базы
     */
    @Override
    void createAndSendReport(ResultSet resultSet) throws IOException, SQLException {
        ByteArrayOutputStream outputStream = createDetailFile(resultSet);
        File file = FileUtil.writeToTempFile(outputStream, report.getName());
        for (Long id : ids) {
            telegramSender.sendToTelegram(id, file);
            log.info("send report - " + report.getName() + " to" + id);
        }
    }


    /**
     * Формирование Excel файла отчета
     *
     * @param resultSet - данные для отчета
     * @return файл отчета
     * @throws SQLException - исключение из базы
     * @throws IOException  - исключение при создание файла
     */
    private ByteArrayOutputStream createDetailFile(ResultSet resultSet) throws SQLException, IOException {
        return excelCreator.createDetailReport(report, resultSet);
    }

}
