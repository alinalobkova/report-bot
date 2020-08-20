package com.github.alinalobkova.reportbot.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.github.alinalobkova.reportbot.entity.Report;
import com.github.alinalobkova.reportbot.enums.ExcelCellStyle;
import com.github.alinalobkova.reportbot.utils.ExcelStructureUtil;
import com.github.alinalobkova.reportbot.utils.ExcelStyleUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ExcelCreator {

    public ByteArrayOutputStream createDetailReport(Report report, ResultSet rs1) throws SQLException, IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Map<ExcelCellStyle, CellStyle> styles = ExcelStyleUtil.createStyles(wb);
            int rownum = 0; //в таблице номер строки будет rownum+1 - нумерация с 1 а не с 0
            XSSFSheet sheet = (XSSFSheet) ExcelStructureUtil.createSheet(wb, report.getName());
            XSSFRow headerRow = sheet.createRow(rownum++); //строка для заголовка
            XSSFRow newRow;
            headerRow.setHeightInPoints(12.75f);

            ResultSetMetaData resultMetaData = rs1.getMetaData();//получить данные о колонках
            int columnCount = resultMetaData.getColumnCount();
            int workMonth = -1;
            int workYear = -1;

            for (int i = 0; i < columnCount; i++) {
                Cell hcell = headerRow.createCell(i);
                hcell.setCellValue(resultMetaData.getColumnLabel(i + 1));
                hcell.setCellStyle(styles.get(ExcelCellStyle.HEADER));
            }
            sheet.createFreezePane(0, 1);

            //итоговые строки
            Map<String, Integer> yearRow = new HashMap<>();
            Map<String, Integer> yearStart = new HashMap<>();
            Map<String, Integer> yearEnd = new HashMap<>();

            Map<String, Integer> monthRow = new HashMap<>();
            Map<String, Integer> monthStart = new HashMap<>();
            Map<String, Integer> monthEnd = new HashMap<>();
            Map<String, Integer> allMonthStart = new HashMap<>();
            Map<String, Integer> allMonthEnd = new HashMap<>();

            String oldMonth = "";
            //String collapsed_month="";// месяц, который надо сворачивать
            int monthCount = 0;
            String oldYear = "";

            while (rs1.next()) {  //перебираем полученные из оракла данные
                // читаем данные таблицы
                newRow = sheet.createRow(rownum);
                newRow.setHeightInPoints(12.75f);
                //анализ месяца/года для формул
                //читаем дату из первой колонки
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(rs1.getDate(1));
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                String dateKey = calendar.get(Calendar.YEAR) + "." + calendar.get(Calendar.MONTH);
                String yearKey = String.valueOf(year);
                if (workMonth < 0) {// если это первая строка таблицф и месяц не определен еще
                    workMonth = month;
                    oldMonth = dateKey;
                    workYear = year;
                    oldYear = yearKey;
                }

                if (workMonth != month) { // новый месяц, в текущую строку пишем  итог по старому и запоминаем строку
                    Cell cell = newRow.createCell(0); // название месяца
                    ExcelStructureUtil.createMonthTotalRow(cell, styles, newRow, resultMetaData, columnCount,
                            monthStart, monthEnd, oldMonth, workMonth);
                    monthRow.put(oldMonth, rownum);
                    // группируем месяц

                    //добавляем  ссылку на первую и последнюю строку в хеши всех месяцев
                    //ключ - общий счетчик месяцев
                    // можно было бы связать month_count и old_month - но решил сделать так
                    allMonthStart.put(Integer.toString(monthCount), monthStart.get(oldMonth));
                    allMonthEnd.put(Integer.toString(monthCount), monthEnd.get(oldMonth));
                    int mc = monthCount - 1;
                    if (mc >= 0) { //текущий и предидущий месяц не группируем и не сворачиваем
                        sheet.groupRow(allMonthStart.get(Integer.toString(mc)), allMonthEnd.get(Integer.toString(mc)));
                        int collapsedrow = allMonthStart.get(Integer.toString(mc));
                        sheet.setRowGroupCollapsed(collapsedrow, true);
                    }
                    monthCount++;
                    // добавить номер строки для суммы года

                    workMonth = month;//добавляем пустую строку
                    rownum++;
                    newRow = sheet.createRow(rownum);
                    newRow.setHeightInPoints(12.75f);

                    if (workYear != year) { // новый ujl, в текущую строку пишем  итог по старому и запоминаем строку
                        //проверяем и запоминаем  последнюю строку года  - итоговую последнего месяца это rownum-1 (rownum текущая итоговая года)
                        checkEndYear(rownum, yearEnd, oldYear);
                        cell = newRow.createCell(0); // год
                        // cell_i.setCellValue(month_name.get(workMonth)+"-"+year);
                        ExcelStructureUtil.createYearTotalRow(cell, styles, newRow,
                                resultMetaData, columnCount, yearStart, yearEnd, oldYear, workYear
                        );
                        yearRow.put(oldYear, rownum);
                        // группируем год
                        // и сворачиваем
                        workYear = year;//добавляем пустую строку
                        rownum++;
                        newRow = sheet.createRow(rownum);
                        newRow.setHeightInPoints(12.75f);
                    }
                }

                checkPeriod(rownum, dateKey, monthStart, monthEnd);
                // проверим , чтоб строка конца мес не была меньше строки начала
                if (monthStart.get(dateKey) > monthEnd.get(dateKey)) {
                    monthEnd.put(dateKey, monthStart.get(dateKey));
                }
                checkPeriod(rownum, yearKey, yearStart, yearEnd);
                //проверяем и запоминаем первую  строку года
                // проверим , чтоб строка конца года не была меньше строки начала
                if (yearStart.get(yearKey) > yearEnd.get(yearKey)) {
                    yearEnd.put(yearKey, yearStart.get(yearKey));
                }

//запоминаем текущий месяц в old_month
                oldMonth = dateKey;
                oldYear = yearKey;

                //формируем строку таблицы
                for (int i = 0; i < columnCount; i++) {
                    Cell cell = newRow.createCell(i);
                    ExcelStructureUtil.setCellValue(cell, i + 1, rs1, resultMetaData, styles);
                }
                rownum++;
            }
            // Итог  по текущему месяцу
            newRow = sheet.createRow(rownum);
            Cell totalMonthCell = newRow.createCell(0); // название месяца
            ExcelStructureUtil.createMonthTotalRow(totalMonthCell, styles, newRow, resultMetaData, columnCount,
                    monthStart, monthEnd, oldMonth, workMonth);
            monthRow.put(oldMonth, rownum);

            rownum++;
            newRow = sheet.createRow(rownum);
            newRow.setHeightInPoints(12.75f);
            //Итог по текущему году
            //проверяем и запоминаем  последнюю строку года  - итоговую последнего месяца это rownum-1 (rownum текущая итоговая)
            checkEndYear(rownum, yearEnd, oldYear);
            Cell totalYearCell = newRow.createCell(0); // год
            ExcelStructureUtil.createYearTotalRow(totalYearCell, styles, newRow,
                    resultMetaData, columnCount, yearStart, yearEnd, oldYear, workYear
            );
            yearRow.put(oldYear, rownum);
            rownum++;
            newRow = sheet.createRow(rownum);
            newRow.setHeightInPoints(12.75f);
            Cell totalAllCell = newRow.createCell(0); // Всего
            totalAllCell.setCellValue("Всего");
            totalAllCell.setCellStyle(styles.get(ExcelCellStyle.TOTAL));
            for (int i = 1; i < columnCount; i++) {
                Cell cell = newRow.createCell(i);
                StringBuilder allFormula = new StringBuilder();
                for (String key : monthRow.keySet()) {
                    CellAddress CA = new CellAddress(monthRow.get(key), i);
                    allFormula.insert(0, "+" + CA.formatAsString());
                }
                cell.setCellFormula(allFormula.substring(1));

                String zag = resultMetaData.getColumnLabel(i + 1);
                if (zag.contains(",руб")) {
                    cell.setCellStyle(styles.get(ExcelCellStyle.TOTAL_MONEY));
                } else {
                    cell.setCellStyle(styles.get(ExcelCellStyle.TOTAL));
                }
            }
            wb.getCreationHelper().createFormulaEvaluator().evaluateAll();//пересчет всех формул

            for (int i = 0; i < columnCount; i++) {
                sheet.autoSizeColumn(i);
            }
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                wb.write(out);
                return out;
            }
        }
    }

    private void checkEndYear(int rownum, Map<String, Integer> yearEnd, String oldYear) {
        if (yearEnd.containsKey(oldYear)) {
            if (rownum - 1 > yearEnd.get(oldYear)) {
                yearEnd.put(oldYear, rownum - 1);
            }
        } else {
            yearEnd.put(oldYear, rownum - 1);
        }
    }

    private void checkPeriod(int rownum, String dateKey, Map<String, Integer> periodStart, Map<String, Integer> periodEnd) {
        if (periodStart.containsKey(dateKey)) {
            if (rownum < periodStart.get(dateKey)) {
                periodStart.put(dateKey, rownum);
            } //проверяем может таки меньше номер строки
        } else {
            periodStart.put(dateKey, rownum);//запоминаем первую строку месяца
        }
        //проверяем и запоминаем  последнюю строку месяца
        if (periodEnd.containsKey(dateKey)) {
            if (rownum > periodEnd.get(dateKey)) {
                periodEnd.put(dateKey, rownum);
            }
        } else {
            periodEnd.put(dateKey, rownum);
        }
    }


    public ByteArrayOutputStream createReport(Report report, ResultSet resultSet, Optional<String> title) throws SQLException, IOException {
        try (Workbook wb = new SXSSFWorkbook()) {
            Map<ExcelCellStyle, CellStyle> styles = ExcelStyleUtil.createStyles(wb);
            SXSSFSheet sheet = (SXSSFSheet) ExcelStructureUtil.createSheet(wb, report.getName());
            sheet.trackAllColumnsForAutoSizing();
            int rownum = 0; //в таблице номер строки будет rownum+1 - нумерация с 1 а не с 0
            SXSSFRow headerRow = sheet.createRow(rownum); //строка для заголовка
            rownum++;
            headerRow.setHeightInPoints(12.75f);
            if (title.isPresent()) {
                headerRow.createCell(0);
                SXSSFCell cell = headerRow.createCell(1);
                cell.setCellValue(title.get());

                headerRow = sheet.createRow(rownum); //строка для заголовка
                rownum++;
            }
            ResultSetMetaData metaData = resultSet.getMetaData();//получить данные о колонках
            int cols = metaData.getColumnCount();

            Cell hcell = headerRow.createCell(0);
            hcell.setCellValue("№");
            hcell.setCellStyle(styles.get(ExcelCellStyle.HEADER));

            for (int i = 1; i <= cols; i++) {
                hcell = headerRow.createCell(i);
                hcell.setCellValue(metaData.getColumnLabel(i));
                hcell.setCellStyle(styles.get(ExcelCellStyle.HEADER));
            }
            sheet.createFreezePane(0, 1);
            SXSSFRow newRow;
            int iter = 0;
            while (resultSet.next()) {  //перебираем полученные из оракла данные
                // читаем данные таблицы
                newRow = sheet.createRow(rownum);
                newRow.setHeightInPoints(12.75f);
                //формируем строку таблицы
                Cell cell = newRow.createCell(0);
                cell.setCellValue(++iter);
                cell.setCellStyle(styles.get(ExcelCellStyle.ORDINARY));
                for (int i = 1; i <= cols; i++) {
                    cell = newRow.createCell(i);
                    ExcelStructureUtil.setCellValue(cell, i, resultSet, metaData, styles);
                }
                rownum++;
            }
            for (int i = 0; i <= cols; i++) {
                sheet.autoSizeColumn(i, true);
            }
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                wb.write(out);
                return out;
            }
        }
    }
}
