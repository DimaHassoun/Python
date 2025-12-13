package view; 

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class BackgroundPanel extends JPanel {
    private BufferedImage image;

    public BackgroundPanel(String imagePath) {
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        setLayout(null); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

