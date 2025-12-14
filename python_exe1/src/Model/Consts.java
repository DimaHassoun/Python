package Model;

import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Consts {

    /**
     * Returns the path to Questions.csv
     * - First checks next to the JAR file
     * - If not found, copies from JAR resources to external location
     */
    public static String getCSVPath() {
        try {
            // Get the directory where the JAR is located
            String jarPath = Consts.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String decodedPath = java.net.URLDecoder.decode(jarPath, "UTF-8");

            File baseDir;
            if (decodedPath.endsWith(".jar")) {
                // Running from JAR - use JAR's directory
                baseDir = new File(decodedPath).getParentFile();
            } else {
                // Running from IDE - use project root
                File classesDir = new File(decodedPath);
                baseDir = classesDir.getParentFile().getParentFile();
            }

            if (baseDir == null) {
                System.out.println("Base directory not found.");
                return null;
            }

            // Look for CSV next to JAR (or in project root for IDE)
            File csvFile = new File(baseDir, "Questions.csv");

            // If CSV doesn't exist externally, copy it from JAR resources
            if (!csvFile.exists()) {
                System.out.println("CSV not found at: " + csvFile.getAbsolutePath());
                System.out.println("Attempting to extract from JAR...");
                
                if (extractCSVFromJAR(csvFile)) {
                    System.out.println("CSV extracted successfully to: " + csvFile.getAbsolutePath());
                } else {
                    System.out.println("Failed to extract CSV from JAR");
                    return null;
                }
            }

            return csvFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Extracts Questions.csv from JAR resources to external file
     */
    private static boolean extractCSVFromJAR(File targetFile) {
        try (InputStream in = Consts.class.getResourceAsStream("/resource/Questions.csv")) {
            if (in == null) {
                System.out.println("Questions.csv not found in JAR resources");
                return false;
            }

            // Create parent directory if needed
            targetFile.getParentFile().mkdirs();

            // Copy from JAR to external file
            try (FileOutputStream out = new FileOutputStream(targetFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}