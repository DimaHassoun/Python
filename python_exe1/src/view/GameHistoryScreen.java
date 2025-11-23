package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;

public class GameHistoryScreen extends JFrame {

    private PlaceholderTextField searchField;
    private JLabel musicLabel;
    private MusicManager musicManager;

    public GameHistoryScreen() {
        setTitle("Game History");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      

        musicManager = MusicManager.getInstance();

        BackgroundPanel panel = new BackgroundPanel("src/background.jpg");
        panel.setLayout(null);
        setContentPane(panel);
        
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

        // Settings icon
        JLabel settingsIcon = new JLabel("⚙");
        settingsIcon.setFont(new Font("SansSerif", Font.BOLD, 36));
        settingsIcon.setForeground(new Color(246, 230, 138));
        settingsIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settingsIcon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                settingsMenu.show(settingsIcon, e.getX(), e.getY());
            }
        });
        panel.add(settingsIcon);

        // Music icon
        musicLabel = new JLabel(musicManager.isPlaying() ? "♪" : "🔇");
        musicLabel.setFont(new Font("Dialog", Font.BOLD, 36));
        musicLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        musicLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                toggleMusic();
            }
        });
        panel.add(musicLabel);

        // Title
        JLabel title = new JLabel("Game's History", SwingConstants.CENTER);
        title.setFont(new Font("Verdana", Font.BOLD, 48));
        title.setForeground(new Color(246, 230, 138));
        panel.add(title);

        // Back button
        JLabel back = new JLabel("Back");
        back.setFont(new Font("Verdana", Font.BOLD, 28));
        back.setForeground(new Color(246, 230, 138));
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new FirstScreen();
                GameHistoryScreen.this.dispose();
            }
        });
        panel.add(back);

        // Search bar
        searchField = new PlaceholderTextField("🔍 Search by game date OR player name...", 20);
        searchField.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        panel.add(searchField);

        // Table
        String[] columns = {"Game No.", "Date", "Player 1", "Player 2", "Difficulty", "Final Score", "Result"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setFont(new Font("Verdana", Font.PLAIN, 18));
        table.setRowHeight(40);
        table.setForeground(Color.BLACK);
        table.setBackground(new Color(230, 210, 240));
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Verdana", Font.BOLD, 16));
        header.setBackground(new Color(180, 160, 200));
        header.setForeground(Color.BLACK);
        header.setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(new Color(230, 210, 240));
        panel.add(scroll);

        // TableRowSorter for filtering
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Search functionality
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }

            private void search() {
                String text = searchField.getText().trim();
                if (text.length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 1, 2, 3));
            }
        });

        // Initial positioning
        positionComponents(settingsIcon, musicLabel, title, back, searchField, scroll);

        // Dynamic repositioning on resize
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                positionComponents(settingsIcon, musicLabel, title, back, searchField, scroll);
            }
        });

        setVisible(true);
    }

    private void positionComponents(JLabel settingsIcon, JLabel musicLabel, JLabel title, JLabel back,
                                    PlaceholderTextField searchField, JScrollPane scroll) {
        int w = getWidth();
        int h = getHeight();

        settingsIcon.setBounds(20, 20, 50, 50);
        musicLabel.setBounds(85, 20, 50, 50);
        back.setBounds(w - 150, 40, 120, 50);
        title.setBounds(w / 2 - 250, 30, 500, 70);
        searchField.setBounds(w / 2 - 350, 140, 700, 50);
        scroll.setBounds(w / 2 - 450, 220, 900, 350);
    }

    // Music functions
    private void toggleMusic() {
        musicManager.toggleMusic();
        updateMusicIcon();
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
        });

        panel.add(volumeLabel, BorderLayout.NORTH);
        panel.add(volumeSlider, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "Volume Control", JOptionPane.PLAIN_MESSAGE);
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

    class PlaceholderTextField extends JTextField {
        private final String placeholder;

        public PlaceholderTextField(String placeholder, int columns) {
            super(columns);
            this.placeholder = placeholder;
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Color.GRAY);
                g2.setFont(getFont());
                Insets insets = getInsets();
                g2.drawString(placeholder, insets.left, getHeight() / 2 + getFont().getSize() / 2 - 2);
                g2.dispose();
            }
        }
    }
}
