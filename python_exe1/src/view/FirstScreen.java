package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * FirstScreen is the main menu screen of the Mine Sweeper game.
 * It handles UI components, music control, window resizing, and navigation
 * to other screens such as New Game, Game History, and Question Manager.
 * It also implements the MusicStateListener interface to respond to music state changes.
 */
public class FirstScreen extends JFrame implements MusicManager.MusicStateListener {

    private JLabel musicLabel; // Label showing music status (playing/muted)
    private MusicManager musicManager; // Singleton manager for background music
    private WindowSizeManager windowSizeManager; // Singleton manager for window resizing

    /**
     * Constructor initializes the main menu screen and all its components.
     */
    public FirstScreen() {
        setTitle("Mine Sweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        // Initialize singleton managers
        musicManager = MusicManager.getInstance();
        windowSizeManager = WindowSizeManager.getInstance();

        // Register this screen as a listener to music state changes
        musicManager.addMusicStateListener(this);

        // Apply window size preferences
        windowSizeManager.applyToFrame(this);
        setLocationRelativeTo(null);

        // Play default background music if none is currently playing
        if (musicManager.getCurrentMusicFile() == null) {
            musicManager.playMusic("/resource/puzzle-game-bright-casual-video-game-music-249202.wav");
        }

        // Setup main panel with background image
        BackgroundPanel mainPanel = new BackgroundPanel("/resource/BackgroundFirstScreen.png");
        mainPanel.setLayout(null);
        setContentPane(mainPanel);

        // ===== TITLE LABEL =====
        JLabel title = new JLabel("Mine Sweeper", SwingConstants.CENTER);
        title.setFont(new Font("Verdana", Font.BOLD, 48));
        title.setForeground(new Color(246, 230, 138));
        mainPanel.add(title);

        // ===== SETTINGS MENU =====
        JPopupMenu settingsMenu = new JPopupMenu();
        settingsMenu.setBackground(new Color(60, 0, 90));
        settingsMenu.setBorder(BorderFactory.createLineBorder(new Color(246, 230, 138), 2));
        settingsMenu.setOpaque(true);

        Font menuFont = new Font("Verdana", Font.BOLD, 14);
        Color bg = new Color(60, 0, 90);
        Color bgHover = new Color(65, 0, 95);
        Color textColor = new Color(246, 230, 138);

        // -------- Game Rules Menu Item --------
        JMenuItem rulesItem = new JMenuItem("Game Rules");
        rulesItem.setFont(menuFont);
        rulesItem.setForeground(textColor);
        rulesItem.setBackground(bg);
        rulesItem.setOpaque(true);
        rulesItem.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        rulesItem.setFocusPainted(false);
        rulesItem.addActionListener(e -> new GameRulesScreen()); // Opens game rules screen

        // -------- Separator --------
        JSeparator separator = new JSeparator();
        separator.setForeground(textColor);

        // -------- Sound Settings Menu Item --------
        JMenuItem soundItem = new JMenuItem("Sound Settings");
        soundItem.setFont(menuFont);
        soundItem.setForeground(textColor);
        soundItem.setBackground(bg);
        soundItem.setOpaque(true);
        soundItem.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        soundItem.setFocusPainted(false);
        soundItem.addActionListener(e -> 
        MessagePlanet.showVolumeControlDialog(
                this,                     // parent component for dialog centering
                musicManager,             //  music manager instance
                new Color(255, 180, 255)  //  border color
            )
        ); // Opens volume slider

        // Fix Swing hover colors
        UIManager.put("MenuItem.selectionBackground", bgHover);
        UIManager.put("MenuItem.selectionForeground", textColor);

        // Add items to settings menu
        settingsMenu.add(rulesItem);
        settingsMenu.add(separator);
        settingsMenu.add(soundItem);

        // ===== SETTINGS ICON =====
        JLabel settings = new JLabel("⚙");
        settings.setFont(new Font("Dialog", Font.BOLD, 40));
        settings.setForeground(new Color(246, 230, 138));
        settings.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settings.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                settingsMenu.show(settings, 0, settings.getHeight()); // Show popup menu
            }
        });
        mainPanel.add(settings);

        // ===== MUSIC TOGGLE ICON =====
        musicLabel = new JLabel(musicManager.isPlaying() ? "♪" : "🔇");
        musicLabel.setFont(new Font("Dialog", Font.BOLD, 40));
        musicLabel.setForeground(musicManager.isPlaying() ? new Color(246, 230, 138) : new Color(180, 180, 180));
        musicLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        musicLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                musicManager.toggleMusic(); // Toggle play/pause
                // Observer will update the icon automatically
            }
        });
        mainPanel.add(musicLabel);

        // ===== EXIT BUTTON =====
        RoundedButton exit = new RoundedButton("Exit");
        exit.setFont(new Font("Verdana", Font.BOLD, 20));
        exit.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                System.exit(0); // Exit application
            }
        });
        mainPanel.add(exit);

        // ===== START NEW GAME BUTTON =====
        RoundedButton startBtn = new RoundedButton("Start New Game");
        mainPanel.add(startBtn);
        startBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new NewGameScreen(); // Open new game screen
                FirstScreen.this.dispose(); // Close main menu
            }
        });

        // ===== GAME HISTORY BUTTON =====
        RoundedButton historyBtn = new RoundedButton("Game History");
        mainPanel.add(historyBtn);
        historyBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new GameHistoryScreen(); // Open history screen
                FirstScreen.this.dispose();
            }
        });

        // ===== QUESTION MANAGER BUTTON (ADMIN ONLY) =====
        RoundedButton managerBtn = new RoundedButton("Question Manager");
        mainPanel.add(managerBtn);
        managerBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                boolean authenticated = false;

                while (!authenticated) {
                    String inputPassword = MessagePlanet.showPasswordDialog(
                            FirstScreen.this,
                            "Admin Authentication Required",
                            new Color(255, 180, 255) // or any border color you want
                    );

                    if (inputPassword == null) {
                        // Cancel pressed → exit loop
                        break;
                    }

                    if (inputPassword.isEmpty()) {
                        MessagePlanet.showBlockingMessageDialog(
                                FirstScreen.this,
                                "Password cannot be empty, Please try again.",
                                Color.YELLOW,
                                "Input Error",
                                "OK"
                        );
                    } else if (!inputPassword.equals("1234")) {
                        MessagePlanet.showBlockingMessageDialog(
                                FirstScreen.this,
                                "The password entered is incorrect, Please try again.",
                                Color.RED,
                                "Authentication Error",
                                "OK"
                        );
                    } else {
                        authenticated = true;
                        new QuestionManagerScreen(); // open admin screen
                        FirstScreen.this.dispose();
                    }
                }
            }
        });


        // Position all components initially
        positionComponents(title, settings, musicLabel, exit, startBtn, historyBtn, managerBtn);

        // Reposition components on window resize
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                positionComponents(title, settings, musicLabel, exit, startBtn, historyBtn, managerBtn);
            }
        });

        setVisible(true);
    }

    // ================= OBSERVER CALLBACK =================
    /**
     * Callback method from MusicManager when music state changes.
     * Updates the music icon accordingly.
     */
    @Override
    public void onMusicStateChanged() {
        updateMusicIcon();
        System.out.println("📢 FirstScreen: Music state updated");
    }

    // ================= POSITION COMPONENTS =================
    /**
     * Dynamically positions all components based on current window size.
     *
     * @param title      The title JLabel
     * @param settings   The settings JLabel
     * @param musicLabel The music toggle JLabel
     * @param exit       Exit button
     * @param startBtn   Start New Game button
     * @param historyBtn Game History button
     * @param managerBtn Question Manager button
     */
    private void positionComponents(JLabel title, JLabel settings, JLabel musicLabel, RoundedButton exit,
                                    RoundedButton startBtn, RoundedButton historyBtn, RoundedButton managerBtn) {
        int w = getWidth();
        int h = getHeight();

        settings.setBounds(30, 30, 60, 60);
        musicLabel.setBounds(110, 30, 60, 60);
        exit.setBounds(w - 140, 30, 100, 50);
        title.setBounds(w / 2 - 250, 40, 500, 70);
        startBtn.setBounds(w / 2 - 230, h / 2 - 120, 460, 80);
        historyBtn.setBounds(w / 2 - 230, h / 2 - 10, 460, 80);
        managerBtn.setBounds(w / 2 - 230, h / 2 + 100, 460, 80);
    }

    // ================= VOLUME CONTROL =================
    /**
     * Shows a volume control dialog with a slider.
     * Updates the MusicManager volume and label dynamically.
     */
  /* private void showVolumeControl() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel volumeLabel = new JLabel("Volume: " + musicManager.getVolumePercent() + "%", SwingConstants.CENTER);
        volumeLabel.setFont(new Font("Verdana", Font.BOLD, 14));

        JSlider volumeSlider = new JSlider(0, 100, musicManager.getVolumePercent());
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);

        // Update music volume on slider change
        volumeSlider.addChangeListener(e -> {
            int value = volumeSlider.getValue();
            musicManager.setVolume(value / 100.0f);
            volumeLabel.setText("Volume: " + value + "%");
            // Observer will automatically update the music icon
        });

        panel.add(volumeLabel, BorderLayout.NORTH);
        panel.add(volumeSlider, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "Volume Control", JOptionPane.PLAIN_MESSAGE);
    }*/

    // ================= UPDATE MUSIC ICON =================
    /**
     * Updates the music toggle icon based on current playback state.
     */
    private void updateMusicIcon() {
        if (musicLabel == null) return;

        if (musicManager.isPlaying()) {
            musicLabel.setText("♪");
            musicLabel.setForeground(new Color(246, 230, 138));
        } else {
            musicLabel.setText("🔇");
            musicLabel.setForeground(new Color(180, 180, 180));
        }
    }

    // ================= CLEANUP ON CLOSE =================
    /**
     * Removes this screen from MusicManager listeners before disposing.
     */
    @Override
    public void dispose() {
        musicManager.removeMusicStateListener(this);
        System.out.println("✓ FirstScreen: Unregistered from music updates");
        super.dispose();
    }

    // ================= INNER CLASS: ROUNDED BUTTON =================
    /**
     * Custom JButton with rounded corners and hover effect.
     */
    static class RoundedButton extends JButton {
        private boolean isHovered = false;

        public RoundedButton(String text) {
            super(text);
            setFont(new Font("Verdana", Font.BOLD, 26));
            setForeground(new Color(246, 230, 138));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Hover effect
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background color changes on hover
            Color bgColor = isHovered ?
                    new Color(100, 20, 140, 200) :
                    new Color(80, 0, 120, 170);

            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 45, 45);

            // Border color changes on hover
            Color borderColor = isHovered ?
                    new Color(255, 255, 255, 80) :
                    new Color(255, 255, 255, 40);

            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 45, 45);
            g2.dispose();

            super.paintComponent(g);
        }

        @Override
        public boolean isContentAreaFilled() {
            return false;
        }
    }

    /**
     * Main entry point of the application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FirstScreen());
    }
}
