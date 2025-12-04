package Model;

import java.time.LocalDateTime;

public class Game {
    // Enums for difficulty and game state
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    public enum GameState {
        CREATED, IN_PROGRESS, FINISHED
    }

    private Difficulty difficulty;
    private GameState state;
    private int sharedPoints;
    private int sharedLives; // max 10
    private Board board1;
    private Board board2;
    private String player1Name;
    private String player2Name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    private int activationCost; // cost for activating question/surprise cell
    private int currentPlayer; // current player turn
    
    private int id;  // Unique identifier
    
 // New field: block actions if surprise activated
    private boolean surpriseActivatedThisTurn = false;
    
    public Game(int id, Difficulty difficulty, String player1Name, String player2Name) {
        this.id = id;
        this.difficulty = difficulty;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.state = GameState.CREATED;
    }
    
    /**
     * Initialize boards and start the game according to difficulty.
     */
    public void start() {
        int size = 8, mines = 10, questions = 3, surprises = 2, lives = 3;

        switch (difficulty) {
            case EASY:
            	size=9; mines=10; questions=6; surprises=2; lives=10; activationCost=5;
            	break;
            case MEDIUM:
            	size=13; mines=26; questions=7; surprises=3; lives=8; activationCost=8;
                break;
            case HARD:
            	size=16; mines=44; questions=11; surprises=4; lives=6; activationCost=12; 
                break;
        }
        board1 = new Board(size, mines, questions, surprises);
        board2 = new Board(size, mines, questions, surprises);

        this.sharedPoints = 0;
        this.sharedLives = lives;
        this.startTime = LocalDateTime.now();
        this.state = GameState.IN_PROGRESS;
        this.surpriseActivatedThisTurn = false;

    }
    
    public void finish() {
        this.state = GameState.FINISHED;
        this.endTime = LocalDateTime.now();
    }
    
    /**
     * Add points to shared score (can be positive or negative)
     */
    public void addSharedPoints(int points) {
        this.sharedPoints += points;
    }
    
    /**
     * Set shared lives with max cap of 10
     * Extra lives are converted to points based on activation cost
     */
    public void setSharedLives(int lives) {
        if (lives > 10) {
            // Convert extra lives to points
            int extraLives = lives - 10;
            int pointsPerLife = this.activationCost;
            addSharedPoints(extraLives * pointsPerLife);
            this.sharedLives = 10;
        } else {
            this.sharedLives = lives;
        }
    }
    
    // ---------- Surprise Cell ----------
    public int applySurpriseEffect(boolean good) {
        int points;
        switch (difficulty) {
            case EASY -> points = 8;
            case MEDIUM -> points = 12;
            case HARD -> points = 16;
            default -> points = 0;
        }

        if (good) {
            sharedLives += 1;
            sharedPoints += points;
        } else {
            sharedLives -= 1;
            sharedPoints -= points;
            if (sharedLives < 0) sharedLives = 0;
        }

        surpriseActivatedThisTurn = true; // block further actions this turn
        return points;
    }

    public boolean canPerformAction() { return !surpriseActivatedThisTurn; }
    public void setSurpriseActivatedThisTurn(boolean activated) { this.surpriseActivatedThisTurn = activated; }
    
    
    /**
     * Check if game is over (no lives left or all cells revealed)
     */
    public boolean isGameOver() {
        return this.sharedLives <= 0 || isVictory();
    }
    
    /**
     * Check if players won (all non-mine cells revealed on both boards)
     */
    public boolean isVictory() {
    	boolean board1Victory = board1.isCompleted() || board1.getMinesRevealed() == board1.getTotalMines();
    	boolean board2Victory = board2.isCompleted() || board2.getMinesRevealed() == board2.getTotalMines();
    	return board1Victory || board2Victory;
  	  }
    
    // Getters & Setters
    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
    
    public GameState getState() {
        return state;
    }
    
    public void setState(GameState state) {
        this.state = state;
    }
    
    public int getSharedPoints() {
        return sharedPoints;
    }
    
    public void setSharedPoints(int sharedPoints) {
        this.sharedPoints = sharedPoints;
    }
    
    public int getSharedLives() {
        return sharedLives;
    }
    
    public Board getBoard1() {
        return board1;
    }
    
    public void setBoard1(Board board1) {
        this.board1 = board1;
    }
    
    public Board getBoard2() {
        return board2;
    }
    
    public void setBoard2(Board board2) {
        this.board2 = board2;
    }
    
    public String getPlayer1Name() {
        return player1Name;
    }
    
    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }
    
    public String getPlayer2Name() {
        return player2Name;
    }
    
    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public int getActivationCost() {
        return activationCost;
    }
    
    public void setActivationCost(int activationCost) {
        this.activationCost = activationCost;
    }
    
    public int getCurrentPlayer() {
        return currentPlayer;
    }
    
    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    

}