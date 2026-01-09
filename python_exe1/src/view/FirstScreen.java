package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * FirstScreen represents the main menu of the Mine Sweeper game.
 * It handles navigation (new game, history, question manager), music control,
 * settings, and admin authentication.
 */
public class FirstScreen extends JFrame implements MusicManager.MusicStateListener {

    private JLabel musicLabel; // Displays music icon (♪ or 🔇)
    private MusicManager musicManager; // Singleton manager for background music
    private WindowSizeManager windowSizeManager; // Singleton for handling window size

    public FirstScreen() {
        setTitle("Mine Sweeper"); // Set window title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit app on close
        setResizable(true); // Allow window resizing

        musicManager = MusicManager.getInstance(); // Get music manager singleton
        windowSizeManager = WindowSizeManager.getInstance(); // Get window size manager
        musicManager.addMusicStateListener(this); // Listen for music state changes

        windowSizeManager.applyToFrame(this); // Apply saved window size and position
        setLocationRelativeTo(null); // Center window on screen

        // Play music if no music is currently playing
        if (musicManager.getCurrentMusicFile() == null) {
            musicManager.playMusic("/resource/puzzle-game-bright-casual-video-game-music-249202.wav");
        }

        // ===== BACKGROUND PANEL =====
        BackgroundPanel mainPanel = new BackgroundPanel("/resource/BackgroundFirstScreen.png");
        mainPanel.setLayout(null); // Use absolute positioning
        setContentPane(mainPanel);

        // ===== GAME TITLE =====
        JLabel title = new JLabel("Mine Sweeper", SwingConstants.CENTER);
        title.setFont(new Font("Verdana", Font.BOLD, 48));
        title.setForeground(new Color(246, 230, 138));
        mainPanel.add(title);

        // ===== SETTINGS MENU =====
        JPopupMenu settingsMenu = new JPopupMenu(); // Popup menu for settings
        settingsMenu.setBackground(new Color(60, 0, 90));

        JMenuItem rulesItem = new JMenuItem("Game Rules"); // Show game rules
        rulesItem.addActionListener(e -> new GameRulesScreen());

        JMenuItem soundItem = new JMenuItem("Sound Settings"); // Volume control
        soundItem.addActionListener(e -> showVolumeControl());

        settingsMenu.add(rulesItem);
        settingsMenu.add(soundItem);

        // Gear icon to open settings menu
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

        // ===== MUSIC ICON =====
        musicLabel = new JLabel(); // Label to show music state
        musicLabel.setFont(new Font("Dialog", Font.BOLD, 40));
        musicLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        musicLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                musicManager.toggleMusic(); // Toggle music play/pause
            }
        });
        mainPanel.add(musicLabel);
        updateMusicIcon(); // Set initial music icon

        // ===== MAIN BUTTONS =====
        RoundedButton startBtn = new RoundedButton("Start New Game");
        startBtn.addActionListener(e -> {
            new NewGameScreen(); // Open new game window
            dispose(); // Close main menu
        });

        RoundedButton historyBtn = new RoundedButton("Game History");
        historyBtn.addActionListener(e -> {
            new GameHistoryScreen(); // Show past game history
            dispose();
        });

        RoundedButton managerBtn = new RoundedButton("Question Manager");
        managerBtn.addActionListener(e -> {
            if (showAdminDialog()) { // Show admin password dialog
                new QuestionManagerScreen(); // Only open if authenticated
                dispose();
            }
        });

        RoundedButton exit = new RoundedButton("Exit");
        exit.addActionListener(e -> System.exit(0)); // Exit program

        mainPanel.add(startBtn);
        mainPanel.add(historyBtn);
        mainPanel.add(managerBtn);
        mainPanel.add(exit);

        // Position all components based on window size
        positionComponents(title, settings, musicLabel, exit, startBtn, historyBtn, managerBtn);

        // Update component positions dynamically when window is resized
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                positionComponents(title, settings, musicLabel, exit, startBtn, historyBtn, managerBtn);
            }
        });

        setVisible(true); // Show main menu
    }

    // ================= ADMIN PASSWORD DIALOG =================
    private boolean showAdminDialog() {
        AdminDialog dialog = new AdminDialog(this); // Open modal dialog
        dialog.setVisible(true);
        return dialog.isAuthenticated(); // Return true if password correct
    }

    // Nested class for password authentication
    private static class AdminDialog extends JDialog {
        private JLabel errorLabel;
        private boolean authenticated = false;
        private final JPasswordField passwordField = new JPasswordField(12);

        AdminDialog(JFrame parent) {
            super(parent, "Admin Login", true);
            setUndecorated(true); // Remove default window frame
            setSize(360, 180);
            setLocationRelativeTo(parent);

            Color bg = new Color(60, 0, 90);
            Color text = new Color(246, 230, 138);

            JPanel root = new JPanel(new BorderLayout());
            root.setBackground(bg);
            root.setBorder(BorderFactory.createLineBorder(text, 3));
            setContentPane(root);

            JLabel title = new JLabel("Admin Authentication", SwingConstants.CENTER);
            title.setFont(new Font("Verdana", Font.BOLD, 18));
            title.setForeground(text);
            title.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
            root.add(title, BorderLayout.NORTH);

            // Label to show errors
            errorLabel = new JLabel(" ", SwingConstants.CENTER);
            errorLabel.setFont(new Font("Verdana", Font.BOLD, 13));
            errorLabel.setForeground(new Color(255, 120, 120));
            errorLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            passwordField.setFont(new Font("Verdana", Font.BOLD, 16));
            passwordField.setBackground(new Color(90, 20, 120));
            passwordField.setForeground(text);
            passwordField.setCaretColor(text);
            passwordField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

            // Eye icon to toggle password visibility
            JLabel eye = new JLabel("👀");
            eye.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
            eye.setCursor(new Cursor(Cursor.HAND_CURSOR));
            eye.setForeground(text);

            JPanel center = new JPanel(new BorderLayout());
            center.setBackground(bg);
            center.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            center.add(passwordField, BorderLayout.CENTER);
            center.add(eye, BorderLayout.EAST);
            root.add(center, BorderLayout.CENTER);

            // Toggle password visibility
            eye.addMouseListener(new MouseAdapter() {
                boolean visible = false;
                public void mouseClicked(MouseEvent e) {
                    visible = !visible;
                    passwordField.setEchoChar(visible ? (char) 0 : '•');
                    eye.setText(visible ? "🙈" : "👀");
                }
            });

            JPanel buttons = new JPanel();
            buttons.setBackground(bg);

            JButton ok = createButton("OK", text);
            JButton cancel = createButton("Cancel", text);

            ok.addActionListener(e -> authenticate()); // Check password
            cancel.addActionListener(e -> dispose()); // Close dialog

            buttons.add(ok);
            buttons.add(cancel);

            JPanel south = new JPanel(new BorderLayout());
            south.setBackground(bg);
            south.add(errorLabel, BorderLayout.NORTH);
            south.add(buttons, BorderLayout.SOUTH);

            root.add(south, BorderLayout.SOUTH);

            getRootPane().setDefaultButton(ok); // Press Enter = OK
        }

        private JButton createButton(String text, Color color) {
            JButton btn = new JButton(text);
            btn.setFocusPainted(false);
            btn.setBackground(new Color(90, 20, 120));
            btn.setForeground(color);
            btn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
            return btn;
        }

        // Validate admin password
        private void authenticate() {
            String input = new String(passwordField.getPassword());

            if (input.isEmpty()) {
                showError("Password cannot be empty");
                return;
            }

            if (!input.equals("1234")) { // Hardcoded password
                showError("Incorrect password");
                return;
            }

            authenticated = true;
            dispose(); // Close dialog
        }

        private void showError(String message) {
            errorLabel.setText(message); // Display error
            shake(); // Shake dialog for visual feedback
        }

        private void shake() {
            Point p = getLocation();
            for (int i = 0; i < 10; i++) {
                setLocation(p.x + (i % 2 == 0 ? 10 : -10), p.y);
                try { Thread.sleep(20); } catch (InterruptedException ignored) {}
            }
            setLocation(p); // Restore original position
        }

        boolean isAuthenticated() {
            return authenticated;
        }
    }

    // ================= STYLED MESSAGE DIALOG =================
    private void showStyledMessage(String msg, int type) {
        Color bg = new Color(60, 0, 90);
        Color text = new Color(246, 230, 138);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bg);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel label = new JLabel(msg, SwingConstants.CENTER);
        label.setFont(new Font("Verdana", Font.BOLD, 14));
        label.setForeground(text);

        panel.add(label, BorderLayout.CENTER);

        // Show message using JOptionPane
        JOptionPane.showMessageDialog(
                this,
                panel,
                "Message",
                type
        );
    }

    // ================= VOLUME CONTROL =================
    private void showVolumeControl() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel volumeLabel = new JLabel(
                "Volume: " + musicManager.getVolumePercent() + "%",
                SwingConstants.CENTER
        );

        JSlider slider = new JSlider(0, 100, musicManager.getVolumePercent());
        slider.addChangeListener(e -> {
            int value = slider.getValue();
            musicManager.setVolume(value / 100f); // Set music volume
            volumeLabel.setText("Volume: " + value + "%");
        });

        panel.add(volumeLabel, BorderLayout.NORTH);
        panel.add(slider, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "Volume Control",
                JOptionPane.PLAIN_MESSAGE);
    }

    // ================= MUSIC ICON HANDLING =================
    @Override
    public void onMusicStateChanged() {
        updateMusicIcon(); // Update icon when music starts/stops
    }

    private void updateMusicIcon() {
        if (musicManager.isPlaying()) {
            musicLabel.setText("♪"); // Music playing
            musicLabel.setForeground(new Color(246, 230, 138));
        } else {
            musicLabel.setText("🔇"); // Music muted
            musicLabel.setForeground(Color.GRAY);
        }
    }

    @Override
    public void dispose() {
        musicManager.removeMusicStateListener(this); // Stop listening when window closes
        super.dispose();
    }

    // ================= POSITION COMPONENTS =================
    private void positionComponents(JLabel title, JLabel settings, JLabel music,
                                    JButton exit, JButton start, JButton history, JButton manager) {
        int w = getWidth();
        int h = getHeight();

        settings.setBounds(30, 30, 60, 60); // Gear icon
        music.setBounds(110, 30, 60, 60); // Music icon
        exit.setBounds(w - 140, 30, 100, 50); // Exit button

        title.setBounds(w / 2 - 250, 40, 500, 70); // Center title
        start.setBounds(w / 2 - 230, h / 2 - 120, 460, 80); // Start button
        history.setBounds(w / 2 - 230, h / 2 - 10, 460, 80); // History button
        manager.setBounds(w / 2 - 230, h / 2 + 100, 460, 80); // Question manager
    }

    // ================= ROUNDED BUTTON CLASS =================
    static class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setFont(new Font("Verdana", Font.BOLD, 26));
            setForeground(new Color(246, 230, 138));
            setContentAreaFilled(false);
            setBorderPainted(false);
        }

        // Custom paint for rounded button with semi-transparent purple
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(80, 0, 120, 180));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 45, 45);
            super.paintComponent(g);
        }
    }

    // ================= MAIN METHOD =================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(FirstScreen::new); // Start GUI on Event Dispatch Thread
    }
}
