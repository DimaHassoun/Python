package view;

import javax.swing.*;

import controller.GameController;
import view.FirstScreen.RoundedButton;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NewGameScreen extends JFrame implements MusicManager.MusicStateListener {

    private JLabel musicLabel;
    private MusicManager musicManager;
    private WindowSizeManager windowSizeManager;
    private JTextField p1;
    private JTextField p2;

    public NewGameScreen() {
        setTitle("Start a New Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true); // allow resizing

        // Music manager
        musicManager = MusicManager.getInstance();
     // Initialize window size manager
        windowSizeManager = WindowSizeManager.getInstance();
        
        musicManager.addMusicStateListener(this);
        
     // Apply saved window size                            
        windowSizeManager.applyToFrame(this);                 
        setLocationRelativeTo(null);

        BackgroundPanel panel = new BackgroundPanel("/resource/background.jpg");
        panel.setLayout(null);
        setContentPane(panel);
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
     
        // SETTINGS ICON
        JLabel settings = new JLabel("⚙");
        settings.setFont(new Font("SansSerif", Font.BOLD, 40));
        settings.setForeground(new Color(246, 230, 138));
        settings.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settings.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
            	settingsMenu.show(settings, 0, settings.getHeight());
            }
        });
        panel.add(settings);

        // MUSIC ICON
        musicLabel = new JLabel(musicManager.isPlaying() ? "♪" : "🔇");
        musicLabel.setFont(new Font("Dialog", Font.BOLD, 40));
        musicLabel.setForeground(musicManager.isPlaying() ? new Color(246, 230, 138) : new Color(180, 180, 180));
        musicLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        musicLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                toggleMusic();
            }
        });
        panel.add(musicLabel);

        // TITLE
        JLabel title = new JLabel("Start a new game", SwingConstants.CENTER);
        title.setFont(new Font("Verdana", Font.BOLD, 48));
        title.setForeground(new Color(246, 230, 138));
        panel.add(title);

        // BACK BUTTON
        RoundedButton back = new RoundedButton("Back");
        back.setFont(new Font("Verdana", Font.BOLD, 20));
        back.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
                new FirstScreen();
                NewGameScreen.this.dispose();
            }
        });
        panel.add(back);
        

        // GAME NUMBER
        int nextGameNum = GameController.getNextGameIdFromHistory();
        JLabel gameNum = new JLabel("Game No. " + nextGameNum);
        gameNum.setFont(new Font("Verdana", Font.BOLD, 26));
        gameNum.setForeground(new Color(246, 230, 138));
        panel.add(gameNum);

        // DATE
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        JLabel dateLbl = new JLabel("Date: " + date);
        dateLbl.setFont(new Font("Verdana", Font.BOLD, 26));
        dateLbl.setForeground(new Color(246, 230, 138));
        panel.add(dateLbl);

        // DIFFICULTY PANEL
        RoundedPanel diffPanel = new RoundedPanel(40);
        diffPanel.setLayout(null);
        diffPanel.setBorder(BorderFactory.createLineBorder(new Color(246, 230, 138), 2));
        panel.add(diffPanel);

        JLabel diffTitle = new JLabel("Select difficulty level:");
        diffTitle.setFont(new Font("Verdana", Font.BOLD, 24));
        diffTitle.setForeground(new Color(246, 230, 138));
        diffPanel.add(diffTitle);

        JRadioButton easy = createOption("Easy", 80);
        JRadioButton medium = createOption("Medium", 130);
        JRadioButton hard = createOption("Hard", 180);

        ButtonGroup group = new ButtonGroup();
        group.add(easy);
        group.add(medium);
        group.add(hard);

        diffPanel.add(easy);
        diffPanel.add(medium);
        diffPanel.add(hard);

        // PLAYER 1 & PLAYER 2
        p1 = createTextField("First Player Name...");
        p2 = createTextField("Second Player Name...");
        panel.add(p1);
        panel.add(p2);

        // START BUTTON
        RoundedButton startBtn = new RoundedButton("Start Game");
        startBtn.setFont(new Font("Verdana", Font.BOLD, 28));
        startBtn.setFocusPainted(false);
        panel.add(startBtn);
        
        // START BUTTON ACTION
        startBtn.addActionListener(e -> {
            String player1 = p1.getText().trim();
            String player2 = p2.getText().trim();
            
            String difficulty = "";
            if (easy.isSelected()) {
                difficulty = "EASY";
            } else if (medium.isSelected()) {
                difficulty = "MEDIUM";
            } else if (hard.isSelected()) {
                difficulty = "HARD";
            }
            
            try {
            	GameBoards gameBoard = GameController.createNewGame(player1, player2, difficulty);
          
                gameBoard.highlightCurrentPlayer(GameController.GetCurrentPlayer());
                gameBoard.setVisible(true);
                NewGameScreen.this.dispose();
                
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(
                    NewGameScreen.this,
                    ex.getMessage(),
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });  
           
        // INITIAL POSITIONING
        positionComponents(title, settings, musicLabel, back, gameNum, dateLbl, diffPanel, diffTitle, easy, medium, hard, p1, p2, startBtn);

        // DYNAMIC POSITIONING WHEN RESIZED
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                positionComponents(title, settings, musicLabel, back, gameNum, dateLbl, diffPanel, diffTitle, easy, medium, hard, p1, p2, startBtn);
            }
        });

        setVisible(true);
    }
    
    /**
     * Observer callback - automatically called when music state changes
     */
    @Override
    public void onMusicStateChanged() {
        updateMusicIcon();
        System.out.println("📢 NewGameScreen: Music state updated");
    }
    
    /* Positions and sizes all GUI components within the container.
    * This method uses absolute positioning by setting bounds for each component.*/
    private void positionComponents(JLabel title, JLabel settings, JLabel musicLabel, RoundedButton back,
                                    JLabel gameNum, JLabel dateLbl, RoundedPanel diffPanel, JLabel diffTitle,
                                    JRadioButton easy, JRadioButton medium, JRadioButton hard,
                                    JTextField p1, JTextField p2, JButton startBtn) {
        int w = getWidth();
        int h = getHeight();

        settings.setBounds(40, 40, 50, 50);
        musicLabel.setBounds(110, 40, 50, 50);
        back.setBounds(w - 140, 30, 100, 50);

        title.setBounds(w / 2 - 350, 40, 700, 80);

        gameNum.setBounds(60, 150, 300, 40);
        dateLbl.setBounds(60, 190, 300, 40);

        diffPanel.setBounds(w / 2 - 215, 150, 430, 260);
        diffTitle.setBounds(60, 20, 330, 40);
        easy.setBounds(60, 80, 200, 40);
        medium.setBounds(60, 130, 200, 40);
        hard.setBounds(60, 180, 200, 40);

        p1.setBounds(w / 2 - 390, 450, 350, 55);
        p2.setBounds(w / 2 + 40, 450, 350, 55);

        startBtn.setBounds(w / 2 - 150, 540, 300, 60);
    }

    // MUSIC FUNCTIONS
    private void toggleMusic() {
        musicManager.toggleMusic();
       
    }
    // Displays a modal dialog for controlling the application's music volume.
    private void showVolumeControl() {
    	  // Create panel with BorderLayout and spacing
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
     // Label showing current volume percentage
        JLabel volumeLabel = new JLabel("Volume: " + musicManager.getVolumePercent() + "%", SwingConstants.CENTER);
        volumeLabel.setFont(new Font("Verdana", Font.BOLD, 14));
     // Slider for adjusting volume
        JSlider volumeSlider = new JSlider(0, 100, musicManager.getVolumePercent());
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        // Update volume in real-time when slider changes
        volumeSlider.addChangeListener(e -> {
            int value = volumeSlider.getValue();
            musicManager.setVolume(value / 100.0f);
            volumeLabel.setText("Volume: " + value + "%");
        });
     // Add components to panel
        panel.add(volumeLabel, BorderLayout.NORTH);
        panel.add(volumeSlider, BorderLayout.CENTER);
     // Show panel in a modal dialog
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


 // ================= Helper Methods =================
    // Creates a styled JRadioButton with a specified label.
    private JRadioButton createOption(String label, int y) {
        JRadioButton rb = new JRadioButton(label);
        rb.setFont(new Font("Verdana", Font.BOLD, 22));
        rb.setOpaque(false);
        rb.setForeground(Color.WHITE);
        return rb;
    }
    //Creates a customized JTextField with a placeholder text.
    private JTextField createTextField(String placeholder) {
        JTextField tf = new JTextField() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets insets = getInsets();
                    g2.drawString(placeholder, insets.left + 5, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                    g2.dispose();
                }
            }
        };
        tf.setFont(new Font("SansSerif", Font.PLAIN, 22));
        tf.setBackground(new Color(60, 60, 60));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createLineBorder(new Color(246, 230, 138), 2));
        return tf;
    }

    // ROUNDED PANEL
    class RoundedPanel extends JPanel {
        int radius;
        public RoundedPanel(int radius) { this.radius = radius; setOpaque(false); }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 60));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }
    
    //for the Result Screen
    public void setPlayerNames(String player1, String player2) {
        p1.setText(player1);
        p2.setText(player2);
    }
    
 // ==========  CLEANUP ==========
    @Override
    public void dispose() {
        musicManager.removeMusicStateListener(this);
        System.out.println("✓ NewGameScreen: Unregistered from music updates");
        super.dispose();
    }
    // =================================
}
