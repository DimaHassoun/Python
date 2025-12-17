import Model.Game;
import controller.GameController;
import org.junit.Test;
import static org.junit.Assert.*;


//Unit tests for the createNewGame() function in GameController.
public class CreateNewGameTest {

	// TEST ID – TG_1
    // Test that a new EASY game is successfully created and is not null
    @Test
    public void testCreateNewGame_Easy_NotNull() {
        int GameNum= GameController.createNewGame("najwa", "Dima", "EASY").GetGameNum();
        Game game=GameController.getGame(GameNum);
        assertNotNull(game);
    }
    
    // TEST ID – TG_2
    // Test that the difficulty of the created game is set to EASY
    @Test
    public void testCreateNewGame_Easy_Difficulty() {
    	int GameNum= GameController.createNewGame("Meson", "Dima", "EASY").GetGameNum();
    	Game game=GameController.getGame(GameNum);
        assertEquals(Game.Difficulty.EASY, game.getDifficulty());
    }

    // TEST ID – TG_3
    // Test that an EASY game is initialized with 10 shared lives
    @Test
    public void testEasyDifficultyLives() {
    	int GameNum= GameController.createNewGame("najwa", "Meson", "Easy").GetGameNum();
    	Game game=GameController.getGame(GameNum);
        assertEquals(10, game.getSharedLives());
    }

    // TEST ID – TG_4
    // Test that the activation cost for EASY difficulty is set to 5
    @Test
    public void testEasyDifficultyActivationCost() {
    	int GameNum= GameController.createNewGame("najwa", "eslam", "Easy").GetGameNum();
    	Game game=GameController.getGame(GameNum);
        assertEquals(5, game.getActivationCost());
    }

    // TEST ID – TG_5
    // Test that the starting player is randomly set to either player 1 or player 2
    @Test
    public void testCurrentPlayerIsValid() {
    	int GameNum= GameController.createNewGame("najwa", "Meson", "Easy").GetGameNum();
    	Game game=GameController.getGame(GameNum);
        assertTrue(game.getCurrentPlayer() == 1 || game.getCurrentPlayer() == 2);
    }

    // TEST ID – TG_6
    // Test that creating a game with an empty player 1 name throws an IllegalArgumentException
    @Test(expected = IllegalArgumentException.class)
    public void testPlayer1NameEmptyThrowsException() {
        GameController.createNewGame("", "eslam", "Easy");
        assertTrue(true); // unreachable, keeps single assert rule
    }

    // TEST ID – TG_7
    // Test that creating a game with an empty player 2 name throws an IllegalArgumentException
    @Test(expected = IllegalArgumentException.class)
    public void testPlayer2NameEmptyThrowsException() {
        GameController.createNewGame("najwa", "", "Easy");
        assertTrue(true);
    }

    // TEST ID – TG_8
    // Test that creating a game with an empty difficulty string throws an IllegalArgumentException
    @Test(expected = IllegalArgumentException.class)
    public void testDifficultyEmptyThrowsException() {
        GameController.createNewGame("dima", "najwa", "");
        assertTrue(true);
    }

    // TEST ID – TG_9
    // Test that creating a game with an unknown difficulty throws an IllegalArgumentException
    @Test(expected = IllegalArgumentException.class)
    public void testUnknownDifficultyThrowsException() {
        GameController.createNewGame("najwa", "eslam", "Impossible");
        assertTrue(true);
    }
}
