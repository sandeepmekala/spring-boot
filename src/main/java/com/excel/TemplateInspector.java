package com.excel;

import java.io.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Utility to inspect template files for formulas and styles
 */
public class TemplateInspector {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Enhanced Formula Detection Test ===");
        
        // Test multiple detection methods
        testFormulaDetection("src/main/resources/gov_template.xlsm");
        
        System.out.println("\n" + "=".repeat(80) + "\n");
        
        // Also test backup for comparison
        testFormulaDetection("src/main/resources/gov_template_backup.xlsm");
    }

    private static void testFormulaDetection(String filePath) {
        try {
            File templateFile = new File(filePath);
            if (!templateFile.exists()) {
                System.out.println("❌ File not found: " + filePath);
                return;
            }

            System.out.println("📂 Enhanced formula detection in: " + filePath);

            try (FileInputStream fis = new FileInputStream(templateFile);
                 XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

                Sheet sheet = workbook.getSheet("Simulateur UVC");
                if (sheet == null) {
                    System.out.println("❌ Worksheet 'Simulateur UVC' not found");
                    return;
                }

                System.out.println("✅ Found worksheet: Simulateur UVC");
                System.out.println("📊 Last row number: " + sheet.getLastRowNum());
                
                // Test different detection methods
                int method1Count = 0;
                int method2Count = 0;
                int method3Count = 0;
                int totalCellsChecked = 0;

                System.out.println("🔍 Testing multiple formula detection methods...\n");

                // Check first 20 rows after row 10 for detailed analysis
                for (int rowIndex = 10; rowIndex < Math.min(30, sheet.getLastRowNum()); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    if (row != null) {
                        for (int colIndex = 0; colIndex < 50; colIndex++) {
                            Cell cell = row.getCell(colIndex);
                            if (cell != null) {
                                totalCellsChecked++;
                                String cellRef = getColumnLetter(colIndex) + (rowIndex + 1);
                                
                                // Method 1: Direct cell type check
                                boolean isFormulaMethod1 = (cell.getCellType() == CellType.FORMULA);
                                if (isFormulaMethod1) {
                                    method1Count++;
                                    System.out.println("📐 METHOD 1 - Formula at " + cellRef + ": " + cell.getCellFormula());
                                }
                                
                                // Method 2: Try to get formula string (catch exceptions)
                                boolean isFormulaMethod2 = false;
                                try {
                                    String formula = cell.getCellFormula();
                                    if (formula != null && !formula.trim().isEmpty()) {
                                        isFormulaMethod2 = true;
                                        method2Count++;
                                        if (!isFormulaMethod1) {
                                            System.out.println("📐 METHOD 2 - Formula at " + cellRef + ": " + formula);
                                        }
                                    }
                                } catch (Exception e) {
                                    // Not a formula cell
                                }
                                
                                // Method 3: Check cell value for formula patterns
                                boolean isFormulaMethod3 = false;
                                try {
                                    String cellValue = cell.toString();
                                    if (cellValue != null && cellValue.trim().startsWith("=")) {
                                        isFormulaMethod3 = true;
                                        method3Count++;
                                        if (!isFormulaMethod1 && !isFormulaMethod2) {
                                            System.out.println("📐 METHOD 3 - Formula pattern at " + cellRef + ": " + cellValue);
                                        }
                                    }
                                } catch (Exception e) {
                                    // Ignore
                                }
                                
                                // Report any discrepancies
                                if (isFormulaMethod1 || isFormulaMethod2 || isFormulaMethod3) {
                                    if (!(isFormulaMethod1 && isFormulaMethod2)) {
                                        System.out.println("⚠️  DETECTION MISMATCH at " + cellRef + 
                                                         " - M1:" + isFormulaMethod1 + 
                                                         " M2:" + isFormulaMethod2 + 
                                                         " M3:" + isFormulaMethod3);
                                    }
                                }
                            }
                        }
                    }
                }

                System.out.println("\n" + "=".repeat(60));
                System.out.println("📊 DETECTION METHOD RESULTS:");
                System.out.println("   Cells checked: " + totalCellsChecked);
                System.out.println("   Method 1 (CellType.FORMULA): " + method1Count + " formulas");
                System.out.println("   Method 2 (getCellFormula()): " + method2Count + " formulas");
                System.out.println("   Method 3 (String pattern): " + method3Count + " formulas");
                
                if (method1Count == 0 && method2Count == 0 && method3Count == 0) {
                    System.out.println("   ✅ ALL METHODS CONFIRM: No formulas found");
                } else {
                    System.out.println("   ⚠️  FORMULAS DETECTED by one or more methods");
                }

            }

        } catch (Exception e) {
            System.err.println("❌ Error in enhanced formula detection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void searchForFormulas(String filePath) {
        try {
            File templateFile = new File(filePath);
            if (!templateFile.exists()) {
                System.out.println("❌ File not found: " + filePath);
                return;
            }

            System.out.println("📂 Searching for formulas in: " + filePath);

            try (FileInputStream fis = new FileInputStream(templateFile);
                 XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

                Sheet sheet = workbook.getSheet("Simulateur UVC");
                if (sheet == null) {
                    System.out.println("❌ Worksheet 'Simulateur UVC' not found");
                    return;
                }

                System.out.println("✅ Found worksheet: Simulateur UVC");
                System.out.println("📊 Last row number: " + sheet.getLastRowNum());
                System.out.println("🔍 Searching for formulas in ALL rows after row 10...\n");

                int totalFormulas = 0;
                int rowsScanned = 0;

                // Scan all rows from row 11 onwards
                for (int rowIndex = 10; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    if (row != null) {
                        rowsScanned++;
                        int formulasInRow = 0;

                        // Check all columns in this row
                        for (int colIndex = 0; colIndex < 100; colIndex++) { // Check up to column CV
                            Cell cell = row.getCell(colIndex);
                            if (cell != null && cell.getCellType() == CellType.FORMULA) {
                                String columnLetter = getColumnLetter(colIndex);
                                String cellRef = columnLetter + (rowIndex + 1);
                                
                                System.out.println("📐 FORMULA FOUND at " + cellRef + ": " + cell.getCellFormula());
                                
                                // Try to get the calculated value
                                try {
                                    Object calculatedValue = getCellValue(cell);
                                    System.out.println("   💡 Calculated value: " + calculatedValue);
                                } catch (Exception e) {
                                    System.out.println("   ⚠️ Could not calculate value: " + e.getMessage());
                                }
                                
                                formulasInRow++;
                                totalFormulas++;
                            }
                        }

                        // Report progress every 1000 rows
                        if (rowsScanned % 1000 == 0) {
                            System.out.println("📊 Progress: Scanned " + rowsScanned + " rows, found " + totalFormulas + " formulas so far...");
                        }
                    }
                }

                System.out.println("\n" + "=".repeat(60));
                System.out.println("📊 FINAL RESULTS:");
                System.out.println("   Rows scanned: " + rowsScanned);
                System.out.println("   Total formulas found: " + totalFormulas);
                
                if (totalFormulas == 0) {
                    System.out.println("   ✅ NO FORMULAS FOUND in any row after row 10");
                    System.out.println("   💡 This confirms extractFormulas() method returns empty");
                } else {
                    System.out.println("   ⚠️ FORMULAS DETECTED - extractFormulas() logic may need review");
                }

            }

        } catch (Exception e) {
            System.err.println("❌ Error searching for formulas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void inspectTemplate(String filePath) {
        try {
            File templateFile = new File(filePath);
            if (!templateFile.exists()) {
                System.out.println("❌ File not found: " + filePath);
                return;
            }

            System.out.println("📂 Inspecting: " + filePath);

            try (FileInputStream fis = new FileInputStream(templateFile);
                 XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

                Sheet sheet = workbook.getSheet("Simulateur UVC");
                if (sheet == null) {
                    System.out.println("❌ Worksheet 'Simulateur UVC' not found");
                    return;
                }

                System.out.println("✅ Found worksheet: Simulateur UVC");
                System.out.println("📊 Last row number: " + sheet.getLastRowNum());

                // Check row 10 (headers) and row 11 (first data row)
                inspectRow(sheet, 10, "Header Row");
                inspectRow(sheet, 11, "First Data Row");

            }

        } catch (Exception e) {
            System.err.println("❌ Error inspecting template: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void inspectRow(Sheet sheet, int rowIndex, String rowDescription) {
        System.out.println("\n🔍 " + rowDescription + " (Row " + (rowIndex + 1) + "):");
        
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            System.out.println("  ❌ Row is null/empty");
            return;
        }

        int formulaCount = 0;
        int styleCount = 0;
        int dataCount = 0;

        // Check first 50 columns (A to AX)
        for (int colIndex = 0; colIndex < 50; colIndex++) {
            Cell cell = row.getCell(colIndex);
            if (cell != null) {
                String columnLetter = getColumnLetter(colIndex);
                
                // Check for formulas
                if (cell.getCellType() == CellType.FORMULA) {
                    System.out.println("  📐 " + columnLetter + ": FORMULA = " + cell.getCellFormula());
                    formulaCount++;
                }
                
                // Check for styles
                CellStyle style = cell.getCellStyle();
                if (style != null && style.getIndex() != 0) {
                    System.out.println("  🎨 " + columnLetter + ": STYLE = " + style.getIndex() + 
                                     " (Format: " + style.getDataFormatString() + ")");
                    styleCount++;
                }
                
                // Check for data
                if (cell.getCellType() != CellType.BLANK) {
                    Object value = getCellValue(cell);
                    if (value != null && !value.toString().trim().isEmpty()) {
                        System.out.println("  📝 " + columnLetter + ": DATA = " + value + 
                                         " (Type: " + cell.getCellType() + ")");
                        dataCount++;
                    }
                }
            }
        }

        System.out.println("  📊 Summary: " + formulaCount + " formulas, " + 
                          styleCount + " styled cells, " + dataCount + " data cells");
    }

    private static String getColumnLetter(int columnIndex) {
        if (columnIndex < 26) {
            return String.valueOf((char) ('A' + columnIndex));
        } else {
            int firstLetter = (columnIndex / 26) - 1;
            int secondLetter = columnIndex % 26;
            return String.valueOf((char) ('A' + firstLetter)) + String.valueOf((char) ('A' + secondLetter));
        }
    }

    private static Object getCellValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    double numValue = cell.getNumericCellValue();
                    return numValue == Math.floor(numValue) ? (long) numValue : numValue;
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                try {
                    switch (cell.getCachedFormulaResultType()) {
                        case STRING:
                            return cell.getStringCellValue();
                        case NUMERIC:
                            double numValue = cell.getNumericCellValue();
                            return numValue == Math.floor(numValue) ? (long) numValue : numValue;
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
