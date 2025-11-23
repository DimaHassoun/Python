package view;

import Model.Consts;
import controller.QuestionManagerLogic;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;

public class QuestionManagerScreen extends JFrame {

    private PlaceholderTextField searchField;
    private DefaultTableModel model;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;
    private JLabel musicLabel;
    private MusicManager musicManager;

    public QuestionManagerScreen() {
        setTitle("Question's Manager");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true); // allow resizing

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
        musicLabel.setForeground(musicManager.isPlaying() ? new Color(246, 230, 138) : new Color(180, 180, 180));
        musicLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        musicLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { toggleMusic(); }
        });
        panel.add(musicLabel);

        // Title
        JLabel title = new JLabel("Question's Manager", SwingConstants.CENTER);
        title.setFont(new Font("Verdana", Font.BOLD, 40));
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
                QuestionManagerScreen.this.dispose();
            }
        });
        panel.add(back);

        // Search field
        searchField = new PlaceholderTextField("🔍 Search by Question Text OR by Difficulty...", 20);
        searchField.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(246, 230, 138), 2),
                BorderFactory.createEmptyBorder(5, 20, 5, 10)
        ));
        panel.add(searchField);

        // Table
        table = new JTable();
        table.setRowHeight(150);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setForeground(new Color(50, 50, 50));
        table.setBackground(new Color(230, 210, 240));
        table.setOpaque(true);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Verdana", Font.BOLD, 14));
        header.setBackground(new Color(180, 160, 200));
        header.setForeground(Color.BLACK);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40)); 


        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(new Color(230, 210, 240));
        panel.add(scroll);

        // Bottom buttons
        JButton deleteBtn = new RoundedButton("🗑 Delete Questions");
        deleteBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        panel.add(deleteBtn);

        JButton editBtn = new RoundedButton("✎ Edit Question");
        editBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        panel.add(editBtn);

        JButton addBtn = new RoundedButton("➕ Add Question");
        addBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        panel.add(addBtn);

         // -----------------------
         // Load DATA from CSV
        // -----------------------
        String csvPath = Consts.getCSVPath();
        try {
            model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            DefaultTableModel temp = QuestionManagerLogic.loadCSVToTable(csvPath);
            for (int i = 0; i < temp.getColumnCount(); i++) model.addColumn(temp.getColumnName(i));
            for (int i = 0; i < temp.getRowCount(); i++) {
                Object[] rowData = new Object[temp.getColumnCount()];
                for (int j = 0; j < temp.getColumnCount(); j++) rowData[j] = temp.getValueAt(i, j);
                model.addRow(rowData);
            }
            table.setModel(model);
            // Set column width to 300
            for (int i = 0; i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setPreferredWidth(300);
            }

            CenteredTextAreaRenderer renderer = new CenteredTextAreaRenderer();
            for (int i = 0; i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(renderer);
            }

            sorter = new TableRowSorter<>(model);
            table.setRowSorter(sorter);

            searchField.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { filter(); }
                public void removeUpdate(DocumentEvent e) { filter(); }
                public void changedUpdate(DocumentEvent e) { filter(); }
                private void filter() {
                    String text = searchField.getText().trim();
                    if (text.isEmpty()) sorter.setRowFilter(null);
                    else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 1, 2));
                    table.repaint();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading CSV: " + e.getMessage());
        }

        // -----------------------
        // Dynamic positioning
        // -----------------------
        positionComponents(settingsIcon, musicLabel, title, back, searchField, scroll, deleteBtn, editBtn, addBtn);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                positionComponents(settingsIcon, musicLabel, title, back, searchField, scroll, deleteBtn, editBtn, addBtn);
            }
        });

        setVisible(true);

        // Action listeners for bottom buttons (your original code)
        deleteBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new DeleteQuestions(model, QuestionManagerScreen.this);
                QuestionManagerScreen.this.dispose();
            }
        });
        editBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(QuestionManagerScreen.this, "Please select a question to edit.", "No selection", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                new EditQuestion(model, selectedRow, Consts.getCSVPath());
                QuestionManagerScreen.this.dispose();
            }
        });
        addBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new AddQuestion();
                QuestionManagerScreen.this.dispose();
            }
        });
    }

    private void positionComponents(JLabel settingsIcon, JLabel musicLabel, JLabel title, JLabel back,
            PlaceholderTextField searchField, JScrollPane scroll,
            JButton deleteBtn, JButton editBtn, JButton addBtn) {

    	int w = getContentPane().getWidth();
        int h = getContentPane().getHeight();
        settingsIcon.setBounds(20, 20, 50, 50);
        musicLabel.setBounds(85, 20, 50, 50);
        back.setBounds(w - 150, 30, 120, 50);
        title.setBounds((w - 600) / 2, 20, 600, 60);
        searchField.setBounds((w - 700) / 2, 110, 700, 40);
        scroll.setBounds(50, 180, w - 100, 360);

        // Spread the bottom buttons evenly
        int bottomY = h - 100; // distance from bottom
        int totalButtonWidth = 240 + 200 + 220; // sum of button widths
        int spaceBetween = (w - totalButtonWidth) / 4; // 4 gaps: left + 2 between + right
        deleteBtn.setBounds(spaceBetween, bottomY, 240, 50);
        editBtn.setBounds(spaceBetween * 2 + 240, bottomY, 200, 50);
        addBtn.setBounds(spaceBetween * 3 + 240 + 200, bottomY, 220, 50);
    }

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
        public PlaceholderTextField(String placeholder, int columns) { super(columns); this.placeholder = placeholder; }
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

    static class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setFont(new Font("Verdana", Font.BOLD, 16));
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
    }
    
    class CenteredTextAreaRenderer implements TableCellRenderer {

        private final JPanel panel;
        private final JTextArea textArea;

        public CenteredTextAreaRenderer() {
            panel = new JPanel(new GridBagLayout()); 
            panel.setOpaque(true);

            textArea = new JTextArea();
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setEditable(false);
            textArea.setFocusable(false);
            textArea.setFont(new Font("Arial", Font.PLAIN, 13));
            textArea.setOpaque(false); 
            textArea.setBorder(null);

            panel.add(textArea);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {

            textArea.setText(value == null ? "" : value.toString());

            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
                textArea.setForeground(table.getSelectionForeground());
            } else {
                panel.setBackground(new Color(230, 210, 240));
                textArea.setForeground(Color.BLACK);
            }

            return panel;
        }
    }


}
