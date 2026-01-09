package view; 

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
// A custom JPanel that displays a background image stretched to fit the panel.
public class BackgroundPanel extends JPanel {
    private Image backgroundImage; 

    public BackgroundPanel(String imagePath) {
        try {
            java.net.URL imgURL = getClass().getResource(imagePath); 
            if (imgURL != null) {
                backgroundImage = new ImageIcon(imgURL).getImage();
            } else {
                System.err.println("Background image not found: " + imagePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Paints the background image, scaling it to fit the panel's size.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

