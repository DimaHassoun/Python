package view;

import java.awt.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

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

        // Background panel
        BackgroundPanel panel = new BackgroundPanel("src/background.jpg");
        panel.setLayout(new BorderLayout());
        setContentPane(panel);

        // ===========================
        // TOP BAR (Back button)
        // ===========================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setLayout(null);
        topPanel.setPreferredSize(new Dimension(1200, 100));

        // Back button
        JLabel back = new JLabel("Back");
        back.setFont(new Font("Verdana", Font.BOLD, 28));
        back.setForeground(new Color(246, 230, 138));
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.setBounds(1050, 30, 120, 50);
        back.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new QuestionManagerScreen();
                DeleteQuestions.this.dispose();
            }
        });
        topPanel.add(back, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        // ===========================
        // CENTER PANEL (Instruction + Table)
        // ===========================
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Instruction label above table
        JLabel instruction = new JLabel("Select the questions you want to delete from the list");
        instruction.setFont(new Font("Verdana", Font.BOLD, 24));
        instruction.setForeground(new Color(246, 230, 138));
        instruction.setHorizontalAlignment(JLabel.CENTER);
        instruction.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0)); 
        centerPanel.add(instruction, BorderLayout.NORTH);
        
        // ===========================
        // TABLE CENTER PANEL
        // ===========================
       
        // Prepare columns: Select + original columns
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Select");
        for (int i = 0; i < originalModel.getColumnCount(); i++) {
            columnNames.add(originalModel.getColumnName(i));
        }

        model = new DefaultTableModel();
        model.setColumnIdentifiers(columnNames);

        // Copy rows from original model
        for (int i = 0; i < originalModel.getRowCount(); i++) {
            Object[] row = new Object[originalModel.getColumnCount() + 1];
            row[0] = false;
            for (int j = 0; j < originalModel.getColumnCount(); j++) {
                row[j + 1] = originalModel.getValueAt(i, j);
            }
            model.addRow(row);
        }

        // Table setup
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

        table.getTableHeader().setReorderingAllowed(false);
        table.setForeground(new Color(50, 50, 50));
        table.setBackground(new Color(230, 210, 240, 180));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(180, 160, 200));
        header.setForeground(Color.BLACK);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setPreferredSize(new Dimension(1500, 800)); 
        centerPanel.add(scroll, BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);

        // ===========================
        // BOTTOM PANEL - DELETE BUTTON
        // ===========================
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 40, 20));

        deleteSelectedBtn = new RoundedButton("Delete Selected");
        deleteSelectedBtn.setFont(new Font("Verdana", Font.BOLD, 18));
        deleteSelectedBtn.setPreferredSize(new Dimension(250, 55));

        bottomPanel.add(deleteSelectedBtn);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // ===========================
        // DELETE LOGIC
        // ===========================
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

    // ===========================
    // Rounded Button (unchanged)
    // ===========================
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
