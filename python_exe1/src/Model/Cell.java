package Model;

public class Cell {

	public enum CellType { MINE, NUMBER, EMPTY, SURPRISE, QUESTION }

	private int x;
	private int y;

	private CellType type;
	private int surroundingMines;  // מספר מוקשים מסביב (0–8)

	private boolean revealed;
	private boolean flagged;
	private boolean used; // לשימוש בהפתעה/שאלה
	private boolean counted; // מסמן אם מוקש כבר נחשב בהפחתת remainingMines

	public Cell(int x, int y, CellType type) {
		this.x = x;
		this.y = y;
		this.type = type;
		this.surroundingMines = 0;
		this.revealed = false;
		this.flagged = false;
		this.used = false;
		this.counted = false;
	}

	// ---------- Getters & Setters ----------

	public int getX() { return x; }
	public void setX(int x) { this.x = x; }

	public int getY() { return y; }
	public void setY(int y) { this.y = y; }

	public CellType getType() { return type; }
	public void setType(CellType type) { this.type = type; }

	public int getSurroundingMines() { return surroundingMines; }
	public void setSurroundingMines(int surroundingMines) { this.surroundingMines = surroundingMines; }

	public boolean isRevealed() { return revealed; }
	public void setRevealed(boolean revealed) { this.revealed = revealed; }

	public boolean isFlagged() { return flagged; }
	public void setFlagged(boolean flagged) { this.flagged = flagged; }

	public boolean isUsed() { return used; }
	public void setUsed(boolean used) { this.used = used; }

	public boolean isCounted() { return counted; }
	public void setCounted(boolean counted) { this.counted = counted; }

	// ---------- Display for GUI ----------

	public String getDisplay() {
		if (!revealed) {
			return flagged ? "🚩" : " ";
		}

		switch (type) {
		case MINE: return "💣";
		case EMPTY: return "";
		case NUMBER: return surroundingMines == 0 ? "" : String.valueOf(surroundingMines);
		case SURPRISE: return used ? "✓" : "🎁";
		case QUESTION: return used ? "✓" : "❓";
		}
		return "";
	}

}