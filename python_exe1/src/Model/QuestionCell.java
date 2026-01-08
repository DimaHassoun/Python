package Model;

public class QuestionCell extends SpecialCell {
    
    public QuestionCell(int x, int y) {
        super(x, y);
    }
    
    @Override
    public String getDisplay() {
        if (!revealed) {
            return flagged ? "🚩" : " ";
        }
        return used ? "✓" : "❓";
    }
    
    @Override
    public CellType getType() {
        return CellType.QUESTION;
    }
}