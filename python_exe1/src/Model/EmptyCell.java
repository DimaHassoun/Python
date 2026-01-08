package Model;

public class EmptyCell extends Cell {
    
    public EmptyCell(int x, int y) {
        super(x, y);
    }
    
    @Override
    public String getDisplay() {
        if (!revealed) {
            return flagged ? "🚩" : " ";
        }
        return "";
    }
    
    @Override
    public CellType getType() {
        return CellType.EMPTY;
    }
    
    @Override
    public boolean shouldCascade() {
        return true; // Empty cells trigger cascade!
    }
}