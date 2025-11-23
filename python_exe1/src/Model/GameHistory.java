package Model;

import java.time.LocalDateTime;

public class GameHistory {
private static int counter = 0;	
private int GameHistory_id=0;	
private int Game_id=0;
private int Player1_id,Player2_id;
private int FinalSharedPoints;
private int FinalSharedLives;
private GameResult Game_Result;
private LocalDateTime timestamp;

public GameHistory(int game_id, int player1_id, int player2_id, int finalSharedPoints,
		int finalSharedLives, GameResult game_Result, LocalDateTime timestamp) {
	super();
	GameHistory_id = ++counter;
	Game_id = game_id;
	Player1_id = player1_id;
	Player2_id = player2_id;
	FinalSharedPoints = finalSharedPoints;
	FinalSharedLives = finalSharedLives;
	Game_Result = game_Result;
	this.timestamp = timestamp;
}
public int getGameHistory_id() {
	return GameHistory_id;
}
public int getGame_id() {
	return Game_id;
}
public void setGame_id(int game_id) {
	Game_id = game_id;
}
public int getPlayer1_id() {
	return Player1_id;
}
public void setPlayer1_id(int player1_id) {
	Player1_id = player1_id;
}
public int getPlayer2_id() {
	return Player2_id;
}
public void setPlayer2_id(int player2_id) {
	Player2_id = player2_id;
}
public int getFinalSharedPoints() {
	return FinalSharedPoints;
}
public void setFinalSharedPoints(int finalSharedPoints) {
	FinalSharedPoints = finalSharedPoints;
}
public int getFinalSharedLives() {
	return FinalSharedLives;
}
public void setFinalSharedLives(int finalSharedLives) {
	FinalSharedLives = finalSharedLives;
}
public GameResult getGame_Result() {
	return Game_Result;
}
public void setGame_Result(GameResult game_Result) {
	Game_Result = game_Result;
}
public LocalDateTime getTimestamp() {
	return timestamp;
}
public void setTimestamp(LocalDateTime timestamp) {
	this.timestamp = timestamp;
}


}

