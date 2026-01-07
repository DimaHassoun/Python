package view;

import java.awt.Color;

import javax.swing.JFrame;

public class VictoryScreen extends ResultScreen {

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


