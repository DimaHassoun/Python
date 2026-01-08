package Model;

public class MineCell extends Cell {
    
    private boolean counted; // Has this mine been counted in remainingMines?
    
    public MineCell(int x, int y) {
        super(x, y);
        this.counted = false;
    }
    
    @Override
    public String getDisplay() {
        if (!revealed) {
            return flagged ? "🚩" : " ";
        }
        return "💣";
    }
    
    @Override
    public CellType getType() {
        return CellType.MINE;
    }
    
    @Override
    public boolean shouldCascade() {
        return false; // Mines don't cascade
    }
    
    // Mine-specific methods
    public boolean isCounted() { return counted; }
    public void setCounted(boolean counted) { this.counted = counted; }
}