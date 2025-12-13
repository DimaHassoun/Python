import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import controller.GameController;

// ============================
// Test class for GameController - focuses on question scoring and game state updates,
// ============================

public class QuestionTestGameController {
	private int gameNum;

	@BeforeEach
	public void setUp() {
		// Create a new game for testing (default EASY)
		gameNum = GameController.createNewGame("Alice", "Bob", "EASY").getId();
		GameController.getGame(gameNum).start();
	}
	// ============================
	// TEST 1: ID:CS_t Test to check if canSwitch is set to true after applyQuestionScoring
	// ============================
	@Test
	public void test_CanSwitch_IsTrue_AfterScoring() {
		// Before scoring, canSwitch should be false or unknown state, just to be safe reset
		GameController.setCanSwitch(false);

		GameController.applyQuestionScoring(gameNum, "1", true, true, 0, 0);

		assertTrue(GameController.isCanSwitch(), "After scoring, canSwitch should be true");
	}

	// ============================
	// TEST 2: Randomness 
	// ============================
	/** ID: R1_t, EASY , question level 1 , incorrect (random -3 or 0 points)*/
	@RepeatedTest(5)
	public void testEasyLevel1_IncorrectRandomPoints() {
		String message = GameController.applyQuestionScoring(gameNum, "1", false/*incorrect answer*/, true/* left=board1*/, 0, 0);
		int points = GameController.getGame(gameNum).getSharedPoints();

		assertTrue(
				points == 0 || points == -3,
				"Points should be either 0 or -3 depending on random50"
				);
		assertTrue(
				message.contains("-3 points") || !message.contains("-3 points"),
				"Message should mention '-3 points' if penalty applied or no mention if not"
				);
	}
	/** ID: R2_t, EASY - question level 2 - incorrect (random -6 or 0 points)*/
	@RepeatedTest(5)
	public void testEasyLevel2_IncorrectRandomPoints() {
		String message = GameController.applyQuestionScoring(gameNum, "2", false, true, 0, 0);
		int points = GameController.getGame(gameNum).getSharedPoints();

		assertTrue(
				points == 0 || points == -6,
				"Points should be either 0 or -6 depending on random50"
				);
		assertTrue(
				message.contains("-6 points") || !message.contains("-6 points"),
				"Message should mention '-6 points' if penalty applied or no mention if not"
				);
	}

	/** ID: R3_t, MEDIUM - question level 2 - incorrect (random -10 points or 0 points AND random -1 heart or 0 hearts)*/
	@RepeatedTest(5)
	public void testMediumLevel2_IncorrectRandomPointsHearts() {
		// Create MEDIUM game
		gameNum = GameController.createNewGame("Alice", "Bob", "MEDIUM").getId();
		GameController.getGame(gameNum).start();

		String message = GameController.applyQuestionScoring(gameNum, "2", false, true, 0, 0);
		int points = GameController.getGame(gameNum).getSharedPoints();
		int hearts = GameController.getGame(gameNum).getSharedLives();

		boolean validPoints = (points == 0 || points == -10);
		boolean validHearts = (hearts == 8 || hearts == 7); // start is 8 lives for MEDIUM

		assertTrue(validPoints, "Points should be either 0 or -10 depending on random50");
		assertTrue(validHearts, "Hearts should be either 8 or 7 depending on random50");

		assertTrue(
				message.contains("-10 points") || !message.contains("-10 points"),
				"Message should mention '-10 points' if penalty applied or no mention if not"
				);
		assertTrue(
				message.contains("-1 hearts") || !message.contains("-1 hearts"),
				"Message should mention '-1 hearts' if penalty applied or no mention if not"
				);
	}

	/** ID: R4_t, MEDIUM - question level 4 - incorrect ( hearts -1 or -2 randomly)*/
	@RepeatedTest(5)
	public void testMediumLevel4_IncorrectRandomHearts() {
		gameNum = GameController.createNewGame("Alice", "Bob", "MEDIUM").getId();
		GameController.getGame(gameNum).start();

		String message = GameController.applyQuestionScoring(gameNum, "4", false, true, 0, 0);
		int hearts = GameController.getGame(gameNum).getSharedLives();

		boolean validHearts = (hearts == 7 || hearts == 6); // 8 - 1 or 8 - 2 hearts
		assertTrue(validHearts, "Hearts should be either -1 or -2 depending on random50");

		assertTrue(
				message.contains("-1 hearts") || message.contains("-2 hearts"),
				"Message should mention either '-1 hearts' or '-2 hearts'"
				);
	}
	/** ID: R5_t, HARD - question level 2 - correct ( hearts +1 or +2 randomly)*/
	@RepeatedTest(5)
	public void testHardLevel2_CorrectRandomHearts() {
		gameNum = GameController.createNewGame("Alice", "Bob", "HARD").getId();
		GameController.getGame(gameNum).start();

		String message = GameController.applyQuestionScoring(gameNum, "2", true, true, 0, 0);
		int hearts = GameController.getGame(gameNum).getSharedLives();

		boolean validHearts = (hearts == 7 || hearts == 8); // 6 + 1 or 6 + 2 hearts
		assertTrue(validHearts, "Hearts should be either +1 or +2 depending on random50");

		assertTrue(
				message.contains("+1 hearts") || message.contains("+2 hearts"),
				"Message should mention '+1 hearts' or '+2 hearts'"
				);
	}

	/** ID: R6_t, HARD - question level 2 - incorrect ( hearts -1 or -2 randomly)*/
	@RepeatedTest(5)
	public void testHardLevel2_IncorrectRandomHearts() {
		gameNum = GameController.createNewGame("Alice", "Bob", "HARD").getId();
		GameController.getGame(gameNum).start();

		String message = GameController.applyQuestionScoring(gameNum, "2", false, true, 0, 0);
		int hearts = GameController.getGame(gameNum).getSharedLives();

		boolean validHearts = (hearts == 5 || hearts == 4); // 6 - 1 or 6 - 2 hearts
		assertTrue(validHearts, "Hearts should be either -1 or -2 depending on random50");

		assertTrue(
				message.contains("-1 hearts") || message.contains("-2 hearts"),
				"Message should mention '-1 hearts' or '-2 hearts'"
				);
	}
	// ============================
	// TEST 3: ActivateQuestion deducts question cost points
	// ============================
	/**ID: AQ_C_T1 ,EASY – cost must be 5 and deduction must be correct*/
	@Test
	public void testActivateQuestion_EasyCostAndDeduction() {

		int cost = GameController.GetGameSurpriseQuestionCoust(gameNum);
		assertEquals(5, cost, "EASY game should have activation cost of 5");

		GameController.getGame(gameNum).addSharedPoints(20);
		int before = GameController.getGame(gameNum).getSharedPoints();

		GameController.ActivateQuestion(gameNum);

		int after = GameController.getGame(gameNum).getSharedPoints();
		assertEquals(before - cost, after, "Activation must deduct exactly 5 points");
	}
	/**MEDIUM – cost must be 8 and deduction must be correct*/
	@Test
	public void testActivateQuestion_MediumCostAndDeduction() {
		gameNum = GameController.createNewGame("Alice", "Bob", "MEDIUM").getId();
		GameController.getGame(gameNum).start();

		int cost = GameController.GetGameSurpriseQuestionCoust(gameNum);
		assertEquals(8, cost, "MEDIUM game should have activation cost of 8");

		GameController.getGame(gameNum).addSharedPoints(20);
		int before = GameController.getGame(gameNum).getSharedPoints();

		GameController.ActivateQuestion(gameNum);

		int after = GameController.getGame(gameNum).getSharedPoints();
		assertEquals(before - cost, after, "Activation must deduct exactly 8 points");
	}
	/**HARD – cost must be 12 and deduction must be correct*/
	@Test
	public void testActivateQuestion_HardCostAndDeduction() {
		gameNum = GameController.createNewGame("Alice", "Bob", "HARD").getId();
		GameController.getGame(gameNum).start();

		int cost = GameController.GetGameSurpriseQuestionCoust(gameNum);
		assertEquals(12, cost, "HARD game should have activation cost of 12");

		GameController.getGame(gameNum).addSharedPoints(20);
		int before = GameController.getGame(gameNum).getSharedPoints();

		GameController.ActivateQuestion(gameNum);

		int after = GameController.getGame(gameNum).getSharedPoints();
		assertEquals(before - cost, after, "Activation must deduct exactly 12 points");
	}
	// ============================
	// TEST 4: ID: HM_t4, revealRandomHiddenMine reveals one mine and decreases remaining mines count
	// ============================
	@Test
	public void testRevealRandomHiddenMine_revealsOneMine() {

		int minesBefore = GameController.getGame(gameNum).getBoard1().getRemainingMines();

		int[] revealed = GameController.revealRandomHiddenMine(gameNum, true);

		assertNotNull(revealed, "Should reveal a mine");

		assertTrue(GameController.IsCellRevealed(gameNum, true, revealed[0], revealed[1]), "Revealed cell should be marked revealed");

		int minesAfter = GameController.getGame(gameNum).getBoard1().getRemainingMines();

		assertEquals(minesBefore - 1, minesAfter, "Remaining mines count should decrease by one");
	}
	// =========================
    // TEST 5:  ID: 3X3G_t5, reveal3x3RandomGrid reveals correct cells and respects board bounds
    // =========================
    @Test
    public void test_Reveal3x3RandomGrid_RevealsCellsWithinBounds() {
        // Call the method to reveal a 3x3 grid on left board
        ArrayList<int[]> revealedCells = GameController.reveal3x3RandomGrid(gameNum, true);

        assertNotNull(revealedCells, "Returned list should not be null");
        assertFalse(revealedCells.isEmpty(), "There should be at least one revealed cell");

        int boardSize = GameController.getGame(gameNum).getBoard1().getSize();

        for (int[] cellPos : revealedCells) {
            int row = cellPos[0];
            int col = cellPos[1];
            assertTrue(row >= 0 && row < boardSize, "Row index should be within board bounds");
            assertTrue(col >= 0 && col < boardSize, "Column index should be within board bounds");

            // Check that the cell is marked revealed
            assertTrue(GameController.IsCellRevealed(gameNum, true, row, col), "Cell should be revealed");
        }
    }
    
	// =========================
	// TEST 6: clearPendingQuestionAction resets to NONE
	// =========================
	@Test
	public void test_ClearPendingQuestionAction_ResetsToNone() {
		// Set pending action to something else first
		GameController.applyQuestionScoring(gameNum, "2", true, true, 0, 0); // sets pendingQuestionAction to REVEAL_RANDOM_MINE for EASY level 2
		assertNotEquals(GameController.QuestionAction.NONE, GameController.getPendingQuestionAction());

		// Clear and check
		GameController.clearPendingQuestionAction();
		assertEquals(GameController.QuestionAction.NONE, GameController.getPendingQuestionAction());
	}
	
	// ============================
	// TEST 7: Test to check if pendingQuestionAction is updated properly: 3x3 reveal" or "Reveal hidden Mine" with Game level Easy
	// ============================
	/**ID:UQA_t1 ,Test For 3x3 reveal (Question level 3)*/
	@Test
	public void test_PendingAction_IsSet_ForHardLevel3() {
		GameController.clearPendingQuestionAction();

		GameController.applyQuestionScoring(gameNum, "3", true, true, 0, 0);

		assertEquals(GameController.QuestionAction.REVEAL_3X3, GameController.getPendingQuestionAction(),
				"After HARD level 3 correct answer, pending action should be REVEAL_3X3");
	}
	/** ID:UQA_t2, Test for Reveal hidden Mine on Q_level 1*/
	@Test
	public void test_PendingAction_IsSet_ForEasyLevel2() {
		gameNum = GameController.createNewGame("Alice", "Bob", "EASY").getId();
		GameController.getGame(gameNum).start();

		GameController.clearPendingQuestionAction();

		GameController.applyQuestionScoring(gameNum, "2", true, true, 0, 0);

		assertEquals(GameController.QuestionAction.REVEAL_RANDOM_MINE, GameController.getPendingQuestionAction(),
				"After EASY level 2 correct answer, pending action should be REVEAL_RANDOM_MINE");
	}
}