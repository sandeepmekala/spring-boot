package com.excel;

import org.apache.poi.openxml4j.opc.*;
import javax.xml.stream.*;
import java.io.*;
import java.util.*;

/**
 * Simplified Excel writer for Simulateur UVC processing
 */
public class OptimizedExcelWriter {

    private static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newInstance();
    private static final XMLOutputFactory XML_OUTPUT_FACTORY = XMLOutputFactory.newInstance();
    private static final int BUFFER_SIZE = 8192;

    /**
     * Main method to fill Excel template with data
     */
    public static void fillExcelTemplate(File template, File output, List<Map<String, Object>> data,
            String worksheetName, int headerRowNumber) throws Exception {

        // Copy template to preserve original
        copyTemplate(template, output);

        // Process the Excel file
        try (OPCPackage pkg = OPCPackage.open(output, PackageAccess.READ_WRITE)) {
            PackagePart sheetPart = findWorksheet(pkg, worksheetName);
            if (sheetPart == null) {
                throw new RuntimeException("Worksheet '" + worksheetName + "' not found!");
            }

            processSheet(sheetPart, data, headerRowNumber);
            removeCalculationChain(pkg);
        }
    }

    /**
     * Copy template file to output location
     */
    private static void copyTemplate(File template, File output) throws IOException {
        System.out.println("📋 Copying template to preserve original: " + template.getName() + " -> " + output.getName());

        File outputDir = output.getParentFile();
        if (outputDir != null && !outputDir.exists()) {
            outputDir.mkdirs();
        }

        try (InputStream in = new FileInputStream(template);
             OutputStream out = new FileOutputStream(output)) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        System.out.println("✅ Template copied successfully. Original template preserved.");
    }

    /**
     * Find worksheet by name
     */
    private static PackagePart findWorksheet(OPCPackage pkg, String worksheetName) throws Exception {
        Map<String, PackagePart> worksheetMap = getWorksheetMapping(pkg);

        PackagePart targetPart = worksheetMap.get(worksheetName);
        if (targetPart != null) {
            System.out.println("✅ Found worksheet '" + worksheetName + "' at: " + targetPart.getPartName().getName());
        } else {
            System.out.println("❌ Worksheet '" + worksheetName + "' not found!");
            System.out.println("Available worksheets: " + worksheetMap.keySet());
        }

        return targetPart;
    }

    /**
     * Create mapping from worksheet names to their parts
     */
    private static Map<String, PackagePart> getWorksheetMapping(OPCPackage pkg) throws Exception {
        Map<String, PackagePart> mapping = new LinkedHashMap<>();

        List<String> worksheetNames = getWorksheetNames(pkg);
        List<PackagePart> worksheetParts = getWorksheetParts(pkg);

        System.out.println("🔍 Mapping worksheets:");
        System.out.println("  Worksheet names from workbook.xml: " + worksheetNames);
        System.out.println("  Available worksheet files: " + worksheetParts.size());

        int minSize = Math.min(worksheetNames.size(), worksheetParts.size());
        for (int i = 0; i < minSize; i++) {
            String name = worksheetNames.get(i);
            PackagePart part = worksheetParts.get(i);
            mapping.put(name, part);
            System.out.println("  ✅ " + name + " -> " + part.getPartName().getName());
        }

        return mapping;
    }

    /**
     * Get worksheet names from workbook.xml
     */
    private static List<String> getWorksheetNames(OPCPackage pkg) throws Exception {
        List<String> names = new ArrayList<>();

        PackagePart workbookPart = null;
        for (PackagePart part : pkg.getParts()) {
            if ("/xl/workbook.xml".equals(part.getPartName().getName())) {
                workbookPart = part;
                break;
            }
        }

        if (workbookPart == null) {
            throw new RuntimeException("workbook.xml not found!");
        }

        try (InputStream stream = workbookPart.getInputStream()) {
            XMLStreamReader reader = XML_INPUT_FACTORY.createXMLStreamReader(stream);

            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT && "sheet".equals(reader.getLocalName())) {
                    String name = reader.getAttributeValue(null, "name");
                    if (name != null) {
                        names.add(name);
                    }
                }
            }
            reader.close();
        }

        return names;
    }

    /**
     * Get worksheet parts in order
     */
    private static List<PackagePart> getWorksheetParts(OPCPackage pkg) {
        List<PackagePart> parts = new ArrayList<>();
        Map<Integer, PackagePart> sortedParts = new TreeMap<>();

        try {
            for (PackagePart part : pkg.getParts()) {
                String partName = part.getPartName().getName();
                if (partName.startsWith("/xl/worksheets/sheet") && partName.endsWith(".xml")
                        && !partName.contains("_rels")) {

                    String fileName = partName.substring(partName.lastIndexOf('/') + 1);
                    String numberStr = fileName.replace("sheet", "").replace(".xml", "");
                    try {
                        int sheetNumber = Integer.parseInt(numberStr);
                        sortedParts.put(sheetNumber, part);
                    } catch (NumberFormatException e) {
                        System.out.println("⚠️ Could not parse sheet number from: " + partName);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error getting worksheet parts: " + e.getMessage());
        }

        parts.addAll(sortedParts.values());
        return parts;
    }

    /**
     * Process the worksheet with data
     */
    private static void processSheet(PackagePart sheetPart, List<Map<String, Object>> data, int headerRowNumber) throws Exception {
        byte[] sheetBytes = readSheetBytes(sheetPart);

        Map<String, String> formulaMap = extractFormulas(sheetBytes, headerRowNumber);
        Map<String, String> styleMap = extractCellStyles(sheetBytes, headerRowNumber);

        ByteArrayOutputStream updatedSheet = new ByteArrayOutputStream();
        writeSheetData(sheetBytes, updatedSheet, data, formulaMap, styleMap, headerRowNumber);

        writeSheetBytes(sheetPart, updatedSheet.toByteArray());
    }

    /**
     * Extract formulas and styles from the first data row
     */
    private static Map<String, String> extractFormulas(byte[] sheetBytes, int headerRowNumber) throws Exception {
        Map<String, String> formulaMap = new HashMap<>();
        int firstDataRowNumber = headerRowNumber + 1;
        String firstDataRowStr = String.valueOf(firstDataRowNumber);

        try (InputStream sheetIn = new ByteArrayInputStream(sheetBytes)) {
            XMLStreamReader reader = XML_INPUT_FACTORY.createXMLStreamReader(sheetIn);

            String currentCellRef = null;
            boolean inFirstDataRow = false;

            while (reader.hasNext()) {
                int event = reader.next();

                if (event == XMLStreamConstants.START_ELEMENT) {
                    String localName = reader.getLocalName();

                    if ("row".equals(localName)) {
                        String rowNum = reader.getAttributeValue(null, "r");
                        if (firstDataRowStr.equals(rowNum)) {
                            inFirstDataRow = true;
                        } else if (inFirstDataRow) {
                            System.out.println("DEBUG: Finished processing first data row, stopping formula extraction");
                            break;
                        }
                    } else if ("c".equals(localName) && inFirstDataRow) {
                        currentCellRef = reader.getAttributeValue(null, "r");
                    } else if ("f".equals(localName) && currentCellRef != null && inFirstDataRow) {
                        String formula = reader.getElementText();
                        if (formula != null && !formula.trim().isEmpty()) {
                            String column = currentCellRef.replaceAll("\\d+", "");
                            formulaMap.put(column, formula);
                            System.out.println("DEBUG: Extracted formula from column " + column + ": " + formula);
                        }
                    }
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    if ("c".equals(reader.getLocalName())) {
                        currentCellRef = null;
                    } else if ("row".equals(reader.getLocalName()) && inFirstDataRow) {
                        inFirstDataRow = false;
                    }
                }
            }
            reader.close();
        }

        System.out.println("DEBUG: Total formulas extracted from first data row: " + formulaMap.size());
        return formulaMap;
    }

    /**
     * Extract cell styles from the first data row to preserve formatting
     */
    private static Map<String, String> extractCellStyles(byte[] sheetBytes, int headerRowNumber) throws Exception {
        Map<String, String> styleMap = new HashMap<>();
        int firstDataRowNumber = headerRowNumber + 1;
        String firstDataRowStr = String.valueOf(firstDataRowNumber);

        try (InputStream sheetIn = new ByteArrayInputStream(sheetBytes)) {
            XMLStreamReader reader = XML_INPUT_FACTORY.createXMLStreamReader(sheetIn);

            boolean inFirstDataRow = false;

            while (reader.hasNext()) {
                int event = reader.next();

                if (event == XMLStreamConstants.START_ELEMENT) {
                    String localName = reader.getLocalName();

                    if ("row".equals(localName)) {
                        String rowNum = reader.getAttributeValue(null, "r");
                        if (firstDataRowStr.equals(rowNum)) {
                            inFirstDataRow = true;
                        } else if (inFirstDataRow) {
                            System.out.println("DEBUG: Finished extracting cell styles from first data row");
                            break;
                        }
                    } else if ("c".equals(localName) && inFirstDataRow) {
                        String cellRef = reader.getAttributeValue(null, "r");
                        String styleIndex = reader.getAttributeValue(null, "s");

                        if (cellRef != null && styleIndex != null) {
                            String column = cellRef.replaceAll("\\d+", "");
                            styleMap.put(column, styleIndex);
                            System.out.println("DEBUG: Extracted style for column " + column + ": s=\"" + styleIndex + "\"");
                        }
                    }
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    if ("row".equals(reader.getLocalName()) && inFirstDataRow) {
                        inFirstDataRow = false;
                    }
                }
            }
            reader.close();
        }

        System.out.println("DEBUG: Total cell styles extracted: " + styleMap.size());
        return styleMap;
    }

    /**
     * Write sheet data with template preservation
     */
    private static void writeSheetData(byte[] sheetXmlBytes, OutputStream sheetOut,
            List<Map<String, Object>> data, Map<String, String> formulaMap, Map<String, String> styleMap, int headerRowNumber) throws Exception {

        try (InputStream sheetIn = new ByteArrayInputStream(sheetXmlBytes)) {
            XMLStreamReader reader = XML_INPUT_FACTORY.createXMLStreamReader(sheetIn);
            XMLStreamWriter writer = XML_OUTPUT_FACTORY.createXMLStreamWriter(sheetOut, "UTF-8");

            boolean inSheetData = false;

            while (reader.hasNext()) {
                int event = reader.next();

                if (event == XMLStreamConstants.START_ELEMENT) {
                    String localName = reader.getLocalName();

                    if ("sheetData".equals(localName)) {
                        inSheetData = true;
                        copyElement(writer, reader);
                        writeCompleteSheetData(writer, sheetXmlBytes, data, formulaMap, styleMap, headerRowNumber); 
                        skipToEndElement(reader, "sheetData");
                        writer.writeEndElement();
                        inSheetData = false;
                    } else if (!inSheetData) {
                        copyElement(writer, reader);
                    }
                } else if (event == XMLStreamConstants.END_ELEMENT && !inSheetData) {
                    writer.writeEndElement();
                } else if (!inSheetData) {
                    copyEventContent(writer, reader, event);
                }
            }

            writer.flush();
            writer.close();
            reader.close();
        }
    }

    /**
     * Write complete sheet data including template rows and new data
     */
    private static void writeCompleteSheetData(XMLStreamWriter writer, byte[] sheetBytes,
            List<Map<String, Object>> data, Map<String, String> formulaMap, Map<String, String> styleMap, int headerRowNumber) throws Exception {

        // Copy template rows up to and including header
        copyTemplateRows(writer, sheetBytes, headerRowNumber);

        // Write data rows starting after header
        writeDataRows(writer, data, formulaMap, styleMap, headerRowNumber + 1);
    }

    /**
     * Copy template rows up to and including header row
     */
    private static void copyTemplateRows(XMLStreamWriter writer, byte[] sheetBytes, int headerRowNumber) throws Exception {
        try (InputStream sheetIn = new ByteArrayInputStream(sheetBytes)) {
            XMLStreamReader reader = XML_INPUT_FACTORY.createXMLStreamReader(sheetIn);

            boolean inSheetData = false;
            boolean foundAllRows = false;

            while (reader.hasNext() && !foundAllRows) {
                int event = reader.next();

                if (event == XMLStreamConstants.START_ELEMENT) {
                    String localName = reader.getLocalName();

                    if ("sheetData".equals(localName)) {
                        inSheetData = true;
                    } else if ("row".equals(localName) && inSheetData) {
                        String rowNumStr = reader.getAttributeValue(null, "r");
                        if (rowNumStr != null) {
                            int rowNum = Integer.parseInt(rowNumStr);

                            if (rowNum <= headerRowNumber) {
                                copyCompleteRow(writer, reader);
                                if (rowNum == headerRowNumber) {
                                    foundAllRows = true;
                                }
                            } else {
                                foundAllRows = true;
                            }
                        }
                    }
                } else if (event == XMLStreamConstants.END_ELEMENT && "sheetData".equals(reader.getLocalName())) {
                    foundAllRows = true;
                }
            }
            reader.close();
        }
    }

    /**
     * Write data rows with formulas and preserved cell styles
     */
    private static void writeDataRows(XMLStreamWriter writer, List<Map<String, Object>> data,
            Map<String, String> formulaMap, Map<String, String> styleMap, int startRow) throws XMLStreamException {

        int rowNum = startRow;

        // Get all columns from data and formulas
        Set<String> allColumns = new LinkedHashSet<>();
        if (!data.isEmpty()) {
            allColumns.addAll(data.get(0).keySet());
        }
        allColumns.addAll(formulaMap.keySet());

        List<String> columnOrder = new ArrayList<>(allColumns);

        for (Map<String, Object> row : data) {
            writer.writeStartElement("row");
            writer.writeAttribute("r", String.valueOf(rowNum));

            for (String columnName : columnOrder) {
                String cellRef = columnName + rowNum;

                writer.writeStartElement("c");
                writer.writeAttribute("r", cellRef);

                // Apply original cell style to preserve formatting (like Text format for column D)
                String styleIndex = styleMap.get(columnName);
                if (styleIndex != null) {
                    writer.writeAttribute("s", styleIndex);
                    System.out.println("DEBUG: Applied style s=\"" + styleIndex + "\" to cell " + cellRef);
                }

                // Check for formula first
                String formula = getFormulaForCell(formulaMap, columnName, rowNum);
                if (formula != null) {
                    writer.writeStartElement("f");
                    writer.writeCharacters(formula);
                    writer.writeEndElement();
                } else if (row.containsKey(columnName)) {
                    Object value = row.get(columnName);

                    if (value instanceof String) {
                        writer.writeAttribute("t", "str");
                        writer.writeStartElement("v");
                        writer.writeCharacters(value.toString());
                        writer.writeEndElement();
                    } else if (value != null) {
                        writer.writeStartElement("v");
                        writer.writeCharacters(value.toString());
                        writer.writeEndElement();
                    }
                }

                writer.writeEndElement();
            }

            writer.writeEndElement();
            rowNum++;
        }
    }

    /**
     * Helper methods
     */
    private static byte[] readSheetBytes(PackagePart sheetPart) throws IOException {
        try (InputStream in = sheetPart.getInputStream()) {
            return FileHandler.readBytes(in);
        }
    }

    private static void writeSheetBytes(PackagePart sheetPart, byte[] data) throws IOException {
        try (OutputStream out = sheetPart.getOutputStream()) {
            FileHandler.writeBytes(data, out);
        }
    }

    private static void removeCalculationChain(OPCPackage pkg) {
        try {
            for (PackagePart part : pkg.getParts()) {
                if ("/xl/calcChain.xml".equals(part.getPartName().getName())) {
                    pkg.removePart(part);
                    break;
                }
            }
        } catch (Exception ignored) {
            // Optional operation - ignore errors
        }
    }

    private static void copyCompleteRow(XMLStreamWriter writer, XMLStreamReader reader) throws XMLStreamException {
        copyElement(writer, reader);

        int depth = 1;
        while (reader.hasNext() && depth > 0) {
            int event = reader.next();

            if (event == XMLStreamConstants.START_ELEMENT) {
                depth++;
                copyElement(writer, reader);
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                writer.writeEndElement();
                depth--;
            } else if (event == XMLStreamConstants.CHARACTERS) {
                writer.writeCharacters(reader.getText());
            } else if (event == XMLStreamConstants.CDATA) {
                writer.writeCData(reader.getText());
            }
        }
    }

    private static void skipToEndElement(XMLStreamReader reader, String elementName) throws XMLStreamException {
        int depth = 1;
        while (reader.hasNext() && depth > 0) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                depth++;
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                depth--;
                if (depth == 0 && elementName.equals(reader.getLocalName())) {
                    break;
                }
            }
        }
    }

    private static void copyElement(XMLStreamWriter writer, XMLStreamReader reader) throws XMLStreamException {
        String localName = reader.getLocalName();
        String namespaceURI = reader.getNamespaceURI();
        String prefix = reader.getPrefix();

        if (namespaceURI != null && !namespaceURI.isEmpty()) {
            writer.writeStartElement(prefix != null ? prefix : "", localName, namespaceURI);
        } else {
            writer.writeStartElement(localName);
        }

        // Copy namespaces
        for (int i = 0; i < reader.getNamespaceCount(); i++) {
            writer.writeNamespace(reader.getNamespacePrefix(i) != null ? reader.getNamespacePrefix(i) : "",
                    reader.getNamespaceURI(i));
        }

        // Copy attributes
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attrNamespace = reader.getAttributeNamespace(i);
            if (attrNamespace != null && !attrNamespace.isEmpty()) {
                writer.writeAttribute(attrNamespace, reader.getAttributeLocalName(i), reader.getAttributeValue(i));
            } else {
                writer.writeAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
            }
        }
    }

    private static void copyEventContent(XMLStreamWriter writer, XMLStreamReader reader, int event)
            throws XMLStreamException {
        switch (event) {
            case XMLStreamConstants.CHARACTERS:
                writer.writeCharacters(reader.getText());
                break;
            case XMLStreamConstants.CDATA:
                writer.writeCData(reader.getText());
                break;
            case XMLStreamConstants.START_DOCUMENT:
                writer.writeStartDocument("UTF-8", "1.0");
                break;
            case XMLStreamConstants.END_DOCUMENT:
                writer.writeEndDocument();
                break;
        }
    }

    private static String getFormulaForCell(Map<String, String> formulaMap, String column, int row) {
        String formula = formulaMap.get(column);
        if (formula != null) {
            return formula.replaceAll("\\b([A-Z]+)(\\d+)\\b", "$1" + row);
        }
        return null;
    }
}
