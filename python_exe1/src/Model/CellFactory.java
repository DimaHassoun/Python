package Model;

/**
 * Factory Method Pattern Implementation
 * Creates appropriate cell subclass based on type
 */
public class CellFactory {
    
    /**
     * Factory method - returns appropriate Cell subclass
     */
    public static Cell createCell(int x, int y, Cell.CellType type) {
        return switch (type) {
            case MINE -> new MineCell(x, y);
            case EMPTY -> new EmptyCell(x, y);
            case NUMBER -> new NumberCell(x, y, 0); //Start with 0 and Mines calculated later
            case QUESTION -> new QuestionCell(x, y);
            case SURPRISE -> new SurpriseCell(x, y);
        };
    }
    
    // Convenience methods
    public static MineCell createMineCell(int x, int y) {
        return new MineCell(x, y);
    }
    
    public static NumberCell createNumberCell(int x, int y, int surroundingMines) {
        return new NumberCell(x, y, surroundingMines);
    }
    
    public static EmptyCell createEmptyCell(int x, int y) {
        return new EmptyCell(x, y);
    }
    
    public static QuestionCell createQuestionCell(int x, int y) {
        return new QuestionCell(x, y);
    }
    
    public static SurpriseCell createSurpriseCell(int x, int y) {
        return new SurpriseCell(x, y);
    }
}