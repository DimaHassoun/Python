package Model;

import java.io.File;
import java.net.URLDecoder;

public class Consts {

    /**
     * Returns the path to the CSV file located in the 'resource' folder next to the JAR or project root.
     * Returns null if file is not found.
     */
    public static String getCSVPath() {
        try {
            String path = Consts.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String decodedPath = URLDecoder.decode(path, "UTF-8");

            File baseDir;

            if (decodedPath.endsWith(".jar")) {
                baseDir = new File(decodedPath).getParentFile();
            } else {
                File classesDir = new File(decodedPath);
                baseDir = classesDir.getParentFile().getParentFile();
            }

            if (baseDir == null) {
                System.out.println("Base directory not found.");
                return null;
            }

            // Here: we look inside "resource" folder next to the JAR or project root
            File csvFile = new File(baseDir, "python_exe1/src/resource" + File.separator + "Questions.csv");

            if (!csvFile.exists()) {
                System.out.println("CSV file not found: " + csvFile.getAbsolutePath());
                return null;
            }

            return csvFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
