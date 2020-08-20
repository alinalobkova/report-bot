package com.github.alinalobkova.reportbot.utils;

import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.*;
import com.github.alinalobkova.reportbot.enums.ExcelCellStyle;
import com.github.alinalobkova.reportbot.enums.ExcelCellType;

import java.util.HashMap;
import java.util.Map;

import static com.github.alinalobkova.reportbot.enums.ExcelCellStyle.*;
import static com.github.alinalobkova.reportbot.enums.ExcelCellType.*;

@UtilityClass
public class ExcelStyleUtil {
    private final static String FONT_NAME = "Calibri";

    public Map<ExcelCellStyle, CellStyle> createStyles(Workbook wb) {
        Map<ExcelCellStyle, CellStyle> styles = new HashMap<>();

        Font headerFont = wb.createFont();
        headerFont.setFontName(FONT_NAME);
        headerFont.setFontHeightInPoints((short) 9);
        headerFont.setBold(true);

        Font normalFont = wb.createFont();
        normalFont.setFontName(FONT_NAME);
        normalFont.setFontHeightInPoints((short) 9);
        normalFont.setBold(false);

        styles.put(HEADER, getColorTextCellBorderStyle(wb, headerFont, IndexedColors.YELLOW, HorizontalAlignment.CENTER, TEXT));
        styles.put(MONTH, getColorTextCellBorderStyle(wb, headerFont, IndexedColors.GREY_25_PERCENT, HorizontalAlignment.RIGHT, TEXT));
        styles.put(MONTH_MONEY, getColorTextCellBorderStyle(wb, headerFont, IndexedColors.GREY_25_PERCENT, HorizontalAlignment.RIGHT, MONEY));
        styles.put(YEAR, getColorTextCellBorderStyle(wb, headerFont, IndexedColors.LIGHT_CORNFLOWER_BLUE, HorizontalAlignment.RIGHT, TEXT));
        styles.put(YEAR_MONEY, getColorTextCellBorderStyle(wb, headerFont, IndexedColors.LIGHT_CORNFLOWER_BLUE, HorizontalAlignment.RIGHT, MONEY));
        styles.put(TOTAL, getColorTextCellBorderStyle(wb, headerFont, IndexedColors.LIGHT_GREEN, HorizontalAlignment.RIGHT, TEXT));
        styles.put(TOTAL_MONEY, getColorTextCellBorderStyle(wb, headerFont, IndexedColors.LIGHT_GREEN, HorizontalAlignment.RIGHT, MONEY));
        styles.put(ORDINARY, getColorTextCellBorderStyle(wb, normalFont, IndexedColors.WHITE, HorizontalAlignment.RIGHT, TEXT));
        styles.put(ORDINARY_MONEY, getColorTextCellBorderStyle(wb, normalFont, IndexedColors.WHITE, HorizontalAlignment.RIGHT, MONEY));
        styles.put(DATE_SIMPLE, getColorTextCellBorderStyle(wb, headerFont, IndexedColors.WHITE, HorizontalAlignment.CENTER, DATE));
        return styles;
    }


    private CellStyle getColorTextCellBorderStyle(Workbook wb, Font font, IndexedColors color, HorizontalAlignment alignment, ExcelCellType cellType) {
        CellStyle style = createBorderedStyle(wb);
        CreationHelper creationHelper = wb.getCreationHelper();
        setStyleParameters(style, font, color, alignment);
        setCellStyleFormat(creationHelper, style, cellType);
        return style;
    }

    private void setCellStyleFormat(CreationHelper creationHelper, CellStyle style, ExcelCellType type) {
        switch (type) {
            case MONEY: {
                style.setDataFormat(creationHelper.createDataFormat().getFormat("#,##0.00p"));
                break;
            }
            case DATE: {
                style.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy.mm.dd"));
                break;
            }
        }
    }

    private void setStyleParameters(CellStyle style, Font headerFont, IndexedColors color, HorizontalAlignment alignment) {
        style.setAlignment(alignment);
        style.setFillForegroundColor(color.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(headerFont);
    }

    private CellStyle createBorderedStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        return style;
    }


}
