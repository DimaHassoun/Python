package Model;

import java.time.LocalDateTime;

public class GameHistory {
    
    private Game game;            
    private LocalDateTime gamedate; 
    private int score;
    private GameResult gameResult;   //  Victory/Defeat

 // Constructor
    public GameHistory(Game game, GameResult gameResult) {
        this.game = game;
        this.gamedate = LocalDateTime.now(); 
        this.score = game.getSharedPoints();
        this.gameResult = gameResult;
    }

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public LocalDateTime getGamedate() {
		return gamedate;
	}

	public void setGamedate(LocalDateTime gamedate) {
		this.gamedate = gamedate;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public GameResult getGameResult() {
		return gameResult;
	}

	public void setGameResult(GameResult gameResult) {
		this.gameResult = gameResult;
	}
    
    
}
