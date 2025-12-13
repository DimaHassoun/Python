import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import controller.GameController;
import Model.Game;

//============================
// GameControllerSurpriseTest
// JUnit tests for verifying the behavior of Surprise cells in the game.
// Tests include activation of non-surprise cells, unrevealed surprise cells,
// already used surprise cells, and activation when the game cannot perform actions.
//============================

public class GameControllerSurpriseTest {

    private int gameNum;

    @BeforeEach
   void setUp() {
        // Create a new EASY game for testing
        gameNum = GameController.createNewGame("Alice", "Bob", "EASY").getId();
        GameController.getGame(gameNum).start();
    }

    // ============================
    // TEST 1: Activating a non-surprise cell returns NOT_SURPRISE
    // ============================
    @Test
    void testActivateNonSurpriseCell_ReturnsNotSurprise() {
        String result = GameController.ActivateSurpriseCell(gameNum, true, 0, 0); // assume top-left is not surprise
        assertEquals("NOT_SURPRISE", result, "Activating a non-surprise cell should return NOT_SURPRISE");
    }

    // ============================
    // TEST 2: Activating a surprise cell before reveal returns NOT_REVEALED
    // ============================
    @Test
    void testActivateUnrevealedSurpriseCell_ReturnsNotRevealed() {
        Game game = GameController.getGame(gameNum);
        boolean found = false;

        outer:
        for (int r = 0; r < game.getBoard1().getSize(); r++) {
            for (int c = 0; c < game.getBoard1().getSize(); c++) {
                if (GameController.GetCellType(gameNum, true, r, c).equals("SURPRISE")) {
                    String result = GameController.ActivateSurpriseCell(gameNum, true, r, c);
                    assertEquals("NOT_REVEALED", result, "Surprise cell not revealed yet should return NOT_REVEALED");
                    found = true;
                    break outer;
                }
            }
        }
        assertTrue(found, "There must be at least one Surprise cell in the board");
    }

    // ============================
    // TEST 3: Activating a revealed surprise cell updates points appropriately
    // ============================
    @Test
    void testActivateRevealedSurpriseCell_UsesCellAndUpdatesPoints() {
        Game game = GameController.getGame(gameNum);
        boolean found = false;

        outer:
        for (int r = 0; r < game.getBoard1().getSize(); r++) {
            for (int c = 0; c < game.getBoard1().getSize(); c++) {
                if (GameController.GetCellType(gameNum, true, r, c).equals("SURPRISE")) {
                    GameController.RevealCell(gameNum, true, r, c);

                    int beforePoints = game.getSharedPoints();
                    String result = GameController.ActivateSurpriseCell(gameNum, true, r, c);
                    int afterPoints = game.getSharedPoints();

                    assertTrue(result.startsWith("GOOD:") || result.startsWith("BAD:"), 
                        "Result should indicate GOOD or BAD surprise");

                    assertTrue(GameController.iscellUsed(gameNum, true, r, c), "Cell should be marked as used");

                    // Check points based on GOOD or BAD surprise
                    if (result.startsWith("BAD:")) {
                        assertTrue(afterPoints <= beforePoints, 
                            "Points should decrease or stay the same after activating a BAD surprise");
                    } else if (result.startsWith("GOOD:")) {
                        assertTrue(afterPoints >= beforePoints, 
                            "Points should increase or stay the same after activating a GOOD surprise");
                    }

                    found = true;
                    break outer;
                }
            }
        }
        assertTrue(found, "There must be at least one Surprise cell to test");
    }

    // ============================
    // TEST 4: Surprise cell cannot be activated twice
    // ============================
    @Test
    void testActivateUsedSurpriseCell_ReturnsAlreadyUsed() {
        Game game = GameController.getGame(gameNum);
        boolean found = false;

        outer:
        for (int r = 0; r < game.getBoard1().getSize(); r++) {
            for (int c = 0; c < game.getBoard1().getSize(); c++) {
                if (GameController.GetCellType(gameNum, true, r, c).equals("SURPRISE")) {
                    GameController.RevealCell(gameNum, true, r, c);
                    GameController.ActivateSurpriseCell(gameNum, true, r, c);

                    String result = GameController.ActivateSurpriseCell(gameNum, true, r, c);
                    assertEquals("ALREADY_USED", result, "Activating the same surprise cell again should return ALREADY_USED");

                    found = true;
                    break outer;
                }
            }
        }
        assertTrue(found, "There must be at least one Surprise cell to test");
    }

    // ============================
    // TEST 5: Activation result when game cannot perform action (canSwitch = false)
    // ============================
    @Test
    void testActivateSurpriseCell_WhenGameCannotPerformAction() {
        GameController.setCanSwitch(false); 
        Game game = GameController.getGame(gameNum);
        boolean found = false;

        outer:
        for (int r = 0; r < game.getBoard1().getSize(); r++) {
            for (int c = 0; c < game.getBoard1().getSize(); c++) {
                if (GameController.GetCellType(gameNum, true, r, c).equals("SURPRISE")) {
                    GameController.RevealCell(gameNum, true, r, c);

                    String result = GameController.ActivateSurpriseCell(gameNum, true, r, c);

                    assertTrue(result.startsWith("GOOD:") || result.startsWith("BAD:"), 
                        "Surprise activation result should be GOOD or BAD even if game cannot switch");

                    found = true;
                    break outer;
                }
            }
        }
        assertTrue(found, "There must be at least one Surprise cell to test");
    }

}
