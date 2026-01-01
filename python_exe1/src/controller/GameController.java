package controller;

import Model.Game;
import Model.GameHistory;
import view.GameBoards;
import Model.Board;
import Model.Cell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JOptionPane;


public class GameController {

	private static final AtomicInteger NEXT_ID = new AtomicInteger(1);
	private static final Map<Integer, Game> activeGames = new ConcurrentHashMap<>();
	private static final Map<Integer, Boolean> pausedGames = new ConcurrentHashMap<>();
	
	// Toggle Pause/Resume state
	public static void togglePause(int gameNum) {
	    boolean paused = pausedGames.getOrDefault(gameNum, false);
	    pausedGames.put(gameNum, !paused);
	}

	// Check if the game is stopped
	public static boolean isPaused(int gameNum) {
	    return pausedGames.getOrDefault(gameNum, false);
	}
	
	public static int startingPlayer;
	public GameController() {}
	
	/**
	 * Create a new Game instance based on difficulty and player names
	 */
	public static GameBoards  createNewGame(String player1Name, String player2Name, String difficultyString) {
	    
	    // Validation with exceptions
	    if (player1Name == null || player1Name.trim().isEmpty()) {
	        throw new IllegalArgumentException("Player 1 name cannot be empty");
	    }
	    
	    if (player2Name == null || player2Name.trim().isEmpty()) {
	        throw new IllegalArgumentException("Player 2 name cannot be empty");
	    }
	    
	    if (difficultyString == null || difficultyString.trim().isEmpty()) {
	        throw new IllegalArgumentException("Difficulty cannot be empty");
	    }

	    int id = getNextGameIdFromHistory();

	    // Convert difficulty
	    Game.Difficulty difficulty;
	    try {
	        difficulty = Game.Difficulty.valueOf(difficultyString.toUpperCase());
	    } catch (IllegalArgumentException e) {
	        throw new IllegalArgumentException("Unknown difficulty: " + difficultyString + ". Please select Easy, Medium, or Hard.");
	    }

	    Game game = new Game(id, difficulty, player1Name, player2Name);

	    // Set lives according to difficulty
	    switch (difficulty) {
	        case EASY -> game.setSharedLives(10);
	        case MEDIUM -> game.setSharedLives(8);
	        case HARD -> game.setSharedLives(6);
	    }

	    // Random first player
	     startingPlayer = (Math.random() < 0.5) ? 1 : 2;
	    game.setCurrentPlayer(startingPlayer);

	    // Difficulty-based settings (activation cost)
	    switch (difficulty) {
	        case EASY -> game.setActivationCost(5);
	        case MEDIUM -> game.setActivationCost(8);
	        case HARD -> game.setActivationCost(12);
	    }

	    activeGames.put(id, game);
	    
	    startingPlayer = game.getCurrentPlayer();
        game.start();
        GameBoards gameBoard = new GameBoards(
            game.getBoard1().getSize(),
            game.getBoard1().getSize(),
            game.getBoard1().getTotalMines(),
            game.getBoard1().getTotalMines(),
            player1Name,
            player2Name,
            game.getId()
        );
	    return gameBoard;
	}
	public static int GetCurrentPlayer() {
		return startingPlayer;
	}
	/**
	 * Returns the next game ID based on the game history.
	 * If the game history list is empty, the method returns 1.
	 * Otherwise, it retrieves the last game in the history and returns
	 * its ID incremented by one.
	 */
	public static int getNextGameIdFromHistory() {
		List<GameHistory> history = GameHistoryController.getHistoryList();
		if (history.isEmpty()) {
			return 1; // If no history exists, start from 1
		} else {
			 // Get the last game in history and increment its ID
			GameHistory last = history.get(history.size() - 1);
			return last.getGame().getId() + 1;
		}
	}
	/**
	 * Switches the turn to the other player in the specified game.
	 * toggles the current player between Player 1 and Player 2,
	 * and updates the game board UI to highlight the new current player.
	 */
	public static int switchTurn(int gameNum) {
	    Game game = getGame(gameNum);
	    game.setSurpriseActivatedThisTurn(false);
	    game.setCurrentPlayer(game.getCurrentPlayer() == 1 ? 2 : 1);
	    return game.getCurrentPlayer();
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
	//Checks whether a specific cell is already revealed.
	public static boolean IsCellRevealed(int gameNum, boolean isLeft, int row, int col) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		Cell clickedCell = board.getCell(row, col);

		if (clickedCell.isRevealed())return true;
		else return false;
	}
	
	// Performs a cascade reveal starting from the specified cell.
	 
	public static void PlayCascadeReveal(int gameNum, boolean isLeft, int row, int col) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		board.cascadeReveal(row, col);

	}
	//Returns the type of a specific cell as a string.
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

	//Checks whether a cell is flagged.
	public static boolean IsCellFlagged(int gameNum, boolean isLeft, int row, int col) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		Cell cell = board.getCell(row, col);
		return cell.isFlagged();
	}
	// Removes a flag from a cell.
	public static void UnFlaggedCell(int gameNum, boolean isLeft, int row, int col) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		Cell cell = board.getCell(row, col);
		cell.setFlagged(false);
	}
	//Flags a cell.
	public static void FlaggedCell(int gameNum, boolean isLeft, int row, int col) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		Cell cell = board.getCell(row, col);
		cell.setFlagged(true);
	}
	//  Reveals a cell.
	public static void RevealCell(int gameNum, boolean isLeft, int row, int col) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		Cell cell = board.getCell(row, col);
		cell.setRevealed(true);
	}
	//Checks whether a cell has already been counted (e.g., for scoring).
	public static boolean IsCellCounted(int gameNum, boolean isLeft, int row, int col) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		Cell currentCell = board.getCell(row, col);
		return currentCell.isCounted();
	}
	//Marks a cell as counted.
	public static void setCountedAsCounted(int gameNum, boolean isLeft, int row, int col) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		Cell currentCell = board.getCell(row, col);
		currentCell.setCounted(true); 
	}
	//Checks whether a cell has already been used.
	public static boolean iscellUsed(int gameNum, boolean isLeft,int row ,int col) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		Cell cell = board.getCell(row, col);
		return cell.isUsed();
	}
	//-------------------------Flag---------------------------
	public enum FlagResult {
		FLAGGED_MINE,
		FLAGGED_NOT_MINE,
		UNFLAGGED
	}
	public static FlagResult handleFlag(int gamenum, Boolean isLeft, int row, int col) {

		// Un-flag the Flagged cell
		if (IsCellFlagged(gamenum, isLeft, row, col)) {
			/**DESIGN DECISION:
			 *An already-flagged cell will be unflagged when the flag action is triggered again.
			 **/
			UnFlaggedCell(gamenum, isLeft, row, col);
			RevealCell(gamenum, isLeft, row, col);

			return  FlagResult.UNFLAGGED;
		}
		// Flag
		FlaggedCell(gamenum, isLeft, row, col);
		String cellType = GetCellType(gamenum, isLeft, row, col);

		if (cellType.equals("MINE")) {
			UpdateSharedPoints(gamenum, 1);
			decrementRemainingMinesInBoard(gamenum, isLeft);
			RevealCell(gamenum, isLeft, row, col);

			return FlagResult.FLAGGED_MINE;
		} else {
			UpdateSharedPoints(gamenum, -3);
			return FlagResult.FLAGGED_NOT_MINE;
		}
	}
	//-------------------------Reveal
	public enum RevealResult {
	    REVEALED_MINE,
	    REVEALED_SAFE,
	    ALREADY_REVEALED
	}
	//------------------------------handleReveal-----------------------------
	public static RevealResult handleReveal(int gamenum, Boolean isLeft, int row, int col) {

	    if (IsCellRevealed(gamenum, isLeft, row, col)) {
	        return RevealResult.ALREADY_REVEALED;
	    }

	    boolean wasMine = GetCellType(gamenum, isLeft, row, col).equals("MINE");

	    if (wasMine) {
	        // Reveal mine directly
	        RevealCell(gamenum, isLeft, row, col);
	        if (!IsCellCounted(gamenum, isLeft, row, col)) {
	            decrementRemainingMinesInBoard(gamenum, isLeft);
	            setCountedAsCounted(gamenum, isLeft, row, col);
	        }
	        UpdateSharedLivesGame(gamenum, getSharedLivesGame(gamenum) - 1);
	        return RevealResult.REVEALED_MINE;
	    } else {
	        PlayCascadeReveal(gamenum, isLeft, row, col);
	        // Update points for safe reveal
	        UpdateSharedPoints(gamenum, 1);
	        return RevealResult.REVEALED_SAFE;
	    }
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
	//Checks whether the specified game is over.
	public static boolean GameIsGameOver(int gameNum) {
		Game game = getGame(gameNum);
		if (game.isGameOver()) return true;
		else return false;
	}
	//Returns the difficulty level of the game.
	public static String GameGetDifficulty(int gameNum) {
		Game game = getGame(gameNum);
		return game.getDifficulty().name();
	}
	//Finishes the game and marks it as ended.
	public static void GameFinish(int gameNum) {
		Game game = getGame(gameNum);
		game.finish();
	}
	//Checks whether the game ended in a victory.
	public static boolean IsGameVictory(int gameNum) {
		Game game = getGame(gameNum);
		return game.isVictory();
	}
	//========================= game =========================
	//========================= Player =========================	
	//Returns the current player number. 
	public static int GameGetCurrentPlayer(int gameNum) {
		Game game = getGame(gameNum);
		return game.getCurrentPlayer();
	}
	//========================= Player =========================
	//========================= Mines =========================	
	//Returns the number of remaining mines on the specified board.
	public static int getRemainingMines(int gameNum, boolean isLeft) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		return board.getRemainingMines();
	}
	//========================= Mines =========================
	//========================= Lives =========================	
	//Returns the number of shared lives remaining in the game.
	public static int getSharedLivesGame(int gameNum) {
		Game game = getGame(gameNum);
		return game.getSharedLives();
	}
	//Updates the number of shared lives in the game.
	public static void UpdateSharedLivesGame(int gameNum,int SharedLives ) {
		Game game = getGame(gameNum);
		game.setSharedLives(SharedLives);
	}
	//========================= Lives =========================
	//========================= Points =========================
	//Adds points to the shared score.
	public static void UpdateSharedPoints(int gameNum,int SharedPointsToAdd) {
		Game game = getGame(gameNum);
		game.addSharedPoints(SharedPointsToAdd);
	}
	//Returns the current shared score.
	public static int getSharedPoints(int gameNum) {
		Game game = getGame(gameNum);
		return game.getSharedPoints();
	}

	// ========================= Surprise Cell Logic =========================
	// get cost:
	public static String getSurpriseCostActivate(int gameNum, boolean isLeft, int row, int col){
		Game game = getGame(gameNum);
		// Deduct activation cost
		int cost = game.getActivationCost();
		return "Activating a Surprise costs"+ cost + " points.";
	}
	// check if the cell is a surprise:
	public static String IsSurprise (int gameNum, boolean isLeft, int row, int col) {
		// Check if this cell is a SURPRISE and has been revealed
		boolean wasSurprise = GetCellType(gameNum, isLeft, row, col).equals("SURPRISE");
		boolean IsRevealed =IsCellRevealed(gameNum, isLeft, row, col);
		// If the surprise has not yet been used, prompt for activation cost
		boolean IsUsed = iscellUsed(gameNum, isLeft, row, col);
		if (!wasSurprise) return "NOT_SURPRISE";
		if (!IsRevealed) return "NOT_REVEALED";
		if (IsUsed) return "ALREADY_USED";
		return "SurpriseCell";
	}
	 //Check if player has enough points 
	public static int CheckActivateCost(int gameNum) {
		Game game = getGame(gameNum);
		int cost = game.getActivationCost();
		 int missing = game.getSharedPoints()- cost;
		return missing;
	}
	//Activates a surprise:
	public static String ActivateSurpriseCell(int gameNum, boolean isLeft, int row, int col) {
		Game game = getGame(gameNum);
		Board board = isLeft ? game.getBoard1() : game.getBoard2();
		Cell cell = board.getCell(row, col);

		int cost = game.getActivationCost();
		
			game.addSharedPoints(-cost);
			// Mark cell as used
			cell.setUsed(true);

			 // 50/50 chance for good or bad effect
			boolean good = Math.random() < 0.5;
			int pointsChanged = game.applySurpriseEffect(good);

			return (good ? "GOOD:" : "BAD:") + pointsChanged ;
		
	}
	// ========================= Question Cell Logic =========================
	//Returns the activation cost for a question or surprise action in the game.
	public static int GetGameSurpriseQuestionCoust(int gameNum) {
		Game game = getGame(gameNum);
		return game.getActivationCost();
	}
	//Activates a question by deducting the activation cost from shared points.
	public static void ActivateQuestion(int gameNum) {
		Game game = getGame(gameNum);
		int cost = game.getActivationCost();
		game.addSharedPoints(-cost);
	}
	// Refunds the activation cost if the question failed to load.
	public static void FaildeToLoadQuestion(int gameNum) {
		Game game = getGame(gameNum);
		int cost = game.getActivationCost();
		game.addSharedPoints(+cost);
	}
	// Indicates whether the game is currently allowed to switch turns.
	private static  boolean canSwitch;
	// Stores a pending action that should be applied after answering a question.
	private static QuestionAction pendingQuestionAction = QuestionAction.NONE;
	// Returns whether the game can switch turns.
	public static boolean isCanSwitch() {
		return canSwitch;
	}
	// Sets whether the game can switch turns.
	public static  void setCanSwitch(boolean canswitch) {
		canSwitch = canswitch;
	}
	// Cancels the current question source and prevents turn switching.
	public static void sourceCancel() {
		setCanSwitch(false);
	}
	// Returns true with a 50% probability.
	private static boolean random50() {
		return Math.random() < 0.5;
	}
	// Defines special actions that can be triggered by question rewards.
	public enum QuestionAction {
	    NONE,
	    REVEAL_RANDOM_MINE,
	    REVEAL_3X3
	}
	public enum QuestionResult {
	    CANCELED,
	    ACTIVATED
	}
	public static QuestionResult handleQuestion(
	        int gameNum, Boolean isLeft, int row, int col, boolean confirmed) {

	    if (!confirmed) {
	        setCanSwitch(false);
	        return QuestionResult.CANCELED;
	    }

	    ActivateQuestion(gameNum);
	    return QuestionResult.ACTIVATED;
	}

	// Returns the currently pending question action.
	public static QuestionAction getPendingQuestionAction() {
		return pendingQuestionAction;
	}
	// Clears any pending question action.
	public static void clearPendingQuestionAction() {
	    pendingQuestionAction = QuestionAction.NONE;
	}
	// Applies scoring, lives, and special actions based on the question result.
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
		 /* ===== SCORING LOGIC (by difficulty) ===== */
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

		 // ===== APPLY RESULTS =====
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
	// Reveals a random hidden mine on the specified board.
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

	/**
	 * Reveals a 3x3 grid of cells randomly on the specified board -> 
	 * Priority is given to revealing the full 3x3 grid whenever possible.
	 * However, if there isn't enough space to reveal a complete 3x3 grid,
	 * the method will reveal a smaller grid that fits within the board boundaries.
	 */

	// Reveals a random 3x3 grid of cells on the specified board with fallback
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

	    // Find the best center (most unrevealed cells in 3x3)
	    int[] bestCenter = null;
	    int maxUnrevealed = 0;
	    
	    for (int[] pos : hiddenCells) {
	        int row = pos[0];
	        int col = pos[1];
	        
	        // Count unrevealed cells in 3x3 around this position
	        int count = 0;
	        for (int dr = -1; dr <= 1; dr++) {
	            for (int dc = -1; dc <= 1; dc++) {
	                int r = row + dr;
	                int c = col + dc;
	                if (r >= 0 && r < size && c >= 0 && c < size) {
	                    if (!board.getCell(r, c).isRevealed()) {
	                        count++;
	                    }
	                }
	            }
	        }
	        
	        // Update best center if this one has more unrevealed cells
	        if (count > maxUnrevealed) {
	            maxUnrevealed = count;
	            bestCenter = pos;
	        }
	    }

	    ArrayList<int[]> revealedCells = new ArrayList<>();

	    if (bestCenter != null && maxUnrevealed >= 3) {
	        // Reveal 3x3 around best center
	        int centerRow = bestCenter[0];
	        int centerCol = bestCenter[1];

	        for (int dr = -1; dr <= 1; dr++) {
	            for (int dc = -1; dc <= 1; dc++) {
	                int r = centerRow + dr;
	                int c = centerCol + dc;
	                if (r >= 0 && r < size && c >= 0 && c < size) {
	                    Cell cell = board.getCell(r, c);
	                    if (!cell.isRevealed()) {
	                        cell.setRevealed(true);
	                        revealedCells.add(new int[]{r, c});

	                        if (cell.getType() == Cell.CellType.MINE && !cell.isCounted()) {
	                            decrementRemainingMinesInBoard(gameNum, isLeft);
	                            setCountedAsCounted(gameNum, isLeft, r, c);
	                        }
	                    }
	                }
	            }
	        }
	    } else {
	        // Fallback: reveal up to 3 random cells
	    	int toReveal = Math.min(3, hiddenCells.size());
	    	ArrayList<int[]> shuffled = new ArrayList<>(hiddenCells);
	    	Collections.shuffle(shuffled);

	    	for (int i = 0; i < toReveal; i++) {
	    	    int[] pos = shuffled.get(i);
	    	    int r = pos[0];
	    	    int c = pos[1];
	    	    Cell cell = board.getCell(r, c);

	    	    if (!cell.isRevealed()) {
	    	        cell.setRevealed(true);
	    	        revealedCells.add(new int[]{r, c});

	    	        if (cell.getType() == Cell.CellType.MINE && !cell.isCounted()) {
	    	            decrementRemainingMinesInBoard(gameNum, isLeft);
	    	            setCountedAsCounted(gameNum, isLeft, r, c);
	    	        }
	    	    }
	    	}
	    }

	    return revealedCells;
	}

}





