package com.excel;

import java.io.*;
import java.util.*;

/**
 * Test client to compare SimplifiedExcelWriter vs OptimizedExcelWriter
 */
public class SimplifiedExcelTest {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Simplified Excel Writer Test ===");
        
        // Load JSON data using simple method
        List<Map<String, Object>> data = loadTestData();
        System.out.println("📊 Loaded " + data.size() + " rows from JSON");
        
        File template = new File("src/main/resources/gov_template.xlsm");
        
        // Test 1: Original OptimizedExcelWriter
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🔧 Testing ORIGINAL OptimizedExcelWriter");
        System.out.println("=".repeat(60));
        
        File originalOutput = new File("src/main/resources/original_output.xlsx");
        long startTime = System.currentTimeMillis();
        
        OptimizedExcelWriter.fillExcelTemplate(template, originalOutput, data, "Simulateur UVC", 10);
        
        long originalTime = System.currentTimeMillis() - startTime;
        System.out.println("⏱️ Original writer time: " + originalTime + " ms");
        System.out.println("📁 Original output: " + originalOutput.getName() + " (" + originalOutput.length() + " bytes)");
        
        // Test 2: Simplified ExcelWriter
        System.out.println("\n" + "=".repeat(60));
        System.out.println("⚡ Testing SIMPLIFIED ExcelWriter");
        System.out.println("=".repeat(60));
        
        File simplifiedOutput = new File("src/main/resources/simplified_output.xlsx");
        startTime = System.currentTimeMillis();
        
        SimplifiedExcelWriter.fillExcelTemplate(template, simplifiedOutput, data, "Simulateur UVC", 10);
        
        long simplifiedTime = System.currentTimeMillis() - startTime;
        System.out.println("⏱️ Simplified writer time: " + simplifiedTime + " ms");
        System.out.println("📁 Simplified output: " + simplifiedOutput.getName() + " (" + simplifiedOutput.length() + " bytes)");
        
        // Performance comparison
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📊 PERFORMANCE COMPARISON");
        System.out.println("=".repeat(60));
        
        System.out.println("Original writer:   " + originalTime + " ms");
        System.out.println("Simplified writer: " + simplifiedTime + " ms");
        
        if (simplifiedTime < originalTime) {
            double improvement = ((double)(originalTime - simplifiedTime) / originalTime) * 100;
            System.out.println("🚀 Simplified is " + String.format("%.1f", improvement) + "% FASTER!");
        } else if (simplifiedTime > originalTime) {
            double slower = ((double)(simplifiedTime - originalTime) / originalTime) * 100;
            System.out.println("⚠️ Simplified is " + String.format("%.1f", slower) + "% slower");
        } else {
            System.out.println("⚖️ Both writers have similar performance");
        }
        
        // File size comparison
        System.out.println("\nFile sizes:");
        System.out.println("Original:   " + originalOutput.length() + " bytes");
        System.out.println("Simplified: " + simplifiedOutput.length() + " bytes");
        
        long sizeDiff = originalOutput.length() - simplifiedOutput.length();
        if (sizeDiff > 0) {
            System.out.println("📉 Simplified file is " + sizeDiff + " bytes smaller");
        } else if (sizeDiff < 0) {
            System.out.println("📈 Simplified file is " + Math.abs(sizeDiff) + " bytes larger");
        } else {
            System.out.println("📏 Both files are the same size");
        }
        
        System.out.println("\n✅ Test completed! Check the output files:");
        System.out.println("   - " + originalOutput.getAbsolutePath());
        System.out.println("   - " + simplifiedOutput.getAbsolutePath());
        
        System.out.println("\n💡 Open both files in Excel to compare formatting and data integrity");
    }

    /**
     * Simple method to load test data from JSON file
     */
    private static List<Map<String, Object>> loadTestData() throws IOException {
        File jsonFile = new File("src/main/resources/simulateur_uvc_data.json");
        
        if (!jsonFile.exists()) {
            throw new FileNotFoundException("JSON data file not found: " + jsonFile.getAbsolutePath());
        }

        // Read JSON file content
        String jsonContent;
        try (FileInputStream fis = new FileInputStream(jsonFile)) {
            byte[] jsonBytes = FileHandler.readBytes(fis);
            jsonContent = new String(jsonBytes, "UTF-8");
        }

        // Simple test data - create a few sample rows
        List<Map<String, Object>> data = new ArrayList<>();
        
        // Sample row 1
        Map<String, Object> row1 = new LinkedHashMap<>();
        row1.put("B", "TEST_PRODUCT_001");
        row1.put("C", "Test Product Name 1");
        row1.put("D", "123456");
        row1.put("F", 10.5);
        row1.put("G", 2.0);
        row1.put("I", 25.3);
        row1.put("J", 30.0);
        row1.put("L", 500.0);
        row1.put("W", 15.0);
        row1.put("AD", "Oui");
        row1.put("AU", "Test Category 1");
        row1.put("AX", 50000.0);
        data.add(row1);
        
        // Sample row 2
        Map<String, Object> row2 = new LinkedHashMap<>();
        row2.put("B", "TEST_PRODUCT_002");
        row2.put("C", "Test Product Name 2");
        row2.put("D", "789012");
        row2.put("F", 5.2);
        row2.put("G", 1.5);
        row2.put("I", 12.8);
        row2.put("J", 18.0);
        row2.put("L", 250.0);
        row2.put("W", 8.0);
        row2.put("AD", "Non");
        row2.put("AU", "Test Category 2");
        row2.put("AX", 25000.0);
        data.add(row2);
        
        // Sample row 3
        Map<String, Object> row3 = new LinkedHashMap<>();
        row3.put("B", "TEST_PRODUCT_003");
        row3.put("C", "Test Product Name 3");
        row3.put("D", "345678");
        row3.put("F", 8.7);
        row3.put("G", 3.2);
        row3.put("I", 35.1);
        row3.put("J", 42.5);
        row3.put("L", 750.0);
        row3.put("W", 22.0);
        row3.put("AD", "Oui");
        row3.put("AU", "Test Category 3");
        row3.put("AX", 75000.0);
        data.add(row3);

        System.out.println("📋 Created " + data.size() + " test data rows");
        return data;
    }
}
