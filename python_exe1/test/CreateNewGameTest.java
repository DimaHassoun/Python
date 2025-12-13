import Model.Game;
import Model.GameResult;
import controller.GameController;
import controller.GameHistoryController;

import org.junit.Test;
import static org.junit.Assert.*;
  /*
  Unit tests for the createNewGame() function in GameController
  These tests verify correct game initialization, difficulty settings,
  player information, ID generation for each game, and proper handling of invalid inputs
 */

public class CreateNewGameTest {

    @Test
    public void testCreateNewGame_Easy() {
        Game game = GameController.createNewGame("Meson", "Dima", "EASY");
        //Test that a new EASY game is created correctly with all expected values
        
        assertNotNull("Game should not be null", game);

        //Difficulty must be EASY
        assertEquals(Game.Difficulty.EASY, game.getDifficulty());

        //Shared Lives must be 10
        assertEquals(10, game.getSharedLives());

        //Activation cost must be 5
        assertEquals(5, game.getActivationCost());

        //Verify player names are stored correctly
        assertEquals("Meson", game.getPlayer1Name());
        assertEquals("Dima", game.getPlayer2Name());

        //Starting player must be either 1 or 2
        assertTrue(game.getCurrentPlayer() == 1 || game.getCurrentPlayer() == 2);
    }

    @Test
    public void testCreateNewGame_Medium() {
        Game game = GameController.createNewGame("Najwa", "Dima", "MEDIUM");
        //Test that a new MEDIUM game is created correctly with all expected values
        assertNotNull(game);
        assertEquals(Game.Difficulty.MEDIUM, game.getDifficulty());
        assertEquals(8, game.getSharedLives());
        assertEquals(8, game.getActivationCost());
        assertEquals("Najwa", game.getPlayer1Name());
        assertEquals("Dima", game.getPlayer2Name());
        assertTrue(game.getCurrentPlayer() == 1 || game.getCurrentPlayer() == 2);
    }

    @Test
    public void testCreateNewGame_Hard() {
        Game game = GameController.createNewGame("Eslam", "Najwa", "HARD");
        //Test that a new HARD game is created correctly with all expected values
        assertNotNull(game);
        assertEquals(Game.Difficulty.HARD, game.getDifficulty());
        assertEquals(6, game.getSharedLives());
        assertEquals(12, game.getActivationCost());
        assertEquals("Eslam", game.getPlayer1Name());
        assertEquals("Najwa", game.getPlayer2Name());
        assertTrue(game.getCurrentPlayer() == 1 || game.getCurrentPlayer() == 2);
    }

    //Test that invalid difficulty input throws an IllegalArgumentException.
    @Test(expected = IllegalArgumentException.class)
    public void testCreateNewGame_InvalidDifficulty() {
        // Should throw exception
        GameController.createNewGame("Najwa", "Meson", "SUPERHARD");
    }
  
    //Test that an empty difficulty throws an IllegalArgumentException
    @Test(expected = IllegalArgumentException.class)
    public void testCreateNewGame_EmptyDifficulty() {
        GameController.createNewGame("Player1", "Player2", "");
    }
    
    // Test that an null difficulty throws an NullPointerException
    @Test(expected = NullPointerException.class)
    public void testCreateNewGame_NullDifficulty() {
        GameController.createNewGame("Player1", "Player2", null);
    }
    
    //Player1 name null → exception
    @Test(expected = IllegalArgumentException.class)
    public void testCreateNewGame_Player1NameNull() {
        GameController.createNewGame(null, "Player2", "EASY");
    }

    //Player2 name null → exception
    @Test(expected = IllegalArgumentException.class)
    public void testCreateNewGame_Player2NameNull() {
        GameController.createNewGame("Player1", null, "EASY");
    }
    
   
    
    //Test that game IDs increment correctly
    @Test
    public void testGameIdIncrement() {
        Game g1 = GameController.createNewGame("Najwa", "Meson", "EASY");
        GameHistoryController.createHistoryEntry(g1, GameResult.Victory);
        Game g2 = GameController.createNewGame("Eslam", "Najwa", "EASY");
        assertTrue(g2.getId() > g1.getId());
    }

    //Test that new games are added to the activeGames map
    @Test
    public void testGameAddedToActiveGames() {
        Game game = GameController.createNewGame("Meson", "Eslam", "EASY");
        int id = game.getId();
        Game retrieved = GameController.getGame(id);
        assertNotNull("Game should be stored in activeGames map", retrieved);
        assertEquals(game, retrieved);
    }
}

