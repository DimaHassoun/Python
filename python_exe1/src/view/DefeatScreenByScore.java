package view;

import java.awt.Color;

import javax.swing.JFrame;

public class DefeatScreenByScore extends ResultScreen {

    public DefeatScreenByScore(int finalScore, JFrame previousWindow, String player1, String player2) {
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
        return "You reached the negative score limit";
    }

    protected Color getIconColor() {
        return new Color(255, 100, 120);
    }

    protected Color getMainTextColor() {
        return new Color(255, 230, 80);
    }

}