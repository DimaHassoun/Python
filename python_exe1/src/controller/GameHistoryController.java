package controller;

import Model.GameHistory;
import Model.GameResult;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GameHistoryController {

    private static final String HISTORY_FILE = "game_history.txt";
    private static final String HISTORY_ID_FILE = "game_history_id.txt";
    private static int NEXT_HISTORY_ID = loadLastHistoryId();

    private static final List<GameHistory> historyList = new ArrayList<>();

    // Load last used history ID
    private static int loadLastHistoryId() {
        try {
            File f = new File(HISTORY_ID_FILE);
            if (!f.exists()) return 1;

            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            br.close();
            return Integer.parseInt(line.trim());
        } catch (Exception e) {
            return 1;
        }
    }

    private static void saveNextHistoryId() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(HISTORY_ID_FILE));
            bw.write(String.valueOf(NEXT_HISTORY_ID));
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Save a history entry to disk
    public static void saveHistory(GameHistory gh) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(HISTORY_FILE, true));
            bw.write(
                gh.getGameHistory_id() + "," +
                gh.getGame_id() + "," +
                gh.getPlayer1_id() + "," +
                gh.getPlayer2_id() + "," +
                gh.getFinalSharedPoints() + "," +
                gh.getFinalSharedLives() + "," +
                gh.getGame_Result() + "," +
                gh.getTimestamp() + "\n"
            );
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Create new GameHistory entry
    public static GameHistory createHistoryEntry(int gameId, int p1, int p2, int points, int lives, GameResult result) {

        GameHistory gh = new GameHistory(
                gameId,
                p1,
                p2,
                points,
                lives,
                result,
                LocalDateTime.now()
        );

        // Force ID to be persistent
        gh.setGame_id(NEXT_HISTORY_ID++);
        saveNextHistoryId();

        historyList.add(gh);
        saveHistory(gh);

        return gh;
    }

    public static List<GameHistory> getHistoryList() {
        return historyList;
    }
}
