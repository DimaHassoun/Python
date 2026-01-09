package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// ========== IMPLEMENT THE LISTENER INTERFACE ==========
public class FirstScreen extends JFrame implements MusicManager.MusicStateListener {
// ======================================================

    private JLabel musicLabel;
    private MusicManager musicManager;
    private WindowSizeManager windowSizeManager;

    public FirstScreen() {
        setTitle("Mine Sweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        musicManager = MusicManager.getInstance();
        windowSizeManager = WindowSizeManager.getInstance();
        
        // ========== REGISTER AS OBSERVER ==========
        musicManager.addMusicStateListener(this);
        // ==========================================
        
        windowSizeManager.applyToFrame(this);
        setLocationRelativeTo(null);

        if (musicManager.getCurrentMusicFile() == null) {
            musicManager.playMusic("src/resource/puzzle-game-bright-casual-video-game-music-249202.wav");
        }

        BackgroundPanel mainPanel = new BackgroundPanel("src/resource/BackgroundFirstScreen.png");
        mainPanel.setLayout(null);
        setContentPane(mainPanel);

        JLabel title = new JLabel("Mine Sweeper", SwingConstants.CENTER);
        title.setFont(new Font("Verdana", Font.BOLD, 48));
        title.setForeground(new Color(246, 230, 138));
        mainPanel.add(title);
        // Create menu for settings
        JPopupMenu settingsMenu = new JPopupMenu();
        settingsMenu.setBackground(new Color(60, 0, 90));
        settingsMenu.setBorder(BorderFactory.createLineBorder(
                new Color(246, 230, 138), 2
        ));
        settingsMenu.setOpaque(true);

        Font menuFont = new Font("Verdana", Font.BOLD, 14);
        Color bg = new Color(60, 0, 90);
        Color bgHover = new Color(65, 0, 95); 
        Color textColor = new Color(246, 230, 138);

        // -------- Game Rules --------
        JMenuItem rulesItem = new JMenuItem("Game Rules");
        rulesItem.setFont(menuFont);
        rulesItem.setForeground(textColor);
        rulesItem.setBackground(bg);
        rulesItem.setOpaque(true);
        rulesItem.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        rulesItem.setFocusPainted(false);
        rulesItem.addActionListener(e -> new GameRulesScreen());

        // -------- Separator --------
        JSeparator separator = new JSeparator();
        separator.setForeground(textColor);

        // -------- Sound Settings --------
        JMenuItem soundItem = new JMenuItem("Sound Settings");
        soundItem.setFont(menuFont);
        soundItem.setForeground(textColor);
        soundItem.setBackground(bg);
        soundItem.setOpaque(true);
        soundItem.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        soundItem.setFocusPainted(false);
        soundItem.addActionListener(e -> showVolumeControl());

        // -------- Fix Swing hover colors --------
        UIManager.put("MenuItem.selectionBackground", bgHover);
        UIManager.put("MenuItem.selectionForeground", textColor);

        // -------- Add --------
        settingsMenu.add(rulesItem);
        settingsMenu.add(separator);
        settingsMenu.add(soundItem);

        
        JLabel settings = new JLabel("⚙");
        settings.setFont(new Font("Dialog", Font.BOLD, 40));
        settings.setForeground(new Color(246, 230, 138));
        settings.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settings.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
            	settingsMenu.show(settings, 0, settings.getHeight());
            }
        });
        mainPanel.add(settings);

        musicLabel = new JLabel(musicManager.isPlaying() ? "♪" : "🔇");
        musicLabel.setFont(new Font("Dialog", Font.BOLD, 40));
        musicLabel.setForeground(musicManager.isPlaying() ? new Color(246, 230, 138) : new Color(180, 180, 180));
        musicLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        musicLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                musicManager.toggleMusic();
                // ← NO NEED TO CALL updateMusicIcon() - Observer will handle it!
            }
        });
        mainPanel.add(musicLabel);

        RoundedButton exit = new RoundedButton("Exit");
        exit.setFont(new Font("Verdana", Font.BOLD, 20));
        exit.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });
        mainPanel.add(exit);

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

                int result = JOptionPane.showConfirmDialog(
                        FirstScreen.this, panel, "Admin Authentication Required",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE
                );

                if (result == JOptionPane.OK_OPTION) {
                    char[] passwordChars = passwordField.getPassword();
                    String inputPassword = new String(passwordChars);
                    if (inputPassword.equals("1234")) {
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

        positionComponents(title, settings, musicLabel, exit, startBtn, historyBtn, managerBtn);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                positionComponents(title, settings, musicLabel, exit, startBtn, historyBtn, managerBtn);
            }
        });

        setVisible(true);
    }

    // ========== OBSERVER CALLBACK METHOD ==========
    /**
     * This method is automatically called when MusicManager state changes!
     * No need to manually call updateMusicIcon() anymore.
     */
    @Override
    public void onMusicStateChanged() {
        updateMusicIcon();
        System.out.println("📢 FirstScreen: Music state updated");
    }
    // ==============================================

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
            // ← NO NEED TO CALL updateMusicIcon() - Observer will handle it!
        });

        panel.add(volumeLabel, BorderLayout.NORTH);
        panel.add(volumeSlider, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "Volume Control", JOptionPane.PLAIN_MESSAGE);
    }
    // Updates the music icon based on the current playback state.
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

    
    // ========== CLEANUP WHEN CLOSING ==========
    @Override
    public void dispose() {
        musicManager.removeMusicStateListener(this);
        System.out.println("✓ FirstScreen: Unregistered from music updates");
        super.dispose();
    }
    // ==========================================

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

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Color bgColor = isHovered ? 
                new Color(100, 20, 140, 200) : 
                new Color(80, 0, 120, 170);
            
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 45, 45);
            
            Color borderColor = isHovered ? 
                new Color(255, 255, 255, 80) : 
                new Color(255, 255, 255, 40);
            
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 45, 45);
            g2.dispose();
            super.paintComponent(g);
        }

        public boolean isContentAreaFilled() {
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FirstScreen());
    }
}
