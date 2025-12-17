import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import controller.GameController;

// ============================
// Test class for GameController - focuses on question scoring and game state updates,
// ============================

public class QuestionTestGameController {
	private int gameNum;

	@BeforeEach
	public void setUp() {
		// Creates a new game instance with two players and a selected difficulty level (default in this Test EASY)
		gameNum = GameController.createNewGame("Dima", "Mayson", "EASY").GetGameNum();
	}
	
	/** ID:CS_t; 
	* Test to check if canSwitch is set to true after applyQuestionScoring
	*/
	@Test
	public void test_CanSwitch_IsTrue_AfterScoring() {
		// Before scoring, canSwitch should be false or unknown state, just to be safe reset
		GameController.setCanSwitch(false);
		//Applies scoring logic after a question is answered and updates the game state accordingly.
		GameController.applyQuestionScoring(gameNum, "1", true, true, 0, 0);
		assertTrue(GameController.isCanSwitch(), "After scoring, canSwitch should be true");
	}
	// ============================
	// Testing ActivateQuestion deducts question cost points according to game level
	// ============================
	/**ID: AQ_C_T1;
	 * EASY – cost must be 5 and deduction must be correct
	 * Game Level: (default in this Test EASY)
	 */
	@Test
	public void testActivateQuestion_EasyCostAndDeduction() {
		//cost_easy=5
		int cost = GameController.GetGameSurpriseQuestionCoust(gameNum);
		//Before deducting points
		int before = GameController.getGame(gameNum).getSharedPoints();
		//deducts its cost from shared points
		GameController.ActivateQuestion(gameNum);
		//after deducting points
		int after = GameController.getGame(gameNum).getSharedPoints();
		assertEquals(before - cost, after, "Activation must deduct exactly 5 points");
	}
	/**ID: AQ_C_T2;
	 * MEDIUM – cost must be 8 and deduction must be correct
	 * Game Level: MEDIUM
	 */
	@Test
	public void testActivateQuestion_MediumCostAndDeduction() {
		gameNum = GameController.createNewGame("Alice", "Bob", "MEDIUM").GetGameNum();
		//cost_easy=8
		int cost = GameController.GetGameSurpriseQuestionCoust(gameNum);
		int before = GameController.getGame(gameNum).getSharedPoints();
		GameController.ActivateQuestion(gameNum);
		int after = GameController.getGame(gameNum).getSharedPoints();
		assertEquals(before - cost, after, "Activation must deduct exactly 8 points");
	}
	/**ID: AQ_C_T3;
	 * HARD – cost must be 12 and deduction must be correct
	 * Game Level: HARD
	 */
	@Test
	public void testActivateQuestion_HardCostAndDeduction() {
		gameNum = GameController.createNewGame("Dima", "Eslam", "HARD").GetGameNum();
		int cost = GameController.GetGameSurpriseQuestionCoust(gameNum);
		GameController.getGame(gameNum).addSharedPoints(20);
		int before = GameController.getGame(gameNum).getSharedPoints();
		GameController.ActivateQuestion(gameNum);
		int after = GameController.getGame(gameNum).getSharedPoints();
		assertEquals(before - cost, after, "Activation must deduct exactly 12 points");
	}
	// ============================
	// Reveal Random Hidden Mine
	// ============================
	/**ID: HM_t1;
	 * Test revealRandomHiddenMine reveals one mine on the selected board.
	 * Game Level: (default in this Test EASY)
	 */
	@Test
	public void testRevealRandomHiddenMine_revealsOneMine() {
		int[] revealed = GameController.revealRandomHiddenMine(gameNum, true);
		assertNotNull(revealed, "Should reveal a mine");
	}
	/**ID: HM_t2;
	 * Test revealRandomHiddenMine: Checks whether a specific cell Marked as revealed.
	 * Game Level: (default in this Test EASY)
	 */
	@Test
	public void testRevealRandomHiddenMine_Marked_Revealed() {
		int[] revealed = GameController.revealRandomHiddenMine(gameNum, true);
		assertTrue(GameController.IsCellRevealed(gameNum, true, revealed[0], revealed[1]), "Revealed cell should be marked revealed");
	}
	/**ID: HM_t3;
	 * Test revealRandomHiddenMine: Checks whether game decreases remaining mines count
	 * Game Level: (default in this Test EASY)
	 */
	@Test
	public void testRevealRandomHiddenMine_Decrease_Remaining() {
		int minesBefore = GameController.getGame(gameNum).getBoard1().getRemainingMines();
		GameController.revealRandomHiddenMine(gameNum, true);
		int minesAfter = GameController.getGame(gameNum).getBoard1().getRemainingMines();
		assertEquals(minesBefore - 1, minesAfter, "Remaining mines count should decrease by one");
	}
	// ============================
	// Reveal Random 3X3
	/**Note: 
	 * Reveals a 3x3 grid of cells randomly on the specified board -> 
	 * Priority is given to revealing the full 3x3 grid whenever possible.
	 * However, if there isn't enough space to reveal a complete 3x3 grid,
	 * the method will reveal a smaller grid that fits within the board boundaries.
	 */
	// ============================
	/**ID: 3X3G_t1;
	 * Test Reveals a random 3×3 grid Return grid.
	 * Game Level: (default in this Test EASY)
	 */
	@Test
	public void test_Reveal3x3RandomGrid_RevealsCellsReturned() {
		ArrayList<int[]> revealedCells = GameController.reveal3x3RandomGrid(gameNum, true);
		assertNotNull(revealedCells, "Returned list should not be null");
	}
	/**ID: 3X3G_t2;
	 * Test Reveals 3X3: There should be at least one revealed cell
	 * Game Level: (default in this Test EASY)
	 */
	@Test
	public void test_Reveal3x3RandomGrid_RevealsCellsAtLeastOne() {
		// Call the method to reveal a 3x3 grid on left board
		ArrayList<int[]> revealedCells = GameController.reveal3x3RandomGrid(gameNum, true);
		assertFalse(revealedCells.isEmpty(), "There should be at least one revealed cell");
	}
	// ============================
	// Updating Pending Question Action (NONE,REVEAL 3X3,REVEAL MINE)
	// ============================
	/**ID: CPQA_ t;
	 * clearPendingQuestionAction resets to NONE
	 * Game Level: (default in this Test EASY)
	 */
	@Test
	public void test_ClearPendingQuestionAction_ResetsToNone() {
		// Clear and check after answering
		GameController.clearPendingQuestionAction();
		assertEquals(GameController.QuestionAction.NONE, GameController.getPendingQuestionAction());
	}
	/**ID: UQA_t1;
	 * Test For 3x3 reveal
	 * (Game Level: EASY and Question level 3)
	 * Check if pendingQuestionAction is updated properly: 3x3 reveal
	 */
	@Test
	public void test_PendingAction_IsSet_ForHardLevel3() {
		GameController.clearPendingQuestionAction();
		GameController.applyQuestionScoring(gameNum, "3", true, true, 0, 0);
		assertEquals(GameController.QuestionAction.REVEAL_3X3, GameController.getPendingQuestionAction(),
				"After HARD level 3 correct answer, pending action should be REVEAL_3X3");
	}
	/** ID: UQA_t2;
	 * (Game Level:EASY and Question level 2)
	 *  Test for Reveal hidden Mine
	 */
	@Test
	public void test_PendingAction_IsSet_ForEasyLevel2() {
		gameNum = GameController.createNewGame("Dima", "Najwa", "EASY").GetGameNum();

		GameController.clearPendingQuestionAction();
		GameController.applyQuestionScoring(gameNum, "2", true, true, 0, 0);
		assertEquals(GameController.QuestionAction.REVEAL_RANDOM_MINE, GameController.getPendingQuestionAction(),
				"After EASY level 2 correct answer, pending action should be REVEAL_RANDOM_MINE");
	}
}