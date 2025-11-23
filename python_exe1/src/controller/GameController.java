package controller;

import Model.Game;
import Model.Board;
import Model.Cell;
import view.GameBoards;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GameController {

	private static final AtomicInteger NEXT_ID = new AtomicInteger(1);
	private static final Map<Integer, Game> activeGames = new ConcurrentHashMap<>();

	public GameController() {}

	/**
	 * Create a new Game instance based on difficulty and player names
	 */
	public static Game createNewGame(String player1Name, String player2Name, String difficultyString) {
		int id = NEXT_ID.getAndIncrement();

		// Convert difficulty
		Game.Difficulty difficulty;
		switch (difficultyString.toUpperCase()) {
		case "EASY": difficulty = Game.Difficulty.EASY; break;
		case "MEDIUM": difficulty = Game.Difficulty.MEDIUM; break;
		case "HARD": difficulty = Game.Difficulty.HARD; break;
		default: throw new IllegalArgumentException("Unknown difficulty: " + difficultyString);
		}

		Game game = new Game(id, difficulty, player1Name, player2Name);

		// NEW: הגדרת חיים לפי קושי
		switch (difficulty) {
		case EASY -> game.setSharedLives(10);
		case MEDIUM -> game.setSharedLives(7);
		case HARD -> game.setSharedLives(5);
		}

		// Random first player
		int startingPlayer = (Math.random() < 0.5) ? 1 : 2;
		game.setCurrentPlayer(startingPlayer);

		// Difficulty-based settings (activation cost)
		switch (difficulty) {
		case EASY -> game.setActivationCost(5);
		case MEDIUM -> game.setActivationCost(8);
		case HARD -> game.setActivationCost(12);
		}

		activeGames.put(id, game);
		return game;
	}

	public static void switchTurn(int gameNum, GameBoards gameBoard) {
		// Switch current player
		Game game = getGame(gameNum);
		if (game.getCurrentPlayer() == 1) game.setCurrentPlayer(2);
		else game.setCurrentPlayer(1);

		// Update UI
		gameBoard.highlightCurrentPlayer(game.getCurrentPlayer());
	}

	public static int getNextGameId() {
		return NEXT_ID.get();
	}

	public static Game getGame(int id) {
		return activeGames.get(id);
	}

	public static void removeGame(int id) {
		activeGames.remove(id);
	}

	//========================= Cell =========================
	public static boolean IsCellRevealed(int gameNum, boolean isLeft, int row, int col) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		Cell clickedCell = board.getCell(row, col);

		if (clickedCell.isRevealed())return true;
		else return false;
	}
	//Play Cascade Reveal
	public static void PlayCascadeReveal(int gameNum, boolean isLeft, int row, int col) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		board.cascadeReveal(row, col);

	}
	//Get Cell Type
	public static String GetCellType(int gameNum, boolean isLeft,int row, int col) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		Cell cell = board.getCell(row, col);
		switch (cell.getType()) {
		case EMPTY:
			return "EMPTY";
		case NUMBER:
			return "NUMBER";
		case MINE:
			return "MINE";
		case SURPRISE:
			return "SURPRISE";
		case QUESTION:
			return "QUESTION";
		}
		return "Error!!";
	}
	// returns the display emoji string
	public static String getCellDisplay(int gameNum, boolean isLeft, int row, int col) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		return board.getCell(row, col).getDisplay();
	}
	//cell.isFlagged()
	public static boolean IsCellFlagged(int gameNum, boolean isLeft, int row, int col) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		Cell cell = board.getCell(row, col);
		return cell.isFlagged();
	}
	//cell setFlagged(false);
	public static void UnFlaggedCell(int gameNum, boolean isLeft, int row, int col) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		Cell cell = board.getCell(row, col);
		cell.setFlagged(false);
	}
	//cell setFlagged(true);
	public static void FlaggedCell(int gameNum, boolean isLeft, int row, int col) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		Cell cell = board.getCell(row, col);
		cell.setFlagged(true);
	}
	//  Reveal cell(true)
	public static void RevealCell(int gameNum, boolean isLeft, int row, int col) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		Cell cell = board.getCell(row, col);
		cell.setRevealed(true);
	}
	//is Current Cell Counted??
	public static boolean IsCellCounted(int gameNum, boolean isLeft, int row, int col) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		Cell currentCell = board.getCell(row, col);
		return currentCell.isCounted();
	}
	//currentCell.setCounted(true);
	public static void setCountedAsCounted(int gameNum, boolean isLeft, int row, int col) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		Cell currentCell = board.getCell(row, col);
		currentCell.setCounted(true); 
	}
	//========================= Cell =========================
	//========================= board =========================
	//board NOT null
	public static boolean boardNOTnull(int gameNum, boolean isLeft) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		if (board != null) {
			return true;
		}
		return false;
	}
	// board.incrementRemainingMines()
	public static void IncrementRemainingMinesInBoard(int gameNum, boolean isLeft) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		board.incrementRemainingMines();
	}
	//board.decrementRemainingMines();
	public static void decrementRemainingMinesInBoard(int gameNum, boolean isLeft) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		board.decrementRemainingMines();
	}
	//board get Size 
	public static int getBoardSize(int gameNum, boolean isLeft) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		return board.getSize();
	}

	//========================= board =========================
	//========================= game =========================
	//game isGameOver 
	public static boolean GameIsGameOver(int gameNum) {
		Game game = getGame(gameNum);
		if (game.isGameOver()) return true;
		else return false;
	}
	//game getSharedLives 
	public static int GameGetSharedLives(int gameNum) {
		Game game = getGame(gameNum);
		return game.getSharedLives();
	}
	//game get Difficulty
	public static String GameGetDifficulty(int gameNum) {
		Game game = getGame(gameNum);
		return game.getDifficulty().name();
	}
	//game finish
	public static void GameFinish(int gameNum) {
		Game game = getGame(gameNum);
		game.finish();
	}
	//isVictory
	public static boolean IsGameVictory(int gameNum) {
		Game game = getGame(gameNum);
		return game.isVictory();
	}
	//========================= game =========================
	//========================= Player =========================	
	//Get Current Player 
	public static int GameGetCurrentPlayer(int gameNum) {
		Game game = getGame(gameNum);
		return game.getCurrentPlayer();
	}
	//========================= Player =========================
	//========================= Mines =========================	
	//getRemainingMines
	public static int getRemainingMines(int gameNum, boolean isLeft) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		return board.getRemainingMines();
	}
	//========================= Mines =========================
	//========================= Lives =========================	
	//getSharedLivesGame
	public static int getSharedLivesGame(int gameNum) {
		Game game = getGame(gameNum);
		return game.getSharedLives();
	}
	//setSharedLives
	public static void UpdateSharedLivesGame(int gameNum,int SharedLives ) {
		Game game = getGame(gameNum);
		game.setSharedLives(SharedLives);
	}
	//========================= Lives =========================
	//========================= Points =========================
	//addSharedPoints
	public static void UpdateSharedPoints(int gameNum,int SharedPointsToAdd) {
		Game game = getGame(gameNum);
		game.addSharedPoints(SharedPointsToAdd);
	}
	//game.getSharedPoints()
	public static int getSharedPoints(int gameNum) {
		Game game = getGame(gameNum);
		return game.getSharedPoints();
	}
}
