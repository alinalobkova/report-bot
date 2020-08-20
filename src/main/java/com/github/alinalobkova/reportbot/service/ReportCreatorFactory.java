package com.github.alinalobkova.reportbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import com.github.alinalobkova.reportbot.entity.Report;
import com.github.alinalobkova.reportbot.exceptions.DataSourceNotFound;

import javax.sql.DataSource;
import java.util.List;

import static com.github.alinalobkova.reportbot.enums.ReportType.*;

@DependsOn("dataSourceService")
@Service
@RequiredArgsConstructor
public class ReportCreatorFactory {
    private final TelegramSender telegramSender;
    private final DataSourceService sourceService;
    private final ExcelCreator excelCreator;

    public ReportCreator getReportCreator(Report report, List<Long> telegramIds) {
        switch (report.getType()) {
            case EXCEL:
                return new ExcelReportCreator(report, telegramIds, telegramSender, getDataSource(report.getSource()),
                    excelCreator);
            case EXCEL_DETAIL:
                return new ExcelDetailReportCreator(report, telegramIds, telegramSender,
                    getDataSource(report.getSource()),
                    excelCreator);
            case STRING:
                return new StringReportCreator(report, telegramIds, telegramSender, getDataSource(report.getSource()));
        }
        throw new UnsupportedOperationException();
    }

    private DataSource getDataSource(String source) {
        return sourceService.getSource(source).orElseThrow(() ->
            new DataSourceNotFound("DataSource for " + source + " not found"));
    }
}
