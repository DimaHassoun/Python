package Model;

public class SurpriseCell extends SpecialCell {
    
    public SurpriseCell(int x, int y) {
        super(x, y);
    }
    
    @Override
    public String getDisplay() {
        if (!revealed) {
            return flagged ? "🚩" : " ";
        }
        return used ? "✓" : "🎁";
    }
    
    @Override
    public CellType getType() {
        return CellType.SURPRISE;
    }
}