package Model;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class Board {
    private int size;
    private int mines;
    private int questions;
    private int surprises;
    private Cell[][] board;
    private int remainingMines; // Track unflagged mines

    public Board(int size, int mines, int questions, int surprises) {
        this.size = size;
        this.mines = mines;
        this.questions = questions;
        this.surprises = surprises;
        this.remainingMines = mines;
        this.board = new Cell[size][size];
        initializeBoard();
    }
    // Initializes the game board.
    private void initializeBoard() {
        // Initialize all cells as empty
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
            	board[i][j] = CellFactory.createEmptyCell(i, j);
            }
        }
        // Place mines randomly
        placeMines();
        // Calculate numbers
        calculateNumbers();
        // Place question & surprise cells
        placeSpecialCells();
    }
    //  Randomly places mines on the game board.
    private void placeMines() {
        Random rand = new Random();
        int placed = 0;
        while (placed < mines) {
            int row = rand.nextInt(size);
            int col = rand.nextInt(size);
            if (!(board[row][col] instanceof MineCell)) { // Check type!
                board[row][col] = CellFactory.createMineCell(row, col); //get an object of Mine
                placed++;
            }
        }
    }
    // Calculates the number of surrounding mines for each cell on the board.
    private void calculateNumbers() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] instanceof MineCell) {
                    continue;
                }

                int mineCount = countAdjacentMines(i, j);
                
                if (mineCount > 0) {
                    board[i][j] = CellFactory.createNumberCell(i, j, mineCount); //get an object of Cell Containing Number
                }
                // else: remains empty
            }
        }
    }
    private int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int di = -1; di <= 1; di++) {
            for (int dj = -1; dj <= 1; dj++) {
                int ni = row + di;
                int nj = col + dj;
                if (ni >= 0 && ni < size && nj >= 0 && nj < size) {
                    if (board[ni][nj] instanceof MineCell) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
    // Randomly places special cells on the game board.
    private void placeSpecialCells() {
        Random rand = new Random();
        List<int[]> emptyPositions = new ArrayList<>();

        // Collect all empty cells
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] instanceof EmptyCell) {
                    emptyPositions.add(new int[]{i, j});
                }
            }
        }

     // Randomly place QUESTION cells
        for (int q = 0; q < questions && !emptyPositions.isEmpty(); q++) {
            int idx = rand.nextInt(emptyPositions.size());
            int[] pos = emptyPositions.remove(idx);
            board[pos[0]][pos[1]] = CellFactory.createQuestionCell(pos[0], pos[1]);//get an object of Question Cell
        }

     // Randomly place SURPRISE cells
        for (int s = 0; s < surprises && !emptyPositions.isEmpty(); s++) {
            int idx = rand.nextInt(emptyPositions.size());
            int[] pos = emptyPositions.remove(idx);
            board[pos[0]][pos[1]] = CellFactory.createSurpriseCell(pos[0], pos[1]); //get an object of Surprise Cell
        }
    }
    // Returns the cell located at the specified row and column.
    public Cell getCell(int row, int col) {
        if (row >= 0 && row < size && col >= 0 && col < size) {
            return board[row][col];
        }
        return null;
    }
    // Reveals the cell at the specified position.
    public void revealCell(int row, int col) {
        Cell cell = getCell(row, col);
        if (cell != null) {
            cell.reveal(); // Polymorphic call!
        }
    }
    // Recursively reveals cells starting from the specified position.
    public void cascadeReveal(int row, int col) {
        if (row < 0 || row >= size || col < 0 || col >= size) return;

        Cell cell = board[row][col];
        if (cell.isRevealed() || cell.isFlagged()) return;

        cell.reveal(); // Polymorphic!

        // Use polymorphic method instead of type checking!
        if (cell.shouldCascade()) {
            for (int di = -1; di <= 1; di++) {
                for (int dj = -1; dj <= 1; dj++) {
                    if (di == 0 && dj == 0) continue;
                    cascadeReveal(row + di, col + dj);
                }
            }
        }
        // Stop cascade automatically if it's a NUMBER or MINE (revealed but no recursion)
    }
    // Counts and returns the number of mine cells that have been revealed on the board.
    public int getMinesRevealed() {
        int count = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] instanceof MineCell && board[i][j].isRevealed()) {
                    count++;
                }
            }
        }
        return count;
    }
    
    
     // Check if board is completed (all non-mine cells revealed)
    public boolean isCompleted() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Cell cell = board[i][j];
                if (!(cell instanceof MineCell) && !cell.isRevealed()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    
     // Get remaining mines (total mines - flagged mines)
    public int getRemainingMines() {
        return remainingMines;
    }
    
     // Decrement remaining mines count (when a mine is flagged)
    public void decrementRemainingMines() {
        if (remainingMines > 0) {
            remainingMines--;
        }
    }
    
   
     // Increment remaining mines count (when a flag is removed from mine)
    public void incrementRemainingMines() {
        if (remainingMines < mines) {
            remainingMines++;
        }
    }
    
    public int getQuestions() {
        return questions;
    }

    public int getSurprises() {
        return surprises;
    }

    public int getTotalMines() { 
        return mines; 
    }
    
    public int getSize() { 
        return size; 
    }
}