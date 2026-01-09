package view;

import java.awt.Color;

import javax.swing.JFrame;

/**
 * DefeatScreen represents the result screen displayed
 * when the player loses the game.
 *
 * This class extends ResultScreen and customizes
 * all visual and textual elements to reflect
 * a defeat state (title, messages, icon, and colors).
 */

public class DefeatScreen extends ResultScreen {
	
	/**
	 * Constructs a DefeatScreen instance.
	 *
	 * @param finalScore the final score achieved in the game
	 * @param previousWindow the previous game window to return to
	 * @param player1 name of the first player
	 * @param player2 name of the second player
	 */

    public DefeatScreen(int finalScore, JFrame previousWindow, String player1, String player2) {
        super(finalScore, previousWindow, player1, player2);
    }

    protected String getWindowTitle() {
        return "Game Over";
    }

    protected String getIcon() {
        return "💔";
    }

    protected String getMainMessage() {
        return "GAME OVER!";
    }

    protected String getSubMessage() {
        return "You lost all your lives";
    }

    protected Color getIconColor() {
        return new Color(255, 100, 120);
    }

    protected Color getMainTextColor() {
        return new Color(255, 230, 80);
    }

}


