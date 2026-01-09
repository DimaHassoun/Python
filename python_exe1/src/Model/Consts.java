package Model;

import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

public class Consts {

    /**
     * Returns the path to Questions.csv
     * - IDE (Eclipse): read directly from resources
     * - JAR: extract CSV next to the JAR if missing
     */
    public static String getCSVPath() {
        try {
            String jarPath = Consts.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath();

            String decodedPath = URLDecoder.decode(jarPath, "UTF-8");

            boolean runningFromJar = decodedPath.endsWith(".jar");

            // ================= IDE MODE =================
            if (!runningFromJar) {
                URL resource = Consts.class.getResource("/resource/Questions.csv");
                if (resource == null) {
                    System.out.println("Questions.csv not found in resources");
                    return null;
                }
                return resource.getPath();
            }

            // ================= JAR MODE =================
            File jarFile = new File(decodedPath);
            File baseDir = jarFile.getParentFile();

            if (baseDir == null) {
                System.out.println("Base directory not found.");
                return null;
            }

            File csvFile = new File(baseDir, "Questions.csv");

            if (!csvFile.exists()) {
                System.out.println("CSV not found externally. Extracting from JAR...");
                if (!extractCSVFromJAR(csvFile)) {
                    System.out.println("Failed to extract CSV from JAR.");
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
     * Extracts Questions.csv from JAR resources to an external file
     */
    private static boolean extractCSVFromJAR(File targetFile) {
        try (InputStream in = Consts.class.getResourceAsStream("/resource/Questions.csv")) {

            if (in == null) {
                System.out.println("Questions.csv not found inside JAR resources.");
                return false;
            }

            targetFile.getParentFile().mkdirs();

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
