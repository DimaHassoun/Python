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

    private void initializeBoard() {
        // Initialize all cells as empty
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = new Cell(i, j, Cell.CellType.EMPTY);
            }
        }

        // Place mines randomly
        placeMines();

        // Calculate numbers
        calculateNumbers();

        // Place question & surprise cells
        placeSpecialCells();
    }

    private void placeMines() {
        Random rand = new Random();
        int placed = 0;
        while (placed < mines) {
            int row = rand.nextInt(size);
            int col = rand.nextInt(size);
            if (board[row][col].getType() != Cell.CellType.MINE) {
                board[row][col].setType(Cell.CellType.MINE);
                placed++;
            }
        }
    }

    private void calculateNumbers() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                if (board[i][j].getType() == Cell.CellType.MINE)
                    continue;

                int mineCount = 0;

                for (int di = -1; di <= 1; di++) {
                    for (int dj = -1; dj <= 1; dj++) {
                        int ni = i + di;
                        int nj = j + dj;

                        if (ni >= 0 && ni < size && nj >= 0 && nj < size) {
                            if (board[ni][nj].getType() == Cell.CellType.MINE) {
                                mineCount++;
                            }
                        }
                    }
                }

                if (mineCount > 0) {
                    board[i][j].setType(Cell.CellType.NUMBER);
                    board[i][j].setSurroundingMines(mineCount);
                } else {
                    board[i][j].setType(Cell.CellType.EMPTY);
                }
            }
        }
    }

    private void placeSpecialCells() {
        Random rand = new Random();
        List<int[]> emptyPositions = new ArrayList<>();

        // Collect empty cells
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j].getType() == Cell.CellType.EMPTY) {
                    emptyPositions.add(new int[]{i, j});
                }
            }
        }

        // Place questions
        for (int q = 0; q < questions && !emptyPositions.isEmpty(); q++) {
            int idx = rand.nextInt(emptyPositions.size());
            int[] pos = emptyPositions.remove(idx);
            board[pos[0]][pos[1]].setType(Cell.CellType.QUESTION);
        }

        // Place surprises
        for (int s = 0; s < surprises && !emptyPositions.isEmpty(); s++) {
            int idx = rand.nextInt(emptyPositions.size());
            int[] pos = emptyPositions.remove(idx);
            board[pos[0]][pos[1]].setType(Cell.CellType.SURPRISE);
        }
    }

    public Cell getCell(int row, int col) {
        if (row >= 0 && row < size && col >= 0 && col < size) {
            return board[row][col];
        }
        return null;
    }

    public void revealCell(int row, int col) {
        if (getCell(row, col) != null) {
            getCell(row, col).setRevealed(true);
        }
    }

    public void cascadeReveal(int row, int col) {
        if (row < 0 || row >= size || col < 0 || col >= size) return;

        Cell cell = board[row][col];
        if (cell.isRevealed() || cell.isFlagged()) return;

        cell.setRevealed(true);

        // Only cascade for cells that behave like empty: EMPTY, SURPRISE, QUESTION
        if (cell.getType() == Cell.CellType.EMPTY ||
            cell.getType() == Cell.CellType.SURPRISE ||
            cell.getType() == Cell.CellType.QUESTION) {

            for (int di = -1; di <= 1; di++) {
                for (int dj = -1; dj <= 1; dj++) {
                    if (di == 0 && dj == 0) continue; 
                    cascadeReveal(row + di, col + dj);
                }
            }
        }
        // Stop cascade automatically if it's a NUMBER or MINE (revealed but no recursion)
    }


    public int getMinesRevealed() {
        int count = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j].getType() == Cell.CellType.MINE && board[i][j].isRevealed()) {
                    count++;
                }
            }
        }
        return count;
    }
    
    /**
     * Check if board is completed (all non-mine cells revealed)
     */
    public boolean isCompleted() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Cell cell = board[i][j];
                // If it's not a mine and not revealed, board is not complete
                if ((cell.getType() != Cell.CellType.MINE) && !cell.isRevealed()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Get remaining mines (total mines - flagged mines)
     */
    public int getRemainingMines() {
        return remainingMines;
    }
    
    /**
     * Decrement remaining mines count (when a mine is flagged)
     */
    public void decrementRemainingMines() {
        if (remainingMines > 0) {
            remainingMines--;
        }
    }
    
    /**
     * Increment remaining mines count (when a flag is removed from mine)
     */
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