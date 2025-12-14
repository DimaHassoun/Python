package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

// A customizable JPanel with rounded corners, optional background color, and optional border.
public class RoundedPanel extends JPanel {

    private Color backgroundColor;
    private int cornerRadius;
    private Color borderColor = Color.BLACK; // optional border
    private int borderThickness = 2;
 // Constructor with corner radius
    public RoundedPanel(int radius) {
        super();
        cornerRadius = radius;
        setOpaque(false); // allows custom painting of rounded corners
    }
    // Constructor with corner radius and background color
    public RoundedPanel(int radius, Color bgColor) {
        super();
        cornerRadius = radius;
        backgroundColor = bgColor;
        setOpaque(false);  // allows custom painting of rounded corners
    }
 // Set or change background color
    public void setBackgroundColor(Color bg) {
        backgroundColor = bg;
        repaint();
    }
 // Set or change border color
    public void setBorderColor(Color color) {
        borderColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        // Enable anti-aliasing for smooth rounded corners
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

     // Fill rounded rectangle with background color
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        // draw border
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(borderThickness));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

        g2.dispose();
    }
}
