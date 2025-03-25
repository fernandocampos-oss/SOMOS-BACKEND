package pe.gob.essalud.apps.common.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtil {

    public static void createCell(Row row, int column, Object value, CellStyle style, boolean isFormula) {
        Cell cell = row.createCell(column);
        setCellValue(value, isFormula, cell);
        cell.setCellStyle(style);
    }

    public static void updateCellValue(Row row, int column, Object newValue, boolean isFormula) {
        Cell cell = row.getCell(column, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        setCellValue(newValue, isFormula, cell);
    }

    private static void setCellValue(Object newValue, boolean isFormula, Cell cell) {
        if (isFormula && newValue instanceof String) {
            cell.setCellFormula((String) newValue);
        } else if (newValue instanceof Integer) {
            cell.setCellValue((Integer) newValue);
        } else if (newValue instanceof Double) {
            cell.setCellValue((Double) newValue);
        } else if (newValue != null) {
            cell.setCellValue(newValue.toString());
        } else {
            cell.setCellType(CellType.BLANK);
        }
    }

    public static void mergeCellsInColumn(XSSFSheet sheet, int startRow, int endRow, int colIndex, CellStyle style) {
        if (startRow != endRow) {
            // Solo fusiona si hay más de una fila
            sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, colIndex, colIndex));
        }

        for (int i = startRow; i <= endRow; i++) {
            Row row = sheet.getRow(i);
            if (row == null) row = sheet.createRow(i);

            Cell cell = row.getCell(colIndex);
            if (cell == null) cell = row.createCell(colIndex);

            cell.setCellStyle(style); // Aplica el estilo
        }
    }

    public static void mergeCellsInRow(XSSFSheet sheet, int rowIndex, int startCol, int endCol, CellStyle style) {
        if (startCol != endCol) {
            // Solo fusiona si hay más de una columna
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, startCol, endCol));
        }

        Row row = sheet.getRow(rowIndex);
        if (row == null) row = sheet.createRow(rowIndex);

        for (int j = startCol; j <= endCol; j++) {
            Cell cell = row.getCell(j);
            if (cell == null) cell = row.createCell(j);
            cell.setCellStyle(style); // Aplica el estilo a todas las celdas
        }
    }

    public static void mergeCells(XSSFSheet sheet, int startRow, int endRow, int startCol, int endCol, CellStyle style) {
        sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, startCol, endCol));
        for (int i = startRow; i <= endRow; i++) {
            Row row = sheet.getRow(i);
            if (row == null) row = sheet.createRow(i);
            for (int j = startCol; j <= endCol; j++) {
                Cell cell = row.getCell(j);
                if (cell == null) cell = row.createCell(j);
                cell.setCellStyle(style);
            }
        }
    }

    public static XSSFCellStyle createCenteredStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }

    public static void moveFooterDown(XSSFSheet sheet, int startRow, int totalRowsNeeded) {
        int footerEndRow = sheet.getLastRowNum(); // Última fila actual

        // Guardar anchos de columnas antes de mover filas
        Map<Integer, Integer> columnWidths = new HashMap<>();
        for (int col = 0; col < sheet.getRow(startRow).getLastCellNum(); col++) {
            columnWidths.put(col, sheet.getColumnWidth(col));
        }

        // Guardar regiones fusionadas antes de mover filas
        List<CellRangeAddress> mergedRegions = new ArrayList<>();
        for (int i = sheet.getNumMergedRegions() - 1; i >= 0; i--) {
            CellRangeAddress region = sheet.getMergedRegion(i);
            if (region.getFirstRow() >= startRow) {
                mergedRegions.add(region);
                sheet.removeMergedRegion(i); // Eliminar temporalmente
            }
        }

        // Guardar validaciones de datos
        List<XSSFDataValidation> validations = new ArrayList<>();
        XSSFSheet sheetXSSF = (XSSFSheet) sheet;
        for (XSSFDataValidation validation : sheetXSSF.getDataValidations()) {
            CellRangeAddressList regions = validation.getRegions();
            for (CellRangeAddress region : regions.getCellRangeAddresses()) {
                if (region.getFirstRow() >= startRow) {
                    validations.add(validation);
                }
            }
        }

        // Guardar comentarios antes de mover filas
        Map<Integer, Map<Integer, String>> commentsMap = new HashMap<>();
        for (int i = startRow; i <= footerEndRow; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                for (Cell cell : row) {
                    if (cell.getCellComment() != null) {
                        String commentText = cell.getCellComment().getString().getString();
                        commentsMap.computeIfAbsent(i, k -> new HashMap<>()).put(cell.getColumnIndex(), commentText);
                    }
                }
            }
        }

        // Eliminar comentarios de las celdas originales
        for (int i = startRow; i <= footerEndRow; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                for (Cell cell : row) {
                    if (cell.getCellComment() != null) {
                        cell.removeCellComment();
                    }
                }
            }
        }

        // Mover filas del footer hacia abajo
        for (int i = footerEndRow; i >= startRow; i--) {
            Row oldRow = sheet.getRow(i);
            Row newRow = sheet.createRow(i + totalRowsNeeded);

            if (oldRow != null) {
                newRow.setHeight(oldRow.getHeight()); // Mantener altura de fila

                for (int j = 0; j < oldRow.getLastCellNum(); j++) {
                    Cell oldCell = oldRow.getCell(j);
                    if (oldCell != null) {
                        Cell newCell = newRow.createCell(j, oldCell.getCellType());

                        // Mantener el estilo de la celda
                        newCell.setCellStyle(oldCell.getCellStyle());

                        // Copiar contenido según el tipo de celda
                        switch (oldCell.getCellTypeEnum()) {
                            case STRING:
                                newCell.setCellValue(oldCell.getStringCellValue());
                                break;
                            case NUMERIC:
                                newCell.setCellValue(oldCell.getNumericCellValue());
                                break;
                            case BOOLEAN:
                                newCell.setCellValue(oldCell.getBooleanCellValue());
                                break;
                            case FORMULA:
                                newCell.setCellFormula(oldCell.getCellFormula());
                                break;
                            case ERROR:
                                newCell.setCellErrorValue(oldCell.getErrorCellValue());
                                break;
                            default:
                                newCell.setCellType(CellType.BLANK);
                        }
                    }
                }
            }
        }

        // Restaurar regiones fusionadas en la nueva posición
        for (CellRangeAddress region : mergedRegions) {
            sheet.addMergedRegion(new CellRangeAddress(
                    region.getFirstRow() + totalRowsNeeded,
                    region.getLastRow() + totalRowsNeeded,
                    region.getFirstColumn(),
                    region.getLastColumn()
            ));
        }

        // Restaurar validaciones de datos
        for (XSSFDataValidation validation : validations) {
            CellRangeAddressList newRegions = new CellRangeAddressList();
            for (CellRangeAddress region : validation.getRegions().getCellRangeAddresses()) {
                newRegions.addCellRangeAddress(
                        new CellRangeAddress(
                                region.getFirstRow() + totalRowsNeeded,
                                region.getLastRow() + totalRowsNeeded,
                                region.getFirstColumn(),
                                region.getLastColumn()
                        )
                );
            }
            XSSFDataValidationHelper helper = new XSSFDataValidationHelper(sheetXSSF);
            XSSFDataValidation newValidation = (XSSFDataValidation) helper.createValidation(validation.getValidationConstraint(), newRegions);
            sheetXSSF.addValidationData(newValidation);
        }

        // Restaurar comentarios en la nueva posición
        for (Map.Entry<Integer, Map<Integer, String>> rowEntry : commentsMap.entrySet()) {
            int oldRowNum = rowEntry.getKey();
            int newRowNum = oldRowNum + totalRowsNeeded;

            for (Map.Entry<Integer, String> cellEntry : rowEntry.getValue().entrySet()) {
                int colIndex = cellEntry.getKey();
                String commentText = cellEntry.getValue();

                Row row = sheet.getRow(newRowNum);
                if (row == null) {
                    row = sheet.createRow(newRowNum);
                }
                Cell cell = row.getCell(colIndex);
                if (cell == null) {
                    cell = row.createCell(colIndex);
                }

                // Crear el comentario en la nueva celda
                CreationHelper factory = sheet.getWorkbook().getCreationHelper();
                Drawing<?> drawing = sheet.createDrawingPatriarch();
                ClientAnchor anchor = factory.createClientAnchor();
                Comment comment = drawing.createCellComment(anchor);
                comment.setString(factory.createRichTextString(commentText));
                cell.setCellComment(comment);
            }
        }

        // Restaurar anchos de columnas
        for (Map.Entry<Integer, Integer> entry : columnWidths.entrySet()) {
            sheet.setColumnWidth(entry.getKey(), entry.getValue());
        }

        // Agregar la fórmula si totalRowsNeeded es mayor a 0
        if (totalRowsNeeded > 0) {
            int formulaRowNumInicio = startRow + totalRowsNeeded;
            String formulaPeso = String.format("IF(OR((SUM(G21:G%d)>1),(SUM(G21:G%d)<1)),\"El peso total debe sumar 100%%\",SUM(G21:G%d))",
                    formulaRowNumInicio, formulaRowNumInicio, formulaRowNumInicio);

            Row formulaRowPeso = sheet.getRow(formulaRowNumInicio);
            if (formulaRowPeso == null) {
                formulaRowPeso = sheet.createRow(formulaRowNumInicio);
            }
            updateCellValue(formulaRowPeso, 6, formulaPeso, true);

            StringBuilder formulaBuilder = new StringBuilder("AND(");
            for (int i = startRow + 1; i <= formulaRowNumInicio; i++) {
                formulaBuilder.append("M").append(i).append("=\"\"");
                if (i < formulaRowNumInicio) {
                    formulaBuilder.append(",");
                }
            }
            formulaBuilder.append(")");
            int formulaRowNumFinal = formulaRowNumInicio + 4;
            String formulaFinal = String.format("IF(L18=\"No\",\"No corresponde\",IF(" + formulaBuilder + ",\"-\",SUM(N21:N%d)))",
                    formulaRowNumInicio);

            Row formulaRowFinal = sheet.getRow(formulaRowNumFinal);
            if (formulaRowFinal == null) {
                formulaRowFinal = sheet.createRow(formulaRowNumFinal);
            }
            updateCellValue(formulaRowFinal, 3, formulaFinal, true);
        }
    }

}
