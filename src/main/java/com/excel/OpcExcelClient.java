package com.excel;

import java.io.*;
import java.util.*;

/**
 * Optimized Excel client for Simulateur UVC processing
 * Now loads data from JSON instead of reading directly from Excel
 */
public class OpcExcelClient {

    private static final String JSON_DATA_FILE = "simulateur_uvc_data.json";

    public static void main(String[] args) throws Exception {
        System.out.println("=== Simulateur UVC Excel Client (Optimized) ===");
        processSimulateurUVC();
    }

    /**
     * Main method to process Simulateur UVC Excel files using JSON data
     */
    private static void processSimulateurUVC() throws Exception {
        File template = new File("src/main/resources/gov_template.xlsm");
        File output = FileHandler.getOutputFile("simulateur_uvc_output.xlsm");

        System.out.println("🔄 Loading data from JSON file: " + JSON_DATA_FILE);

        // Load data from JSON instead of reading from Excel
        List<Map<String, Object>> data = loadDataFromJson();
        
        System.out.println("📊 Processing " + data.size() + " rows for Simulateur UVC worksheet");

        long startTime = System.currentTimeMillis();

        // Process the Excel file with JSON data
        OptimizedExcelWriter.fillExcelTemplate(template, output, data, "Simulateur UVC", 10);

        long processingTime = System.currentTimeMillis() - startTime;

        System.out.println("✅ Finished processing to: " + output.getPath());
        System.out.println("⏱️ Processing time: " + processingTime + "ms");
        System.out.println("📊 File size: " + (output.length() / 1024) + " KB");
    }

    /**
     * Load data from JSON file (simplified version for OpcExcelClient)
     */
    private static List<Map<String, Object>> loadDataFromJson() throws IOException {
        File jsonFile = FileHandler.getOutputFile(JSON_DATA_FILE);
        
        if (!jsonFile.exists()) {
            throw new FileNotFoundException("JSON data file not found: " + jsonFile.getAbsolutePath() + 
                "\nPlease run DataPreparationClient.extractTemplateDataToJson() first to generate the JSON file.");
        }

        System.out.println("📂 Reading JSON file: " + jsonFile.getAbsolutePath());

        // Read JSON file content
        String jsonContent;
        try (FileInputStream fis = new FileInputStream(jsonFile)) {
            byte[] jsonBytes = FileHandler.readBytes(fis);
            jsonContent = new String(jsonBytes, "UTF-8");
        }

        // Parse JSON manually to extract data array
        return parseJsonData(jsonContent);
    }

    /**
     * Manual JSON parsing for data section (simplified for OpcExcelClient)
     */
    private static List<Map<String, Object>> parseJsonData(String jsonContent) {
        List<Map<String, Object>> data = new ArrayList<>();
        
        try {
            // Find the "data" array in JSON
            int dataStart = jsonContent.indexOf("\"data\": [");
            if (dataStart == -1) {
                throw new RuntimeException("Could not find 'data' array in JSON");
            }
            
            // Find the start of the array content
            int arrayStart = jsonContent.indexOf('[', dataStart);
            int arrayEnd = findMatchingBracket(jsonContent, arrayStart);
            
            if (arrayStart == -1 || arrayEnd == -1) {
                throw new RuntimeException("Could not parse data array bounds");
            }
            
            // Extract array content (without outer brackets)
            String arrayContent = jsonContent.substring(arrayStart + 1, arrayEnd).trim();
            
            if (arrayContent.isEmpty()) {
                return data; // Empty array
            }
            
            // Split by objects (look for },{ pattern)
            String[] objects = splitJsonObjects(arrayContent);
            
            for (String objectStr : objects) {
                objectStr = objectStr.trim();
                if (objectStr.startsWith("{")) {
                    objectStr = objectStr.substring(1);
                }
                if (objectStr.endsWith("}")) {
                    objectStr = objectStr.substring(0, objectStr.length() - 1);
                }
                
                Map<String, Object> rowData = parseJsonObject(objectStr);
                if (!rowData.isEmpty()) {
                    data.add(rowData);
                }
            }
            
            System.out.println("📖 Successfully parsed " + data.size() + " rows from JSON");
            
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON data: " + e.getMessage(), e);
        }
        
        return data;
    }

    /**
     * Find matching closing bracket
     */
    private static int findMatchingBracket(String json, int startPos) {
        int depth = 0;
        for (int i = startPos; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '[') depth++;
            else if (c == ']') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    /**
     * Split JSON array content into individual objects
     */
    private static String[] splitJsonObjects(String arrayContent) {
        List<String> objects = new ArrayList<>();
        int depth = 0;
        int start = 0;
        
        for (int i = 0; i < arrayContent.length(); i++) {
            char c = arrayContent.charAt(i);
            
            if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    // Found complete object
                    String obj = arrayContent.substring(start, i + 1).trim();
                    if (!obj.isEmpty()) {
                        objects.add(obj);
                    }
                    
                    // Skip comma and whitespace
                    while (i + 1 < arrayContent.length() && 
                           (arrayContent.charAt(i + 1) == ',' || Character.isWhitespace(arrayContent.charAt(i + 1)))) {
                        i++;
                    }
                    start = i + 1;
                }
            }
        }
        
        return objects.toArray(new String[0]);
    }

    /**
     * Parse individual JSON object into Map
     */
    private static Map<String, Object> parseJsonObject(String objectContent) {
        Map<String, Object> map = new LinkedHashMap<>();
        
        // Split by commas, but be careful about commas inside strings
        String[] pairs = splitJsonPairs(objectContent);
        
        for (String pair : pairs) {
            pair = pair.trim();
            if (pair.isEmpty()) continue;
            
            // Find the colon separator
            int colonPos = findColonSeparator(pair);
            if (colonPos == -1) continue;
            
            String key = pair.substring(0, colonPos).trim();
            String value = pair.substring(colonPos + 1).trim();
            
            // Remove quotes from key
            if (key.startsWith("\"") && key.endsWith("\"")) {
                key = key.substring(1, key.length() - 1);
            }
            
            // Parse value
            Object parsedValue = parseJsonValue(value);
            map.put(key, parsedValue);
        }
        
        return map;
    }

    /**
     * Split JSON object content into key-value pairs
     */
    private static String[] splitJsonPairs(String objectContent) {
        List<String> pairs = new ArrayList<>();
        boolean inString = false;
        int start = 0;
        
        for (int i = 0; i < objectContent.length(); i++) {
            char c = objectContent.charAt(i);
            
            if (c == '"' && (i == 0 || objectContent.charAt(i - 1) != '\\')) {
                inString = !inString;
            } else if (c == ',' && !inString) {
                String pair = objectContent.substring(start, i).trim();
                if (!pair.isEmpty()) {
                    pairs.add(pair);
                }
                start = i + 1;
            }
        }
        
        // Add the last pair
        String lastPair = objectContent.substring(start).trim();
        if (!lastPair.isEmpty()) {
            pairs.add(lastPair);
        }
        
        return pairs.toArray(new String[0]);
    }

    /**
     * Find colon separator outside of strings
     */
    private static int findColonSeparator(String pair) {
        boolean inString = false;
        
        for (int i = 0; i < pair.length(); i++) {
            char c = pair.charAt(i);
            
            if (c == '"' && (i == 0 || pair.charAt(i - 1) != '\\')) {
                inString = !inString;
            } else if (c == ':' && !inString) {
                return i;
            }
        }
        
        return -1;
    }

    /**
     * Parse JSON value (string, number, boolean, null)
     */
    private static Object parseJsonValue(String value) {
        value = value.trim();
        
        if (value.equals("null")) {
            return null;
        } else if (value.equals("true")) {
            return true;
        } else if (value.equals("false")) {
            return false;
        } else if (value.startsWith("\"") && value.endsWith("\"")) {
            // String value - remove quotes and handle escape sequences
            String str = value.substring(1, value.length() - 1);
            return str.replace("\\\"", "\"")
                     .replace("\\\\", "\\")
                     .replace("\\n", "\n")
                     .replace("\\r", "\r")
                     .replace("\\t", "\t");
        } else {
            // Try to parse as number
            try {
                if (value.contains(".") || value.toLowerCase().contains("e")) {
                    return Double.parseDouble(value);
                } else {
                    long longValue = Long.parseLong(value);
                    // Return as Integer if it fits, otherwise Long
                    if (longValue >= Integer.MIN_VALUE && longValue <= Integer.MAX_VALUE) {
                        return (int) longValue;
                    } else {
                        return longValue;
                    }
                }
            } catch (NumberFormatException e) {
                // If not a number, return as string
                return value;
            }
        }
    }
}
