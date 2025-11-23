package Model;

import java.net.URLDecoder;

public class Consts {

    // Return path to the Excel file dynamically
    public static String getCSVPath() {
        try {
            // Path to the class location
            String path = Consts.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String decoded = URLDecoder.decode(path, "UTF-8");

            if (decoded.contains(".jar")) {
                return null; 
            } else {
                decoded = decoded.substring(0, decoded.lastIndexOf("bin/"));
                return decoded + "src/model/Questions.csv";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

