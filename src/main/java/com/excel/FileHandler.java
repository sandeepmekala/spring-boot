package com.excel;

import java.io.*;

/**
 * Simplified file handler for Excel operations
 */
public class FileHandler {

    /**
     * Load template from resources as temporary file
     */
    public static File loadTemplate(String resourcePath) throws Exception {
        try (InputStream stream = FileHandler.class.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new FileNotFoundException("Resource not found: " + resourcePath);
            }

            File temp = File.createTempFile("template", ".xlsx");
            temp.deleteOnExit();

            try (OutputStream out = new FileOutputStream(temp)) {
                stream.transferTo(out);
            }

            return temp;
        }
    }

    /**
     * Get output file in resources folder
     */
    public static File getOutputFile(String fileName) {
        File resourcesDir = new File("src/main/resources");
        resourcesDir.mkdirs();
        return new File(resourcesDir, fileName);
    }

    /**
     * Read stream to byte array
     */
    public static byte[] readBytes(InputStream input) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }
    }

    /**
     * Write bytes to stream
     */
    public static void writeBytes(byte[] data, OutputStream output) throws IOException {
        output.write(data);
        output.flush();
    }
}
