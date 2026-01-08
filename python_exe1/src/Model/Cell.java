package Model;

/**
 * Abstract base class for all cell types
 * Contains common properties and behavior shared by all cells
 */
public abstract class Cell {
    
    // Common properties - ALL cells have these
    protected int x;
    protected int y;
    protected boolean revealed;
    protected boolean flagged;
    
    // Constructor
    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.revealed = false;
        this.flagged = false;
    }
    
    // ========== COMMON BEHAVIOR ==========
    
    /**
     * Reveals the cell - common to ALL cell types
     */
    public void reveal() {
        this.revealed = true;
    }
    
    /**
     * Toggles flag state - common to ALL cell types
     */
    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }
    
    // ========== ABSTRACT METHODS (must be implemented by subclasses) ==========
    
    /**
     * Returns the display representation of this cell
     * Each cell type has its own display logic
     */
    public abstract String getDisplay();
    
    /**
     * Returns the type of this cell
     * Used for type checking when needed
     */
    public abstract CellType getType();
    
    /**
     * Determines if this cell should trigger cascade reveal
     * Empty-like cells return true, others return false
     */
    public abstract boolean shouldCascade();
    
    // ========== GETTERS (common to all) ==========
    
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isRevealed() { return revealed; }
    public boolean isFlagged() { return flagged; }
    
    // Keep the enum for type identification
    public enum CellType {
        MINE, NUMBER, EMPTY, SURPRISE, QUESTION
    }
}