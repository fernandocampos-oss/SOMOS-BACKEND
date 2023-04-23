package com.marcas.common.util;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class XlsReport {

    private static final Color colorPrimary = new Color(0, 70, 132);
    private static final Color colorText = new Color(0, 0, 1);
    private static final Color colorAltern = new Color(240, 245, 255);

    @Data
    private static class HeadExcel {
        private String title;
        private String comentario;
        private String[] header;
    }

    private static void createTitle(XSSFWorkbook book, XSSFSheet sheet, String title, int mergeTitle) {

        XSSFRow headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(25);
        XSSFCellStyle titleStyle = book.createCellStyle();
        XSSFFont font = book.createFont();

        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        font.setColor(new XSSFColor(colorPrimary));
        Color colorFondo = new Color(255, 255, 255);
        titleStyle.setFillForegroundColor(new XSSFColor(colorFondo));
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setFont(font);
        titleStyle.setWrapText(true);

        XSSFCell cell = headerRow.createCell(0);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, mergeTitle));
        cell.setCellStyle(titleStyle);
        cell.setCellValue(title);
        // Sets the allignment to the created cell
        CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
    }

    private static XSSFCellStyle devuelveCellStyle(
            XSSFWorkbook book,
            Color colorLetra,
            Color colorFondo,
            boolean negrita,
            short tamannoLetra) {

        XSSFFont font = book.createFont();
        XSSFCellStyle cellStyle = book.createCellStyle();
        font.setBold(negrita);
        font.setFontHeightInPoints(tamannoLetra);
        font.setColor(new XSSFColor(colorLetra));
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(new XSSFColor(colorFondo));
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cellStyle;
    }

    private static void autosizeColumns(XSSFSheet sheet, int numColumns) {
        for (int i = 0; i < numColumns; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static HeadExcel getHeadExcel(String headerTitle, String excelTitle) {
        HeadExcel headExcel = new HeadExcel();
        char c = (char) 124; /* | */
        String[] titleHeader = StringUtils.splitPreserveAllTokens(headerTitle, c);
        headExcel.setHeader(titleHeader);
        headExcel.setTitle(excelTitle);
        return headExcel;
    }

    private static <T> void writeRow(List<T> lista, Class<T> type, XSSFSheet sheet,
                                     String rowFields,
                                     XSSFCellStyle cellStyle01,
                                     XSSFCellStyle cellStyle02) throws NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException {
        boolean filaImpar = true;
        CellStyle cellStyleList;
        char c = (char) 124; /* | */
        String[] fieldRow = StringUtils.splitPreserveAllTokens(rowFields, c);
        Method method;
        for (T bean : lista) {
            int lastRow = sheet.getLastRowNum();
            int i = Math.max(lastRow, 0);
            int iCell = 0;
            XSSFRow dataRow = sheet.createRow(i + 1);
            if (filaImpar) {
                cellStyleList = cellStyle01;
            } else {
                cellStyleList = cellStyle02;
            }
            filaImpar = !filaImpar;
            for (String fieldName : fieldRow) {
                XSSFCell cell = dataRow.createCell(iCell);
                cell.setCellStyle(cellStyleList);
                method = type.getMethod("get" + fieldName);
                Object value = method.invoke(bean, (Object[]) null);
                String cellValue = "";
                if (value != null) {
                    if (value instanceof LocalDateTime)
                        cellValue = DateUtil.formatDateTime((LocalDateTime)value);
                    else if (value instanceof LocalDate)
                        cellValue = DateUtil.format((LocalDate)value);
                    else
                        cellValue = value.toString();
                }
                cell.setCellValue(cellValue);
                iCell++;
            }
        }
    }

    public static <T> byte[] getReporte(String sheetName, String excelHeader, String excelTitle,
                                        String fields, List<T> data, Class<T> type) throws IOException,
            NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        byte[] bytes;
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        SXSSFWorkbook book = new SXSSFWorkbook();
        XSSFWorkbook xbook = book.getXSSFWorkbook();
        XSSFSheet sheet = xbook.createSheet(sheetName);

        HeadExcel headExcel = XlsReport.getHeadExcel(excelHeader, excelTitle);
        XlsReport.createTitle(xbook, sheet, headExcel.getTitle(), headExcel.getHeader().length - 1);

        XSSFCellStyle cellStyle01 = XlsReport.devuelveCellStyle(xbook,
                colorText,
                colorAltern,
                false, (short) 10);

        XSSFCellStyle cellStyle02 = XlsReport.devuelveCellStyle(xbook,
                colorText,
                new Color(255, 255, 255),
                false, (short) 10);

        XSSFCellStyle headerCellStyle = XlsReport.devuelveCellStyle(xbook,
                new Color(255, 255, 255),
                colorPrimary,
                true, (short) 10);

        if (!data.isEmpty()) {
            int lastRow = sheet.getLastRowNum();
            int i = Math.max(lastRow, 0);
            XSSFRow dataRow = sheet.createRow(i + 1);
            headerCellStyle.setAlignment(HorizontalAlignment.LEFT);
            headerCellStyle.setWrapText(true);
            int iCellH = 0;
            for (String headerTitle : headExcel.getHeader()) {
                XSSFCell cell = dataRow.createCell(iCellH);
                cell.setCellStyle(headerCellStyle);
                cell.setCellValue(headerTitle);
                iCellH++;
            }
        }
        XlsReport.writeRow(data, type, sheet,
                fields, cellStyle01, cellStyle02);

        XlsReport.autosizeColumns(sheet, headExcel.getHeader().length);
        book.write(outByteStream);
        bytes = outByteStream.toByteArray();
        book.dispose();
        book.close();
        return bytes;
    }
}
