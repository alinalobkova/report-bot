package com.github.alinalobkova.reportbot.utils;

import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import com.github.alinalobkova.reportbot.enums.ExcelCellStyle;
import com.github.alinalobkova.reportbot.enums.Month;

import java.rmi.UnexpectedException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.Map;

@UtilityClass
public class ExcelStructureUtil {

    public void createMonthTotalRow(Cell cell, Map<ExcelCellStyle, CellStyle> styles, XSSFRow newRow,
                                    ResultSetMetaData resultSetMetaData, int cols,
                                    Map<String, Integer> monthStart, Map<String, Integer> monthEnd,
                                    String oldMonth, int workMonth) throws SQLException, UnexpectedException {
        cell.setCellValue(Month.valueOf(workMonth + 1).getRussianName());
        cell.setCellStyle(styles.get(ExcelCellStyle.MONTH));
        ExcelStructureUtil.createTotalRow(styles, resultSetMetaData, cols, monthStart, monthEnd, oldMonth, newRow, ")",
                ExcelCellStyle.MONTH_MONEY, ExcelCellStyle.MONTH);

    }

    public void createYearTotalRow(Cell cell, Map<ExcelCellStyle, CellStyle> styles, XSSFRow newRow,
                                   ResultSetMetaData resultSetMetaData, int cols,
                                   Map<String, Integer> yearStart, Map<String, Integer> yearEnd,
                                   String oldYear, int workYear) throws SQLException, UnexpectedException {
        cell.setCellValue("Всего за " + workYear);
        cell.setCellStyle(styles.get(ExcelCellStyle.YEAR));
        ExcelStructureUtil.createTotalRow(styles, resultSetMetaData, cols, yearStart, yearEnd, oldYear, newRow, ")/2", ExcelCellStyle.YEAR_MONEY, ExcelCellStyle.YEAR);
    }

    public Sheet createSheet(Workbook wb, String sheetName) {
        Sheet sheet = wb.createSheet(sheetName);
        sheet.setDisplayGridlines(false);
        sheet.setPrintGridlines(false);
        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);
        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        return sheet;
    }

    private void createTotalRow(Map<ExcelCellStyle, CellStyle> styles, ResultSetMetaData resultSetMetaData, int cols,
                                Map<String, Integer> periodStart, Map<String, Integer> periodEnd, String oldPeriod,
                                XSSFRow newRow, String s, ExcelCellStyle moneyStyle, ExcelCellStyle style) throws SQLException {
        for (int i = 1; i < cols; i++) {
            Cell cell = newRow.createCell(i);
            CellRangeAddress cellRangeAddress = new CellRangeAddress(periodStart.get(oldPeriod), periodEnd.get(oldPeriod), i, i);
            cell.setCellFormula("SUM(" + cellRangeAddress.formatAsString() + s); // в сумму года попали итоговые строки - делим результат на 2
            if (isMoneyColumn(resultSetMetaData.getColumnLabel(i + 1))) {
                cell.setCellStyle(styles.get(moneyStyle));
            } else {
                cell.setCellStyle(styles.get(style));
            }
        }
    }

    public void setCellValue(Cell cell, Integer i, ResultSet resultSet, ResultSetMetaData resultSetMetaData, Map<ExcelCellStyle, CellStyle> styles) throws SQLException {
        int sqlTypeCell = resultSetMetaData.getColumnType(i);
        switch (sqlTypeCell) { //sqlTypeCell
            case Types.TIMESTAMP:
                Calendar dt = Calendar.getInstance();
                dt.setTime(resultSet.getDate(i));
                cell.setCellValue(dt.getTime());
                cell.setCellStyle(styles.get(ExcelCellStyle.DATE_SIMPLE));
                break;
            case Types.INTEGER:
                if (resultSet.getDouble(i) != 0) {
                    cell.setCellValue(resultSet.getDouble(i));
                }
                cell.setCellStyle(styles.get(ExcelCellStyle.ORDINARY));
                break;
            case Types.NUMERIC:
                if (resultSet.getDouble(i) != 0) {
                    cell.setCellValue(resultSet.getDouble(i));
                }
                if (isMoneyColumn(resultSetMetaData.getColumnLabel(i))) {
                    cell.setCellStyle(styles.get(ExcelCellStyle.ORDINARY_MONEY));
                } else {
                    cell.setCellStyle(styles.get(ExcelCellStyle.ORDINARY));
                }
                break;
            default:
                cell.setCellValue(resultSet.getString(i));
                cell.setCellStyle(styles.get(ExcelCellStyle.ORDINARY));
        }
    }

    private boolean isMoneyColumn(String columnName) {
        return columnName.contains(",руб");
    }

}
