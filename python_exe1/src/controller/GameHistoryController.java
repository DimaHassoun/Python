package controller;

import Model.Game;
import Model.GameHistory;
import Model.GameResult;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GameHistoryController {

    private static final String HISTORY_FILE = getHistoryFilePath();
    private static final List<GameHistory> historyList = new ArrayList<>();

    static {
    	// Load existing history from file when the program starts
        loadHistoryFromFile();
    }
    
    /**
     * Get the correct path for history.txt
     * - In Eclipse: src/resource/history.txt (for development)
     * - In JAR: history.txt next to JAR (because JAR is read-only)
     */
    private static String getHistoryFilePath() {
        try {
            // Get the location of the class
            String path = GameHistoryController.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath();
            String decodedPath = java.net.URLDecoder.decode(path, "UTF-8");
            
            if (decodedPath.endsWith(".jar")) {
                // Running from JAR - save next to JAR file
                File jarFile = new File(decodedPath);
                File jarDir = jarFile.getParentFile();
                File historyFile = new File(jarDir, "history.txt");
                System.out.println("✓ Running from JAR");
                System.out.println("📁 History location: " + historyFile.getAbsolutePath());
                return historyFile.getAbsolutePath();
            } else {
                // Running from Eclipse - use src/resource/history.txt
                System.out.println("✓ Running from IDE");
                System.out.println("📁 History location: src/resource/history.txt");
                return "src/resource/history.txt";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to src/resource for IDE
            return "src/resource/history.txt";
        }
    }

    private static void loadHistoryFromFile() {
        File f = new File(HISTORY_FILE);
        if (!f.exists()) {
            System.out.println(" No history file found - will create on first save");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
            	// Format: GameID,Player1,Player2,Score,Difficulty,Result,Date
                String[] parts = line.split(",");
                if (parts.length < 7) continue;
                int gameId = Integer.parseInt(parts[0].trim());
                String player1 = parts[1].trim();
                String player2 = parts[2].trim();
                int score = Integer.parseInt(parts[3].trim());
                Game.Difficulty difficulty = Game.Difficulty.valueOf(parts[4].trim());
                GameResult result = GameResult.valueOf(parts[5].trim());
                LocalDateTime date = LocalDateTime.parse(parts[6].trim());

                // Create a temporary Game object to store relevant data
                Game game = new Game(gameId, difficulty, player1, player2);
                game.setSharedPoints(score);

                GameHistory gh = new GameHistory(game, result);
                gh.setGamedate(date);
                historyList.add(gh);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 
 // Save a new history entry to file and memory
    public static void saveHistory(GameHistory gh) {
        try {
            File f = new File(HISTORY_FILE);

            // Ensure parent directory exists
            File parent = f.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            // Append entry to file
            try (FileWriter fw = new FileWriter(f, true);
                 BufferedWriter bw = new BufferedWriter(fw)) {

                String line = String.join(",",
                        String.valueOf(gh.getGame().getId()),
                        gh.getGame().getPlayer1Name(),
                        gh.getGame().getPlayer2Name(),
                        String.valueOf(gh.getScore()),
                        gh.getGame().getDifficulty().name(),
                        gh.getGameResult().name(),
                        gh.getGamedate().toString()
                ) + System.lineSeparator();

                bw.write(line);
                bw.flush();
            }

            // Add to in-memory list
            historyList.add(gh);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Create a new GameHistory entry from a Game object GameResult
    public static GameHistory createHistoryEntry(Game game, String result) {
    	 GameResult enumResult;

    	    if ("Defeat".equalsIgnoreCase(result)) {
    	        enumResult = GameResult.Defeat;
    	    } else if ("Victory".equalsIgnoreCase(result)) {
    	        enumResult = GameResult.Victory;
    	    } else {
    	        // Default or throw exception if needed
    	        throw new IllegalArgumentException("Invalid game result: " + result);
    	        // Or you can set a default:
    	        // enumResult = GameResult.Defeat; 
    	    }
        GameHistory gh = new GameHistory(game, enumResult);
        saveHistory(gh);
        return gh;
    }
   

 // Return the full history list
    public static List<GameHistory> getHistoryList() {
        return historyList;
    }
}
