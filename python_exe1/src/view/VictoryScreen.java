package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class VictoryScreen extends JFrame {

    public VictoryScreen(int finalScore ,JFrame previousWindow) {
        setTitle("Victory!");
        setSize(550, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);


        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color c1 = new Color(50, 0, 80);
                Color c2 = new Color(140, 0, 160);
                GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));
        setContentPane(mainPanel);

         // ⭐ A big star
        JLabel starLabel = new JLabel("🥇", SwingConstants.CENTER);
        starLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 100));
        starLabel.setForeground(new Color(255, 204, 0));
        starLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        starLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        mainPanel.add(starLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // VICTORY main text
        JLabel messageLabel = new JLabel("Congratulations!", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Audiowide", Font.BOLD, 38));
        messageLabel.setForeground(new Color(0, 255, 140));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(messageLabel);

        // Additional text
        JLabel subLabel = new JLabel("You win The Game!", SwingConstants.CENTER);
        subLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        subLabel.setForeground(new Color(255, 210, 210));
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(subLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // the final points
        JLabel scoreLabel = new JLabel("Final Score: " + finalScore, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setForeground(new Color(255, 220, 150));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(scoreLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // buttons
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnNewGame = createRoundedButton("Start New Game");
        JButton btnMainMenu = createRoundedButton("Return to Main Menu");

        btnNewGame.addActionListener((ActionEvent e) -> {
        	 dispose();
             if(previousWindow != null) previousWindow.dispose();
             new NewGameScreen().setVisible(true);
        });

        btnMainMenu.addActionListener((ActionEvent e) -> {
        	 dispose();
             if(previousWindow != null) previousWindow.dispose();
             new FirstScreen().setVisible(true);

        });

        buttonsPanel.add(btnNewGame);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonsPanel.add(btnMainMenu);

        mainPanel.add(buttonsPanel);
    }

    // RoundedButton
    private JButton createRoundedButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(120, 60, 200),
                        0, getHeight(), new Color(80, 30, 150)
                );

                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35);

                super.paintComponent(g);
            }

            @Override
            public void setContentAreaFilled(boolean b) {
                super.setContentAreaFilled(false);
            }
        };

        button.setForeground(new Color(255, 235, 130));
        button.setFont(new Font("Arial", Font.BOLD, 22));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(300, 70));
        button.setMaximumSize(new Dimension(300, 70));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);

        return button;
    }
    
    

}
