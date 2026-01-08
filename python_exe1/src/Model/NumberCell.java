package Model;

public class NumberCell extends Cell {
    
    private int surroundingMines;
    
    public NumberCell(int x, int y, int surroundingMines) {
        super(x, y);
        this.surroundingMines = surroundingMines;
    }
    
    @Override
    public String getDisplay() {
        if (!revealed) {
            return flagged ? "🚩" : " ";
        }
        return surroundingMines == 0 ? "" : String.valueOf(surroundingMines);
    }
    
    @Override
    public CellType getType() {
        return CellType.NUMBER;
    }
    
    @Override
    public boolean shouldCascade() {
        return false; // Number cells stop cascade
    }
    
    // Number-specific methods
    public int getSurroundingMines() { return surroundingMines; }
    public void setSurroundingMines(int count) { this.surroundingMines = count; }
}