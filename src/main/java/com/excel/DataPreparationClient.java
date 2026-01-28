package com.excel;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Complete data preparation client for extracting Excel template data to JSON
 * Uses FileHandler for all file operations and contains complete extraction
 * logic
 */
public class DataPreparationClient {

    private static final String TEMPLATE_RESOURCE_PATH = "/gov_template.xlsm";
    private static final String WORKSHEET_NAME = "Simulateur UVC";
    private static final int HEADER_ROW_NUMBER = 10; // Row 11 in Excel (0-based index)
    private static final int MAX_COLUMNS = 50; // A to AX
    private static final String OUTPUT_JSON_FILE = "simulateur_uvc_data.json";

    public static void main(String[] args) {
        System.out.println("=== Data Preparation Client ===");
        System.out.println("Extracting data from " + TEMPLATE_RESOURCE_PATH + " to JSON format");

        try {
            extractTemplateDataToJson();
            System.out.println("✅ Data extraction completed successfully!");
        } catch (Exception e) {
            System.err.println("❌ Error during data extraction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Main method to extract template data and convert to JSON
     */
    public static void extractTemplateDataToJson() throws Exception {
        long startTime = System.currentTimeMillis();

        // Load template using FileHandler
        File templateFile = loadTemplateFile();
        System.out.println("📂 Loaded template: " + templateFile.getName());

        // Extract data from Excel
        List<Map<String, Object>> data = readTemplateData(templateFile);
        System.out.println("📊 Extracted " + data.size() + " rows from worksheet: " + WORKSHEET_NAME);

        // Convert to JSON
        String jsonContent = convertToJson(data);
        System.out.println("🔄 Converted data to JSON format (" + jsonContent.length() + " characters)");

        // Write JSON to file using FileHandler
        writeJsonToFile(jsonContent);

        long processingTime = System.currentTimeMillis() - startTime;
        System.out.println("⏱️ Total processing time: " + processingTime + "ms");
    }

    /**
     * Load template file using FileHandler
     */
    private static File loadTemplateFile() throws Exception {
        // For .xlsm files, we need to load directly from resources
        File resourcesDir = new File("src/main/resources");
        File templateFile = new File(resourcesDir, "gov_template.xlsm");

        if (!templateFile.exists()) {
            throw new FileNotFoundException("Template file not found: " + templateFile.getAbsolutePath());
        }

        return templateFile;
    }

    /**
     * Read template data from Excel file (complete logic from OpcExcelClient)
     */
    private static List<Map<String, Object>> readTemplateData(File templateFile) throws Exception {
        List<Map<String, Object>> data = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(templateFile);
                XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(WORKSHEET_NAME);
            if (sheet == null) {
                throw new RuntimeException("Worksheet '" + WORKSHEET_NAME + "' not found in template");
            }

            System.out.println("📖 Reading data from worksheet: " + WORKSHEET_NAME);
            System.out.println("📍 Starting from row " + (HEADER_ROW_NUMBER + 2) + " (after header)");

            // Read data starting from row after header
            for (int rowNum = HEADER_ROW_NUMBER; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row != null && !isRowEmpty(row)) {
                    Map<String, Object> rowData = new LinkedHashMap<>();

                    // Read all columns up to MAX_COLUMNS
                    for (int colIndex = 0; colIndex < MAX_COLUMNS; colIndex++) {
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

        return data;
    }

    /**
     * Convert data to JSON format (manual implementation to avoid external
     * dependencies)
     */
    private static String convertToJson(List<Map<String, Object>> data) {
        StringBuilder json = new StringBuilder();

        // Create metadata
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        json.append("{\n");
        json.append("  \"metadata\": {\n");
        json.append("    \"sourceFile\": \"gov_template.xlsm\",\n");
        json.append("    \"worksheet\": \"").append(WORKSHEET_NAME).append("\",\n");
        json.append("    \"extractedAt\": \"").append(timestamp).append("\",\n");
        json.append("    \"totalRows\": ").append(data.size()).append(",\n");
        json.append("    \"startRow\": ").append(HEADER_ROW_NUMBER + 1).append(",\n");
        json.append("    \"columnRange\": \"A-AX\"\n");
        json.append("  },\n");

        // Add data array
        json.append("  \"data\": [\n");

        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> row = data.get(i);
            json.append("    {\n");

            List<String> keys = new ArrayList<>(row.keySet());
            for (int j = 0; j < keys.size(); j++) {
                String key = keys.get(j);
                Object value = row.get(key);

                json.append("      \"").append(key).append("\": ");
                json.append(formatJsonValue(value));

                if (j < keys.size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }

            json.append("    }");
            if (i < data.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("  ]\n");
        json.append("}");

        return json.toString();
    }

    /**
     * Format value for JSON output
     */
    private static String formatJsonValue(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            // Escape JSON special characters
            String str = value.toString()
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
            return "\"" + str + "\"";
        } else if (value instanceof Number) {
            return value.toString();
        } else if (value instanceof Boolean) {
            return value.toString();
        } else if (value instanceof Date) {
            return "\"" + value.toString() + "\"";
        } else {
            // Convert to string and escape
            String str = value.toString()
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
            return "\"" + str + "\"";
        }
    }

    /**
     * Write JSON content to file using FileHandler
     */
    private static void writeJsonToFile(String jsonContent) throws IOException {
        File outputFile = FileHandler.getOutputFile(OUTPUT_JSON_FILE);

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] jsonBytes = jsonContent.getBytes("UTF-8");
            FileHandler.writeBytes(jsonBytes, fos);
        }

        System.out.println("💾 JSON file created: " + outputFile.getAbsolutePath());
        System.out.println("📊 File size: " + (outputFile.length() / 1024) + " KB");
    }

    /**
     * Check if a row is empty (checks first 14 columns A-N)
     */
    private static boolean isRowEmpty(Row row) {
        for (int colNum = 0; colNum < 14; colNum++) {
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
     * Convert column index to Excel column letter (A, B, C, ..., AX)
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
     * Get cell value with proper formatting handling (complete logic from
     * OpcExcelClient)
     */
    private static Object getCellValue(Cell cell) {
        if (cell == null)
            return null;

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
                    boolean hasSpecialFormat = formatString != null && (formatString.contains("0000") ||
                            formatString.contains("000") ||
                            formatString.contains("00") ||
                            formatString.startsWith("0") ||
                            formatString.contains("\"0") ||
                            formatString.contains("@") ||
                            formatString.toLowerCase().contains("text") ||
                            (formatIndex >= 164 && formatIndex <= 180) ||
                            formatIndex == 49);

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

    /**
     * Get extracted data from JSON file (utility method for other classes)
     */
    public static List<Map<String, Object>> loadDataFromJson() throws IOException {
        File jsonFile = FileHandler.getOutputFile(OUTPUT_JSON_FILE);
        if (!jsonFile.exists()) {
            throw new FileNotFoundException("JSON data file not found. Run extractTemplateDataToJson() first.");
        }

        // For now, return empty list - full JSON parsing would require more complex
        // implementation
        // This can be enhanced later if needed
        System.out.println("📖 JSON file exists: " + jsonFile.getAbsolutePath());
        return new ArrayList<>();
    }
}
