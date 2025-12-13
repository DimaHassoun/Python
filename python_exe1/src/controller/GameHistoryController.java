package controller;

import Model.Game;
import Model.GameHistory;
import Model.GameResult;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GameHistoryController {

    private static final String HISTORY_FILE = "src/resource/history.txt";
    private static final List<GameHistory> historyList = new ArrayList<>();

    static {
    	// Load existing history from file when the program starts
        loadHistoryFromFile();
    }

    private static void loadHistoryFromFile() {
        File f = new File(HISTORY_FILE);
        if (!f.exists()) return;

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


    //Create a new GameHistory entry from a Game object
    public static GameHistory createHistoryEntry(Game game, GameResult result) {
        GameHistory gh = new GameHistory(game, result);
        saveHistory(gh);
        return gh;
    }

 // Return the full history list
    public static List<GameHistory> getHistoryList() {
        return historyList;
    }
}
