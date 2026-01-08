package Model;

/**
 * Abstract base class for special cells (Question and Surprise)
 * They share the "used" property
 */
public abstract class SpecialCell extends Cell {
    
    protected boolean used;
    
    public SpecialCell(int x, int y) {
        super(x, y);
        this.used = false;
    }
    
    @Override
    public boolean shouldCascade() {
        return true; // Special cells cascade like empty cells
    }
    
    // Special cell common behavior
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
}