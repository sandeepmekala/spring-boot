package com.excel;

import java.io.*;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;

/**
 * Simplified Excel client for Simulateur UVC processing
 */
public class OpcExcelClient {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Simulateur UVC Excel Client ===");
        processSimulateurUVC();
    }

    /**
     * Main method to process Simulateur UVC Excel files
     */
    private static void processSimulateurUVC() throws Exception {
        File template = new File("src/main/resources/gov_template2.xlsm");
        File output = FileHandler.getOutputFile("simulateur_uvc_output.xlsm");

        // Read actual data from template to preserve macros and formulas
        List<Map<String, Object>> data = readTemplateData(template);

        System.out.println("📊 Processing " + data.size() + " rows for Simulateur UVC worksheet");

        long startTime = System.currentTimeMillis();

        // Process the Excel file
        OptimizedExcelWriter.fillExcelTemplate(template, output, data, "Simulateur UVC", 10);

        long processingTime = System.currentTimeMillis() - startTime;

        System.out.println("✅ Finished processing to: " + output.getPath());
        System.out.println("⏱️ Processing time: " + processingTime + "ms");
        System.out.println("📊 File size: " + (output.length() / 1024) + " KB");
    }

    /**
     * Read actual data from the template file to preserve macros and formulas
     */
    private static List<Map<String, Object>> readTemplateData(File templateFile) {
        try {
            List<Map<String, Object>> data = new ArrayList<>();

            try (FileInputStream fis = new FileInputStream(templateFile);
                 XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

                Sheet sheet = workbook.getSheet("Simulateur UVC");
                if (sheet == null) {
                    throw new RuntimeException("Simulateur UVC worksheet not found");
                }

                // Read data starting from row 11 (index 10)
                for (int rowNum = 10; rowNum <= sheet.getLastRowNum(); rowNum++) {
                    Row row = sheet.getRow(rowNum);
                    if (row != null && !isRowEmpty(row)) {
                        Map<String, Object> rowData = new LinkedHashMap<>();

                        // Read all columns
                        for (int colIndex = 0; colIndex < 50; colIndex++) { // Read up to column AX
                            Cell cell = row.getCell(colIndex);
                            if (cell != null) {
                                Object cellValue = getCellValue(cell);
                                if (cellValue != null && !cellValue.toString().trim().isEmpty()) {
                                    String columnLetter = getColumnLetter(colIndex);
                                    rowData.put(columnLetter, cellValue);
                                }
                            }
                        }

                        if (!rowData.isEmpty()) {
                            data.add(rowData);
                        }
                    }
                }
            }

            System.out.println("📖 Read " + data.size() + " rows from template");
            return data;

        } catch (Exception e) {
            System.err.println("❌ Error reading template data: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Check if a row is empty
     */
    private static boolean isRowEmpty(Row row) {
        for (int colNum = 0; colNum < 14; colNum++) { // Check columns A-N
            Cell cell = row.getCell(colNum);
            if (cell != null) {
                Object value = getCellValue(cell);
                if (value != null && !value.toString().trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Convert column index to Excel column letter (A, B, C, ...)
     */
    private static String getColumnLetter(int columnIndex) {
        if (columnIndex < 26) {
            return String.valueOf((char) ('A' + columnIndex));
        } else {
            int firstLetter = (columnIndex / 26) - 1;
            int secondLetter = columnIndex % 26;
            return String.valueOf((char) ('A' + firstLetter)) + String.valueOf((char) ('A' + secondLetter));
        }
    }

    /**
     * Get cell value with proper formatting handling
     */
    private static Object getCellValue(Cell cell) {
        if (cell == null) return null;

        DataFormatter formatter = new DataFormatter();

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    String formatString = cell.getCellStyle().getDataFormatString();
                    short formatIndex = cell.getCellStyle().getDataFormat();

                    // Handle leading zeros and special formatting
                    boolean hasSpecialFormat = formatString != null && (
                        formatString.contains("0000") ||
                        formatString.contains("000") ||
                        formatString.contains("00") ||
                        formatString.startsWith("0") ||
                        formatString.contains("\"0") ||
                        formatString.contains("@") ||
                        formatString.toLowerCase().contains("text") ||
                        (formatIndex >= 164 && formatIndex <= 180) ||
                        formatIndex == 49
                    );

                    if (hasSpecialFormat) {
                        return formatter.formatCellValue(cell);
                    } else {
                        double numValue = cell.getNumericCellValue();
                        return numValue == Math.floor(numValue) ? (long) numValue : numValue;
                    }
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                try {
                    switch (cell.getCachedFormulaResultType()) {
                        case STRING:
                            return cell.getStringCellValue();
                        case NUMERIC:
                            String formatString = cell.getCellStyle().getDataFormatString();
                            if (formatString != null && (formatString.contains("0000") ||
                                formatString.startsWith("0") || formatString.contains("\"0") ||
                                formatString.contains("@"))) {
                                return formatter.formatCellValue(cell);
                            } else {
                                double numValue = cell.getNumericCellValue();
                                return numValue == Math.floor(numValue) ? (long) numValue : numValue;
                            }
                        case BOOLEAN:
                            return cell.getBooleanCellValue();
                        default:
                            return "=" + cell.getCellFormula();
                    }
                } catch (Exception e) {
                    return "=" + cell.getCellFormula();
                }
            case BLANK:
                return null;
            default:
                return cell.toString();
        }
    }
}
