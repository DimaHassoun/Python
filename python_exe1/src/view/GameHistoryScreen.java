package view;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

public class GameHistoryScreen extends JFrame {

    private PlaceholderTextField searchField;
    private JLabel musicLabel;
    private MusicManager musicManager;
    private JTable table;
    private JComboBox<String> searchMode;
    private TableRowSorter<DefaultTableModel> sorter;
    private DefaultTableModel model;

    // Base dimensions for scaling
    private final int BASE_WIDTH = 1200;
    private final int BASE_HEIGHT = 700;

    // Original bounds of components (x, y, width, height)
    private Rectangle settingsIconBounds = new Rectangle(20, 20, 50, 50);
    private Rectangle musicLabelBounds = new Rectangle(85, 20, 50, 50);
    private Rectangle backBounds = new Rectangle(BASE_WIDTH - 150, 40, 120, 50);
    private Rectangle titleBounds = new Rectangle(BASE_WIDTH / 2 - 250, 30, 500, 70);
    private Rectangle searchFieldBounds = new Rectangle((BASE_WIDTH - 700)/3 +15, 140, 650, 50);
    private Rectangle searchModeBounds = new Rectangle((BASE_WIDTH - 700)/3 + 685, 140, 180, 50);
    private Rectangle scrollBounds = new Rectangle(BASE_WIDTH / 2 - 450, 220, 900, 350);

    private BackgroundPanel panel;

    public GameHistoryScreen() {
        setTitle("Game History");
        setSize(BASE_WIDTH, BASE_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        musicManager = MusicManager.getInstance();

        panel = new BackgroundPanel("src/resource/background.jpg");
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
        musicLabel.setForeground(musicManager.isPlaying() ? new Color(246, 230, 138) : new Color(180, 180, 180));
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

        // Search mode ComboBox
        String[] searchOptions = {"Search By Date", "By Players Name"};
        searchMode = new JComboBox<>(searchOptions);
        searchMode.setFont(new Font("Verdana", Font.PLAIN, 16));
        searchMode.setBackground(new Color(250, 248, 240));
        panel.add(searchMode);
        searchMode.addActionListener(e -> {
            String mode = (String) searchMode.getSelectedItem();
            switch (mode) {
                case "By Players Name":
                    searchField.setPlaceholder("🔍 Search by players name");
                    break;
                default:
                    searchField.setPlaceholder("🔍 Search by game date");
                    break;
            }
            searchField.setText("");
            sorter.setRowFilter(null);
            table.repaint();
        });

        // Search bar
        searchField = new PlaceholderTextField("🔍 Search by game date", 20);
        searchField.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        panel.add(searchField);

        // Table
        String[] columns = {"Game No.", "Date", "Player 1", "Player 2", "Difficulty", "Final Score", "Result"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
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

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        populateTable();

        // Filtering logic depending on ComboBox choice
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }

            private void filter() {
                String text = searchField.getText().trim();
                String mode = (String) searchMode.getSelectedItem();

                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                    return;
                }

                switch (mode) {
                    case "By Players Name":
                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 2, 3));
                        break;
                    default:
                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 1));
                }
                table.repaint();
            }
        });

        // Initial positioning
        resizeComponents(settingsIcon, musicLabel, title, back, searchField, scroll);

        // Dynamic repositioning on resize
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                resizeComponents(settingsIcon, musicLabel, title, back, searchField, scroll);
            }
        });

        setVisible(true);
    }

    /** 
     * Resize and scale all components based on current frame size.
     * Scales positions, sizes, and fonts proportionally.
     */
    private void resizeComponents(JLabel settingsIcon, JLabel musicLabel, JLabel title,
                                  JLabel back, PlaceholderTextField searchField, JScrollPane scroll) {
        double xRatio = getWidth() / (double) BASE_WIDTH;
        double yRatio = getHeight() / (double) BASE_HEIGHT;
        double fontRatio = Math.min(xRatio, yRatio);

        // Scale positions and sizes
        settingsIcon.setBounds(
                (int)(settingsIconBounds.x * xRatio),
                (int)(settingsIconBounds.y * yRatio),
                (int)(settingsIconBounds.width * xRatio),
                (int)(settingsIconBounds.height * yRatio)
        );

        musicLabel.setBounds(
                (int)(musicLabelBounds.x * xRatio),
                (int)(musicLabelBounds.y * yRatio),
                (int)(musicLabelBounds.width * xRatio),
                (int)(musicLabelBounds.height * yRatio)
        );

        back.setBounds(
                (int)(backBounds.x * xRatio),
                (int)(backBounds.y * yRatio),
                (int)(backBounds.width * xRatio),
                (int)(backBounds.height * yRatio)
        );

        title.setBounds(
                (int)(titleBounds.x * xRatio),
                (int)(titleBounds.y * yRatio),
                (int)(titleBounds.width * xRatio),
                (int)(titleBounds.height * yRatio)
        );

        searchField.setBounds(
                (int)(searchFieldBounds.x * xRatio),
                (int)(searchFieldBounds.y * yRatio),
                (int)(searchFieldBounds.width * xRatio),
                (int)(searchFieldBounds.height * yRatio)
        );

        searchMode.setBounds(
                (int)(searchModeBounds.x * xRatio),
                (int)(searchModeBounds.y * yRatio),
                (int)(searchModeBounds.width * xRatio),
                (int)(searchModeBounds.height * yRatio)
        );

        scroll.setBounds(
                (int)(scrollBounds.x * xRatio),
                (int)(scrollBounds.y * yRatio),
                (int)(scrollBounds.width * xRatio),
                (int)(scrollBounds.height * yRatio)
        );

        // Scale fonts
        settingsIcon.setFont(settingsIcon.getFont().deriveFont((float)(36 * fontRatio)));
        musicLabel.setFont(musicLabel.getFont().deriveFont((float)(36 * fontRatio)));
        back.setFont(back.getFont().deriveFont((float)(28 * fontRatio)));
        title.setFont(title.getFont().deriveFont((float)(48 * fontRatio)));
        searchField.setFont(searchField.getFont().deriveFont((float)(20 * fontRatio)));
        searchMode.setFont(searchMode.getFont().deriveFont((float)(16 * fontRatio)));
        table.setFont(table.getFont().deriveFont((float)(18 * fontRatio)));
        table.setRowHeight((int)(40 * yRatio));
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont((float)(16 * fontRatio)));
    }
    
    // Fill game history table
    private void populateTable() {
        //  Clear existing rows
        model.setRowCount(0);

     // Get the history list from the controller
        java.util.List<Model.GameHistory> history = controller.GameHistoryController.getHistoryList();

        // Date formatter for readable display
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Fill the table with history entries
        for (Model.GameHistory gh : history) {
            Model.Game g = gh.getGame();
            String id = String.valueOf(g.getId());
            String date = gh.getGamedate() != null ? gh.getGamedate().format(fmt) : "";
            String p1 = g.getPlayer1Name() != null ? g.getPlayer1Name() : "";
            String p2 = g.getPlayer2Name() != null ? g.getPlayer2Name() : "";
            String diff = g.getDifficulty() != null ? g.getDifficulty().name() : "";
            String score = String.valueOf(gh.getScore());
            String result = gh.getGameResult() != null ? gh.getGameResult().name() : "";

            model.addRow(new Object[]{ id, date, p1, p2, diff, score, result });
        }
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
        private String placeholder;

        public PlaceholderTextField(String placeholder, int columns) {
            super(columns);
            this.placeholder = placeholder;
        }

        public void setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
            repaint();
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

    /** Custom Background Panel to scale image proportionally */
    class BackgroundPanel extends JPanel {
        private BufferedImage backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = ImageIO.read(new File(imagePath));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}

