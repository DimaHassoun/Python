package view;

import java.awt.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import Model.Consts;
import controller.QuestionManagerLogic;

public class DeleteQuestions extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JButton deleteSelectedBtn;

    public DeleteQuestions(DefaultTableModel originalModel, QuestionManagerScreen parent) {
        setTitle("Delete Questions");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);

        // --- Background Panel ---
        BackgroundPanel panel = new BackgroundPanel("src/background.jpg");
        panel.setLayout(new BorderLayout(20, 20));
        panel.setBorder(new EmptyBorder(30, 50, 30, 50));
        setContentPane(panel);

        // -------------------------
        // TOP COMPONENTS
        // -------------------------
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel back = new JLabel("Back");
        back.setFont(new Font("Verdana", Font.BOLD, 28));
        back.setForeground(new Color(246, 230, 138));
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.setHorizontalAlignment(SwingConstants.RIGHT);
        back.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new QuestionManagerScreen();
                DeleteQuestions.this.dispose();
            }
        });

        JLabel instruction = new JLabel("Select the questions you want to delete from the list", SwingConstants.CENTER);
        instruction.setFont(new Font("Verdana", Font.BOLD, 24));
        instruction.setForeground(new Color(246, 230, 138));
        instruction.setBorder(new EmptyBorder(20, 0, 10, 0));

        topPanel.add(back, BorderLayout.NORTH);
        topPanel.add(instruction, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);

        // -------------------------
        // TABLE SETUP
        // -------------------------
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Select");
        columnNames.add("No.");        
        columnNames.add("Question"); 

        model = new DefaultTableModel();
        model.setColumnIdentifiers(columnNames);

        for (int i = 0; i < originalModel.getRowCount(); i++) {
            Object[] row = new Object[3]; 
            row[0] = false;
            row[1] = originalModel.getValueAt(i, 0);
            row[2] = originalModel.getValueAt(i, 1);
            model.addRow(row);
        }

        table = new JTable(model) {
            @Override
            public Class<?> getColumnClass(int column) {
                return (column == 0) ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        // ========================================================
        // STYLING AND SIZE ADJUSTMENT
        // ========================================================
        
        // 1. Row Height (Reduced from 150 to 90)
        table.setRowHeight(90); 
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        
        // 2. Colors
        table.setForeground(new Color(50, 50, 50));
        table.setBackground(new Color(230, 210, 240)); 
        table.setOpaque(true);
        table.setFillsViewportHeight(true);

        // 3. Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Verdana", Font.BOLD, 14));
        header.setBackground(new Color(180, 160, 200));
        header.setForeground(Color.BLACK);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

        // 4. Custom Renderer for Text Wrapping 
        CenteredTextAreaRenderer textRenderer = new CenteredTextAreaRenderer();
        table.getColumnModel().getColumn(1).setCellRenderer(textRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(textRenderer);

        // Column Widths
        table.getColumnModel().getColumn(0).setMaxWidth(80);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setMaxWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        
        // Scroll Pane
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(new Color(230, 210, 240));
        scroll.setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(180, 160, 200), 2));
        
        panel.add(scroll, BorderLayout.CENTER);

        // -------------------------
        // DELETE BUTTON
        // -------------------------
        deleteSelectedBtn = new RoundedButton("Delete Selected");
        deleteSelectedBtn.setPreferredSize(new Dimension(250, 55));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(deleteSelectedBtn);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // -------------------------
        // DELETE LOGIC
        // -------------------------
        deleteSelectedBtn.addActionListener(e -> {
            int deleted = 0;
            for (int i = model.getRowCount() - 1; i >= 0; i--) {
                boolean selected = (Boolean) model.getValueAt(i, 0);
                if (selected) {
                    model.removeRow(i);
                    originalModel.removeRow(i);
                    deleted++;
                }
            }
            if (deleted > 0) {
                try {
                    QuestionManagerLogic.saveTableToCSV(originalModel, Consts.getCSVPath());
                    JOptionPane.showMessageDialog(this, deleted + " questions deleted successfully.");
                    new QuestionManagerScreen();
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error saving CSV: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "No questions selected.");
            }
        });

        setVisible(true);
    }

    // -------------------------
    // RENDERER FOR TEXT WRAPPING
    // -------------------------
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

    // -------------------------
    // ROUNDED BUTTON
    // -------------------------
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
}