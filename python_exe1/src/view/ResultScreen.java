package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public abstract class ResultScreen extends JFrame {

    protected int finalScore;
    protected JFrame previousWindow;

    public ResultScreen(int finalScore, JFrame previousWindow) {
        this.finalScore = finalScore;
        this.previousWindow = previousWindow;

        setupFrame();
        JPanel mainPanel = createMainPanel();
        setContentPane(mainPanel);

        addIcon(mainPanel);
        addMainMessage(mainPanel);
        addSubMessage(mainPanel);
        addScore(mainPanel);
        addButtons(mainPanel);
    }

    // ======================= Template Method setup ============================
    private void setupFrame() {
        setTitle(getWindowTitle());
        setSize(550, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    // ======================= Abstract methods ==================================
    protected abstract String getWindowTitle();
    protected abstract String getIcon();
    protected abstract String getMainMessage();
    protected abstract String getSubMessage();

    // ======================= Abstract methods for colors =======================
    protected abstract Color getIconColor();
    protected abstract Color getMainTextColor();

    // ======================= Common UI code ===================================
    private JPanel createMainPanel() {
        Color[] colors =  new Color[]{
                new Color(50, 0, 80),
                new Color(140, 0, 160)
            };

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, colors[0],
                        0, getHeight(), colors[1]
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));
        return panel;
    }

    //Icon
    private void addIcon(JPanel panel) {
        JLabel label = new JLabel(getIcon(), SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 100));
        label.setForeground(getIconColor());
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    //Main Massage
    private void addMainMessage(JPanel panel) {
        JLabel label = new JLabel(getMainMessage(), SwingConstants.CENTER);
        label.setFont(new Font("Audiowide", Font.BOLD, 38));
        label.setForeground(getMainTextColor());
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
    }

    //Sub Massage
    private void addSubMessage(JPanel panel) {
        JLabel label = new JLabel(getSubMessage(), SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 22));
        label.setForeground(getSubTextColor());
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
    }

    //Score
    private void addScore(JPanel panel) {
        JLabel label = new JLabel("Final Score: " + finalScore, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setForeground(getScoreTextColor());
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 40)));
    }
    
    //===== shared colors for the two screens =======
    protected Color getSubTextColor() {
        return new Color(255, 210, 210);
    }

    protected Color getScoreTextColor() {
        return new Color(255, 220, 150);
    }

    //buttons
    private void addButtons(JPanel panel) {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnNewGame = createRoundedButton("Start New Game");
        JButton btnMainMenu = createRoundedButton("Return to Main Menu");

        btnNewGame.addActionListener((ActionEvent e) -> {
            dispose();
            if (previousWindow != null) previousWindow.dispose();
            new NewGameScreen().setVisible(true);
        });

        btnMainMenu.addActionListener((ActionEvent e) -> {
            dispose();
            if (previousWindow != null) previousWindow.dispose();
            new FirstScreen().setVisible(true);
        });

        buttonsPanel.add(btnNewGame);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonsPanel.add(btnMainMenu);

        panel.add(buttonsPanel);
    }

    // ===== Shared Button Style =====
    protected JButton createRoundedButton(String text) {
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
