package view; 

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
// A custom JPanel that displays a background image stretched to fit the panel.
public class BackgroundPanel extends JPanel {
    private BufferedImage image;
    // Constructs a BackgroundPanel with the specified image.
    public BackgroundPanel(String imagePath) {
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        setLayout(null); 
    }
    // Paints the background image, scaling it to fit the panel's size.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

