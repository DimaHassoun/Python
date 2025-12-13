package controller;

import Model.Game;
import Model.GameHistory;
import Model.Board;
import Model.Cell;
import view.GameBoards;

import java.util.ArrayList;
import java.util.List;
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
		 // ===== validation =====
	    if (player1Name == null || player2Name == null) {
	        throw new IllegalArgumentException("Player name cannot be null");
	    }

	    if (difficultyString == null) {
	        throw new NullPointerException("Difficulty cannot be null");
	    }

	    if (difficultyString.isEmpty()) {
	        throw new IllegalArgumentException("Difficulty cannot be empty");
	    }
		
		int id = getNextGameIdFromHistory();

		// Convert difficulty
		Game.Difficulty difficulty;
		switch (difficultyString.toUpperCase()) {
		case "EASY": difficulty = Game.Difficulty.EASY; break;
		case "MEDIUM": difficulty = Game.Difficulty.MEDIUM; break;
		case "HARD": difficulty = Game.Difficulty.HARD; break;
		default: throw new IllegalArgumentException("Invalid difficulty");
		}

		Game game = new Game(id, difficulty, player1Name, player2Name);

		// NEW: Set lives according to difficulty
		switch (difficulty) {
		case EASY -> game.setSharedLives(10);
		case MEDIUM -> game.setSharedLives(8);
		case HARD -> game.setSharedLives(6);
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

	public static int getNextGameIdFromHistory() {
		List<GameHistory> history = GameHistoryController.getHistoryList();
		if (history.isEmpty()) {
			return 1; // If no history exists, start from 1
		} else {
			// If no history exists, start from 1
			GameHistory last = history.get(history.size() - 1);
			return last.getGame().getId() + 1;
		}
	}

	public static void switchTurn(int gameNum, GameBoards gameBoard) {
		// Switch current player
		Game game = getGame(gameNum);
		game.setSurpriseActivatedThisTurn(false);
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
	public static boolean iscellUsed(int gameNum, boolean isLeft,int row ,int col) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		Cell cell = board.getCell(row, col);
		return cell.isUsed();
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

	// ========================= Surprise Cell Logic =========================
	public static String ActivateSurpriseCell(int gameNum, boolean isLeft, int row, int col) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		Cell cell = board.getCell(row, col);

		// בדיקות
		if (cell.getType() != Cell.CellType.SURPRISE) return "NOT_SURPRISE";
		if (!cell.isRevealed()) return "NOT_REVEALED";
		if (cell.isUsed()) return "ALREADY_USED";
		if (!game.canPerformAction()) return "ALREADY_USED";

		// מורידים נקודות הפעלה
		int cost = game.getActivationCost();
		game.addSharedPoints(-cost);

		// מסמנים שהופעלה
		cell.setUsed(true);

		// סיכוי 50/50
		boolean good = Math.random() < 0.5;
		int pointsChanged = game.applySurpriseEffect(good);

		return (good ? "GOOD:" : "BAD:") + pointsChanged ;
	}
	// ========================= Question Cell Logic =========================
	public static int GetGameSurpriseQuestionCoust(int gameNum) {
		Game game = getGame(gameNum);
		return game.getActivationCost();
	}
	public static void ActivateQuestion(int gameNum) {
		Game game = getGame(gameNum);
		int cost = game.getActivationCost();
		game.addSharedPoints(-cost);
	}
	
	private static  boolean canSwitch;//tell game it can switch turns
	private static QuestionAction pendingQuestionAction = QuestionAction.NONE;
	//getter and setter
	public static boolean isCanSwitch() {
		return canSwitch;
	}
	public static  void setCanSwitch(boolean canswitch) {
		canSwitch = canswitch;
	}
	//Cancel 
	public static void sourceCancel() {
		setCanSwitch(false);
	}
	//random Helper
	private static boolean random50() {
		return Math.random() < 0.5;
	}
	public enum QuestionAction {
	    NONE,
	    REVEAL_RANDOM_MINE,
	    REVEAL_3X3
	}
	public static QuestionAction getPendingQuestionAction() {
		return pendingQuestionAction;
	}
	public static void clearPendingQuestionAction() {
	    pendingQuestionAction = QuestionAction.NONE;
	}
	//Scoring Question
	public static String applyQuestionScoring(int gameNum, String questionLevel,boolean isCorrect, boolean isLeft,int row,int col){
		Game game = getGame(gameNum);
		Game.Difficulty gameLevel= game.getDifficulty();
		// Mark cell as used
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		Cell cell = board.getCell(row, col);
		cell.setUsed(true);
		
		int pointsChange = 0;
		int heartsChange = 0;
		String actionMessage = ""; 
		// EASY GAME 
		if (gameLevel == Game.Difficulty.EASY) {
			//EASY
			if (questionLevel.equals("1")) {
				if (isCorrect) {
					pointsChange = 3;
					heartsChange = 1;
				} else if (random50()) { // Incorrect: -3 points OR nothing (random50 decides)
					pointsChange = -3;
				}
			}
			//MEDIUM
			else if (questionLevel.equals("2")) {
				if (isCorrect) {
					pointsChange = 6;
					actionMessage = "Reveal hidden Mine";
					pendingQuestionAction = QuestionAction.REVEAL_RANDOM_MINE;
				} else if (random50()) { // Incorrect: -6 points OR nothing
					pointsChange = -6;
				}
			}
			//HARD
			else if (questionLevel.equals("3")) {
				if (isCorrect) {
					pointsChange = 10;
					actionMessage = "Show 3x3 random grid";
					pendingQuestionAction = QuestionAction.REVEAL_3X3;
				} else {
					pointsChange = -10;  // Incorrect: always -10 point
				}
			}
			//EXPERT
			else if (questionLevel.equals("4")) {
				if (isCorrect) {
					pointsChange = 15;
					heartsChange = 2;
				} else {
					pointsChange = -15;
					heartsChange = -1;
				}
			}
		}

		//  MEDIUM GAME
		else if (gameLevel == Game.Difficulty.MEDIUM) {
			//EASY
			if (questionLevel.equals("1")) {
				if (isCorrect) {
					pointsChange = 8;
					heartsChange = 1;
				} else {
					pointsChange = -8;
				}
			}
			//MEDIUM
			else if (questionLevel.equals("2")) {
				if (isCorrect) {
					pointsChange = 10;
					heartsChange = 1;
				} else if (random50()) {
					pointsChange = -10; // Incorrect: -10 points OR nothing
					heartsChange = -1; // Incorrect: -1 heart OR nothing
				}
			}
			//HARD
			else if (questionLevel.equals("3")) {
				if (isCorrect) {
					pointsChange = 15;
					heartsChange = 1;
				} else {
					pointsChange = -15;
					heartsChange = -1;
				}
			}
			//EXPERT
			else if (questionLevel.equals("4")) {
				if (isCorrect) {
					pointsChange = 20;
					heartsChange = 2;
				} else {
					pointsChange = -20;
					heartsChange = random50() ? -1 : -2; // Incorrect: -1 OR -2 hearts
				}
			}
		}

		// HARD GAME 
		else if (gameLevel == Game.Difficulty.HARD) {
			//EASY
			if (questionLevel.equals("1")) {
				if (isCorrect) {
					pointsChange = 10;
					heartsChange = 1;
				} else {
					pointsChange = -10;
					heartsChange = -1;
				}
			}
			//MEDIUM
			else if (questionLevel.equals("2")) {
				if (isCorrect) {
					pointsChange = 15;
					heartsChange = random50() ? 1 : 2; // Correct: +1 OR +2 hearts
				} else {
					pointsChange = -15;
					heartsChange = random50() ? -1 : -2;  // Incorrect: -1 OR -2 hearts
				}
			}
			//HARD
			else if (questionLevel.equals("3")) {
				if (isCorrect) {
					pointsChange = 20;
					heartsChange = 2;
				} else {
					pointsChange = -20;
					heartsChange = -2;
				}
			}
			//EXPERT
			else if (questionLevel.equals("4")) {
				if (isCorrect) {
					pointsChange = 40;
					heartsChange = 3;
				} else {
					pointsChange = -40;
					heartsChange = -3;
				}
			}
		}

		// ===== APPLY =====
		game.addSharedPoints(pointsChange);
		game.setSharedLives(game.getSharedLives() + heartsChange);

		// ===== BUILD MESSAGE =====
		String message = "";
		if (pointsChange != 0) {
			message += (pointsChange > 0 ? "+" : "") + pointsChange + " points\n";
		}
		if (heartsChange != 0) {
			message += (heartsChange > 0 ? "+" : "") + heartsChange + " hearts\n";
		}
		if (!actionMessage.isEmpty()) {
			message += "Action: " + actionMessage + "\n";
		}
		if (message.equals("")) {
			message = "No change in points, hearts, or actions.";
		}
		setCanSwitch(true);
		return message;
	}
	
	public static int[] revealRandomHiddenMine(int gameNum, boolean isLeft) {
	    Game game = getGame(gameNum);
	    Board board = isLeft ? game.getBoard1() : game.getBoard2();

	    List<int[]> hiddenMines = new ArrayList<>();

	    for (int r = 0; r < board.getSize(); r++) {
	        for (int c = 0; c < board.getSize(); c++) {
	            Cell cell = board.getCell(r, c);
	            if (cell.getType() == Cell.CellType.MINE && !cell.isRevealed() && !cell.isCounted()) {
	                hiddenMines.add(new int[]{r, c});
	            }
	        }
	    }

	    if (hiddenMines.isEmpty()) return null;

	    int[] chosen = hiddenMines.get((int)(Math.random() * hiddenMines.size()));

	    int row = chosen[0];
	    int col = chosen[1];

	    RevealCell(gameNum, isLeft, row, col);
	    setCountedAsCounted(gameNum, isLeft, row, col);
	    decrementRemainingMinesInBoard(gameNum, isLeft);

	    return chosen; // {row, col}
	}

	//3x3 random grid
	public static ArrayList<int[]> reveal3x3RandomGrid(int gameNum, boolean isLeft) {
	    Game game = getGame(gameNum);
	    Board board = isLeft ? game.getBoard1() : game.getBoard2();
	    int size = board.getSize();

	    ArrayList<int[]> hiddenCells = new ArrayList<>();

	    // Collect all unrevealed cells
	    for (int r = 0; r < size; r++) {
	        for (int c = 0; c < size; c++) {
	            Cell cell = board.getCell(r, c);
	            if (!cell.isRevealed()) {
	                hiddenCells.add(new int[]{r, c});
	            }
	        }
	    }

	    if (hiddenCells.isEmpty()) return new ArrayList<>(); // nothing to reveal

	    // Pick a random center cell for the 3x3 block
	    int[] center = hiddenCells.get((int)(Math.random() * hiddenCells.size()));
	    int centerRow = center[0];
	    int centerCol = center[1];

	    ArrayList<int[]> revealedCells = new ArrayList<>();

	    // Reveal all cells in 3x3 block around the center (respect board bounds)
	    for (int dr = -1; dr <= 1; dr++) {
	        for (int dc = -1; dc <= 1; dc++) {
	            int r = centerRow + dr;
	            int c = centerCol + dc;
	            if (r >= 0 && r < size && c >= 0 && c < size) {
	                Cell cell = board.getCell(r, c);
	                if (!cell.isRevealed()) {
	                    cell.setRevealed(true);
	                    revealedCells.add(new int[]{r, c});

	                    // If it's a mine and not counted, decrement remaining mines and mark counted
	                    if (cell.getType() == Cell.CellType.MINE && !cell.isCounted()) {
	                        decrementRemainingMinesInBoard(gameNum, isLeft);
	                        setCountedAsCounted(gameNum, isLeft, r, c);
	                    }
	                }
	            }
	        }
	    }

	    return revealedCells; // List of {row, col} revealed
	}

}
