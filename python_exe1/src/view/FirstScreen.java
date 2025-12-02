package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FirstScreen extends JFrame {

    private JLabel musicLabel;
    private MusicManager musicManager;

    public FirstScreen() {
        setTitle("Mine Sweeper");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true); // allow resizing

        // Initialize music manager and start background music
        musicManager = MusicManager.getInstance();
        musicManager = MusicManager.getInstance();
        if (musicManager.getCurrentMusicFile() == null) {
            musicManager.playMusic("src/resource/puzzle-game-bright-casual-video-game-music-249202.wav");
        }


        // Main panel
        BackgroundPanel mainPanel = new BackgroundPanel("src/resource/background.jpg");
        mainPanel.setLayout(null);
        setContentPane(mainPanel);

        // Components
        JLabel title = new JLabel("Mine Sweeper", SwingConstants.CENTER);
        title.setFont(new Font("Verdana", Font.BOLD, 48));
        title.setForeground(new Color(246, 230, 138));
        mainPanel.add(title);

       // Create menu for settings
        JPopupMenu settingsMenu = new JPopupMenu();

        // Option 1: Game Rules
        JMenuItem rulesItem = new JMenuItem("Game Rules");
        rulesItem.addActionListener(e -> new GameRulesScreen());
        settingsMenu.add(rulesItem);

        // Option 2: Sound settings 
        JMenuItem soundItem = new JMenuItem("Sound Settings");
        soundItem.addActionListener(e -> showVolumeControl());
        settingsMenu.add(soundItem);
        
        
        JLabel settings = new JLabel("⚙");
        settings.setFont(new Font("Dialog", Font.BOLD, 40));
        settings.setForeground(new Color(246, 230, 138));
        settings.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settings.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                settingsMenu.show(settings, e.getX(), e.getY());
            }
        });
        mainPanel.add(settings);

        musicLabel = new JLabel(musicManager.isPlaying() ? "♪" : "🔇");
        musicLabel.setFont(new Font("Dialog", Font.BOLD, 40));
        musicLabel.setForeground(musicManager.isPlaying() ? new Color(246, 230, 138) : new Color(180, 180, 180));
        musicLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        musicLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                toggleMusic();
            }
        });
        mainPanel.add(musicLabel);

        JLabel exit = new JLabel("Exit");
        exit.setFont(new Font("Verdana", Font.BOLD, 24));
        exit.setForeground(new Color(246, 230, 138));
        exit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exit.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });
        mainPanel.add(exit);

        // Buttons
        RoundedButton startBtn = new RoundedButton("Start New Game");
        mainPanel.add(startBtn);
        startBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new NewGameScreen();
                FirstScreen.this.dispose();
            }
        });

        RoundedButton historyBtn = new RoundedButton("Game History");
        mainPanel.add(historyBtn);
        historyBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new GameHistoryScreen();
                FirstScreen.this.dispose();
            }
        });

        RoundedButton managerBtn = new RoundedButton("Question Manager");
        mainPanel.add(managerBtn);
        final String CORRECT_PASSWORD = "1234";
        managerBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JPasswordField passwordField = new JPasswordField(10);

                JLabel eyeLabel = new JLabel("👀"); 
                eyeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                eyeLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20)); 

                JPanel passPanel = new JPanel(new BorderLayout(5, 5));
                passPanel.add(passwordField, BorderLayout.CENTER);
                passPanel.add(eyeLabel, BorderLayout.EAST);

                JPanel panel = new JPanel(new BorderLayout(5, 5));
                panel.add(new JLabel("Please enter Admin Password:"), BorderLayout.NORTH);
                panel.add(passPanel, BorderLayout.CENTER);

                // Toggle password visibility
                eyeLabel.addMouseListener(new MouseAdapter() {
                    private boolean visible = false;

                    public void mouseClicked(MouseEvent e) {
                        visible = !visible;
                        if (visible) {
                            passwordField.setEchoChar((char)0); 
                            eyeLabel.setText("🙈"); 
                        } else {
                            passwordField.setEchoChar('•'); 
                            eyeLabel.setText("👀"); 
                        }
                    }
                });

                if (result == JOptionPane.OK_OPTION) {
                    char[] passwordChars = passwordField.getPassword();
                    String inputPassword = new String(passwordChars);
                    if (inputPassword.equals(CORRECT_PASSWORD)) {
                        new QuestionManagerScreen();
                        FirstScreen.this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(
                                FirstScreen.this, "The password entered is incorrect.",
                                "Authentication Error", JOptionPane.ERROR_MESSAGE
                        );
                    }
                    java.util.Arrays.fill(passwordChars, '0');
                }
            }
        });

        // Initial positioning
        positionComponents(title, settings, musicLabel, exit, startBtn, historyBtn, managerBtn);

        // Reposition components dynamically when window resizes
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                positionComponents(title, settings, musicLabel, exit, startBtn, historyBtn, managerBtn);
            }
        });

        setVisible(true);
    }

    // Method to position components relative to window size
    private void positionComponents(JLabel title, JLabel settings, JLabel musicLabel, JLabel exit,
                                    RoundedButton startBtn, RoundedButton historyBtn, RoundedButton managerBtn) {
        int w = getWidth();
        int h = getHeight();

        // Top icons
        settings.setBounds(30, 30, 60, 60);
        musicLabel.setBounds(110, 30, 60, 60);
        exit.setBounds(w - 120, 40, 80, 40);

        // Title centered
        title.setBounds(w / 2 - 250, 40, 500, 70);

        // Buttons centered vertically
        startBtn.setBounds(w / 2 - 230, h / 2 - 120, 460, 80);
        historyBtn.setBounds(w / 2 - 230, h / 2 - 10, 460, 80);
        managerBtn.setBounds(w / 2 - 230, h / 2 + 100, 460, 80);
    }

    // Toggle music
    private void toggleMusic() {
        musicManager.toggleMusic();
        updateMusicIcon();
    }

    // Show volume control dialog
    private void showVolumeControl() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel volumeLabel = new JLabel("Volume: " + musicManager.getVolumePercent() + "%", SwingConstants.CENTER);
        volumeLabel.setFont(new Font("Verdana", Font.BOLD, 14));

        JSlider volumeSlider = new JSlider(0, 100, musicManager.getVolumePercent());
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);

        volumeSlider.addChangeListener(e -> {
            int value = volumeSlider.getValue();
            musicManager.setVolume(value / 100.0f);
            volumeLabel.setText("Volume: " + value + "%");
        });

        panel.add(volumeLabel, BorderLayout.NORTH);
        panel.add(volumeSlider, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(
                this,
                panel,
                "Volume Control",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private void updateMusicIcon() {
        if (musicManager.isPlaying()) {
            musicLabel.setText("♪");
            musicLabel.setForeground(new Color(246, 230, 138));
        } else {
            musicLabel.setText("🔇");
            musicLabel.setForeground(new Color(180, 180, 180));
        }
    }

    // Rounded Button
    static class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setFont(new Font("Verdana", Font.BOLD, 26));
            setForeground(new Color(246, 230, 138));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color bgColor = new Color(80, 0, 120, 170);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 45, 45);
            g2.setColor(new Color(255, 255, 255, 40));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 45, 45);
            g2.dispose();
            super.paintComponent(g);
        }

        public boolean isContentAreaFilled() {
            return false;
        }
    }

    //main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FirstScreen());
    }
}


