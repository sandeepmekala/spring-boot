package com.excel;

import org.apache.poi.openxml4j.opc.*;
import javax.xml.stream.*;
import java.io.*;
import java.util.*;

/**
 * Ultra-simplified Excel writer - no style/formula processing
 * Lets Excel handle formatting automatically
 */
public class SimplifiedExcelWriter {

    private static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newInstance();
    private static final XMLOutputFactory XML_OUTPUT_FACTORY = XMLOutputFactory.newInstance();
    private static final int BUFFER_SIZE = 8192;

    /**
     * Simple method to fill Excel template with data
     */
    public static void fillExcelTemplate(File template, File output, List<Map<String, Object>> data,
            String worksheetName, int headerRowNumber) throws Exception {

        System.out.println("📋 Using simplified Excel writer (no style processing)");
        
        // Copy template to preserve original
        copyTemplate(template, output);

        // Process the Excel file with simple data writing
        try (OPCPackage pkg = OPCPackage.open(output, PackageAccess.READ_WRITE)) {
            PackagePart sheetPart = findWorksheet(pkg, worksheetName);
            if (sheetPart == null) {
                throw new RuntimeException("Worksheet '" + worksheetName + "' not found!");
            }

            writeSimpleData(sheetPart, data, headerRowNumber);
            removeCalculationChain(pkg);
        }
    }

    /**
     * Copy template file to output location
     */
    private static void copyTemplate(File template, File output) throws IOException {
        System.out.println("📋 Copying template: " + template.getName() + " -> " + output.getName());

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

        System.out.println("✅ Template copied successfully");
    }

    /**
     * Find worksheet by name (simplified)
     */
    private static PackagePart findWorksheet(OPCPackage pkg, String worksheetName) throws Exception {
        // Get worksheet names
        List<String> worksheetNames = getWorksheetNames(pkg);
        List<PackagePart> worksheetParts = getWorksheetParts(pkg);

        System.out.println("🔍 Looking for worksheet: " + worksheetName);
        System.out.println("📋 Available worksheets: " + worksheetNames);

        int minSize = Math.min(worksheetNames.size(), worksheetParts.size());
        for (int i = 0; i < minSize; i++) {
            if (worksheetName.equals(worksheetNames.get(i))) {
                System.out.println("✅ Found worksheet: " + worksheetName);
                return worksheetParts.get(i);
            }
        }

        return null;
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
                        // Skip invalid sheet names
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
     * Write data to worksheet (ultra-simplified)
     */
    private static void writeSimpleData(PackagePart sheetPart, List<Map<String, Object>> data, int headerRowNumber) throws Exception {
        byte[] sheetBytes = readSheetBytes(sheetPart);

        ByteArrayOutputStream updatedSheet = new ByteArrayOutputStream();
        writeSheetWithData(sheetBytes, updatedSheet, data, headerRowNumber);

        writeSheetBytes(sheetPart, updatedSheet.toByteArray());
    }

    /**
     * Write sheet data (simplified XML processing)
     */
    private static void writeSheetWithData(byte[] sheetXmlBytes, OutputStream sheetOut,
            List<Map<String, Object>> data, int headerRowNumber) throws Exception {

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
                        
                        // Copy template rows up to header
                        copyTemplateRows(writer, sheetXmlBytes, headerRowNumber);
                        
                        // Write simple data rows
                        writeSimpleDataRows(writer, data, headerRowNumber + 1);
                        
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
     * Write data rows (ultra-simplified - no styles, no formulas)
     */
    private static void writeSimpleDataRows(XMLStreamWriter writer, List<Map<String, Object>> data, int startRow) throws XMLStreamException {
        System.out.println("📝 Writing " + data.size() + " data rows (simplified mode)");
        
        int rowNum = startRow;

        for (Map<String, Object> row : data) {
            writer.writeStartElement("row");
            writer.writeAttribute("r", String.valueOf(rowNum));

            // Get all columns from data
            List<String> columns = new ArrayList<>(row.keySet());
            
            for (String columnName : columns) {
                Object value = row.get(columnName);
                if (value != null) {
                    String cellRef = columnName + rowNum;

                    writer.writeStartElement("c");
                    writer.writeAttribute("r", cellRef);

                    // Simple data writing - let Excel handle formatting
                    if (value instanceof String) {
                        writer.writeAttribute("t", "str");
                        writer.writeStartElement("v");
                        writer.writeCharacters(value.toString());
                        writer.writeEndElement();
                    } else if (value instanceof Number) {
                        writer.writeStartElement("v");
                        writer.writeCharacters(value.toString());
                        writer.writeEndElement();
                    } else {
                        // Convert everything else to string
                        writer.writeAttribute("t", "str");
                        writer.writeStartElement("v");
                        writer.writeCharacters(value.toString());
                        writer.writeEndElement();
                    }

                    writer.writeEndElement();
                }
            }

            writer.writeEndElement();
            rowNum++;
        }
        
        System.out.println("✅ Data rows written successfully");
    }

    /**
     * Helper methods (simplified)
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
}
