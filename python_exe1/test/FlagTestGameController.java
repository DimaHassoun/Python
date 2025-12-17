import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import controller.GameController;

// ============================
// Test class for GameController - focuses on handleFlag method
// ============================

public class FlagTestGameController {
    private int gameNum;

    @BeforeEach
    public void setUp() {
        // Creates a new game instance with two players and EASY difficulty
        gameNum = GameController.createNewGame("Player1", "Player2", "EASY").GetGameNum();
    }

    /** ID: UF_t1
     * Test that handleFlag returns UNFLAGGED when cell is already flagged
     */
    @Test
    public void test_HandleFlag_AlreadyFlagged_ReturnsUnflagged() {
        GameController.FlaggedCell(gameNum, true, 0, 0);
        GameController.FlagResult result = GameController.handleFlag(gameNum, true, 0, 0);
        assertEquals(GameController.FlagResult.UNFLAGGED, result, "Flagging an already flagged cell should return UNFLAGGED");
    }

    /** ID: UF_t2
     * Test that handleFlag unflags the cell when already flagged
     */
    @Test
    public void test_HandleFlag_AlreadyFlagged_CellIsUnflagged() {
        GameController.FlaggedCell(gameNum, true, 0, 0);
        GameController.handleFlag(gameNum, true, 0, 0);
        assertFalse(GameController.IsCellFlagged(gameNum, true, 0, 0), "Cell should be unflagged after handleFlag");
    }

    /** ID: UF_t3
     * Test that handleFlag reveals the cell when unflagging
     */
    @Test
    public void test_HandleFlag_AlreadyFlagged_CellIsRevealed() {
        GameController.FlaggedCell(gameNum, true, 0, 0);
        GameController.handleFlag(gameNum, true, 0, 0);
        assertTrue(GameController.IsCellRevealed(gameNum, true, 0, 0), "Cell should be revealed after unflagging");
    }

    /** ID: FM_t1
     * Test that handleFlag flags a mine cell and marks it flagged
     */
    @Test
    public void test_HandleFlag_MineCell_CellIsFlagged() {
        int row = 1, col = 1; // known mine coordinate
        GameController.handleFlag(gameNum, true, row, col);
        assertTrue(GameController.IsCellFlagged(gameNum, true, row, col), "Mine cell should be flagged after handleFlag");
    }

    /** ID: FNM_t1
     * Test that handleFlag returns FLAGGED_NOT_MINE when flagging empty cell
     */
    @Test
    public void test_HandleFlag_EmptyCell_ReturnsFlaggedNotMine() {
        int row = 0, col = 1;
        GameController.FlagResult result = GameController.handleFlag(gameNum, true, row, col);
        assertEquals(GameController.FlagResult.FLAGGED_NOT_MINE, result, "Flagging a non-mine cell should return FLAGGED_NOT_MINE");
    }

    /** ID: FNM_t2
     * Test that handleFlag flags the non-mine cell
     */
    @Test
    public void test_HandleFlag_NonMineCell_CellIsFlagged() {
        int row = 0, col = 1;
        GameController.handleFlag(gameNum, true, row, col);
        assertTrue(GameController.IsCellFlagged(gameNum, true, row, col), "Non-mine cell should be flagged after handleFlag");
    }

    /** ID: FNM_t3
     * Test that handleFlag does not reveal non-mine cell
     */
    @Test
    public void test_HandleFlag_NonMineCell_CellNotRevealed() {
        int row = 0, col = 1;
        GameController.handleFlag(gameNum, true, row, col);
        assertFalse(GameController.IsCellRevealed(gameNum, true, row, col), "Non-mine cell should not be revealed");
    }
}
