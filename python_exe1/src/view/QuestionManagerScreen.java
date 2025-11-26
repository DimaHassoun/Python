package view;

import Model.Consts;
import controller.QuestionManagerLogic;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
    private JComboBox<String> searchMode;
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

        // Background Panel Setup
        BackgroundPanel panel = new BackgroundPanel("src/resource/background.jpg");
        panel.setLayout(new BorderLayout(20, 20));
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        setContentPane(panel);

        // ==========================
        // TOP SECTION (Header + Search)
        // ==========================
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        // --- Header Row (Icons, Title, Back Button) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0)); 

        // Left side: Icons
        JPanel iconsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        iconsPanel.setOpaque(false);

        // Settings Menu
        JPopupMenu settingsMenu = new JPopupMenu();
        JMenuItem rulesItem = new JMenuItem("Game Rules");
        rulesItem.addActionListener(e -> new GameRulesScreen());
        settingsMenu.add(rulesItem);

        JMenuItem soundItem = new JMenuItem("Sound Settings");
        soundItem.addActionListener(e -> showVolumeControl());
        settingsMenu.add(soundItem);

        JLabel settingsIcon = new JLabel("⚙");
        settingsIcon.setFont(new Font("SansSerif", Font.BOLD, 36));
        settingsIcon.setForeground(new Color(246, 230, 138));
        settingsIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settingsIcon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                settingsMenu.show(settingsIcon, e.getX(), e.getY());
            }
        });

        // Music Icon
        musicLabel = new JLabel(musicManager.isPlaying() ? "♪" : "🔇");
        musicLabel.setFont(new Font("Dialog", Font.BOLD, 36));
        musicLabel.setForeground(musicManager.isPlaying() ? new Color(246, 230, 138) : new Color(180, 180, 180));
        musicLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        musicLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { toggleMusic(); }
        });

        iconsPanel.add(settingsIcon);
        iconsPanel.add(musicLabel);

        // Center: Title
        JLabel title = new JLabel("Question's Manager", SwingConstants.CENTER);
        title.setFont(new Font("Verdana", Font.BOLD, 40));
        title.setForeground(new Color(246, 230, 138));

        // Right: Back Button
        JLabel back = new JLabel("Back");
        back.setFont(new Font("Verdana", Font.BOLD, 28));
        back.setForeground(new Color(246, 230, 138));
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.setHorizontalAlignment(SwingConstants.RIGHT);
        back.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new FirstScreen();
                QuestionManagerScreen.this.dispose();
            }
        });

        headerPanel.add(iconsPanel, BorderLayout.WEST);
        headerPanel.add(title, BorderLayout.CENTER);
        headerPanel.add(back, BorderLayout.EAST);

        // --- Search Row ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        searchPanel.setOpaque(false);

        searchField = new PlaceholderTextField("🔍 Search by Question ID", 30); 
        searchField.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        searchField.setPreferredSize(new Dimension(500, 45)); 
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(246, 230, 138), 2),
                BorderFactory.createEmptyBorder(5, 20, 5, 10)
        ));

        String[] searchOptions = {"Search By ID", "By Question Text", "By Difficulty"};
        searchMode = new JComboBox<>(searchOptions);
        searchMode.setFont(new Font("Verdana", Font.PLAIN, 16));
        searchMode.setBackground(new Color(250, 248, 240));
        searchMode.setPreferredSize(new Dimension(200, 45));

        searchPanel.add(searchField);
        searchPanel.add(searchMode);

        // Add rows to top container
        topContainer.add(headerPanel);
        topContainer.add(searchPanel);

        panel.add(topContainer, BorderLayout.NORTH);

        // ==========================
        // CENTER SECTION (Table)
        // ==========================
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
        scroll.setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(180, 160, 200), 2));
        
        panel.add(scroll, BorderLayout.CENTER);

        // ==========================
        // BOTTOM SECTION (Buttons)
        // ==========================
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20)); 
        buttonsPanel.setOpaque(false);

        JButton deleteBtn = new RoundedButton("🗑 Delete Questions");
        deleteBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        deleteBtn.setPreferredSize(new Dimension(240, 55));

        JButton editBtn = new RoundedButton("✎ Edit Question");
        editBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        editBtn.setPreferredSize(new Dimension(220, 55));

        JButton addBtn = new RoundedButton("➕ Add Question");
        addBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        addBtn.setPreferredSize(new Dimension(220, 55));

        buttonsPanel.add(deleteBtn);
        buttonsPanel.add(editBtn);
        buttonsPanel.add(addBtn);

        panel.add(buttonsPanel, BorderLayout.SOUTH);

        // ==========================
        // LOGIC AND LISTENERS
        // ==========================
        
        // Search Logic
        searchMode.addActionListener(e -> {
            String mode = (String) searchMode.getSelectedItem();
            switch (mode) {
                case "By Question Text":
                    searchField.setPlaceholder("🔍 Search by Question Text");
                    break;
                case "By Difficulty":
                    searchField.setPlaceholder("🔍 Search by Difficulty");
                    break;
                default:
                    searchField.setPlaceholder("🔍 Search by Question ID");
                    break;
            }
            searchField.setText("");
            sorter.setRowFilter(null);
            table.repaint();
        });

        // Load Data
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
            
            // Set renderers and width
            CenteredTextAreaRenderer renderer = new CenteredTextAreaRenderer();
            for (int i = 0; i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(renderer);
                table.getColumnModel().getColumn(i).setPreferredWidth(300); 
            }

            sorter = new TableRowSorter<>(model);
            table.setRowSorter(sorter);
            
            // Filter Logic
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
                        case "By Question Text":
                            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 1));
                            break;
                        case "By Difficulty":
                            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 2));
                            break;
                        default:
                            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 0));
                    }
                    table.repaint();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading CSV: " + e.getMessage());
        }

        // Button Actions
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

                // Convert view row index to model row index
                int modelRow = table.convertRowIndexToModel(selectedRow);

                new EditQuestion(model, modelRow, Consts.getCSVPath());
                QuestionManagerScreen.this.dispose();
            }
        });

        addBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new AddQuestion();
                QuestionManagerScreen.this.dispose();
            }
        });

        setVisible(true);
    }

    // --- Helper Methods ---

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

    // --- Inner Classes ---

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

        @Override
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
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5); 
            panel.add(textArea, gbc);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            textArea.setText(value == null ? "" : value.toString());
            int width = table.getColumnModel().getColumn(column).getWidth();
            textArea.setSize(new Dimension(width - 10, 100)); 

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
