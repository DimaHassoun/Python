package view;

import java.awt.Color;

import javax.swing.JFrame;

/**
 * VictoryScreen represents the result screen displayed
 * when the player wins the game.
 *
 * This class is a specialization of ResultScreen and
 * customizes all visual and textual elements to reflect
 * a victory state (title, messages, icon, and colors).
 */

public class VictoryScreen extends ResultScreen {

	/**
	 * Constructs a VictoryScreen instance.
	 *
	 * @param finalScore the final score achieved in the game
	 * @param previousWindow the previous game window to return to
	 * @param player1 name of the first player
	 * @param player2 name of the second player
	 */
	
	public VictoryScreen(int finalScore, JFrame previousWindow, String player1, String player2) {
        super(finalScore, previousWindow, player1, player2);
    }

    protected String getWindowTitle() {
        return "Victory!";
    }

    protected String getIcon() {
        return "🥇";
    }

    protected String getMainMessage() {
        return "Congratulations!";
    }

    protected String getSubMessage() {
        return "You win the game!";
    }

    protected Color getIconColor() {
        return new Color(255, 204, 0);
    }

    protected Color getMainTextColor() {
        return new Color(0, 255, 140);
    }

}


