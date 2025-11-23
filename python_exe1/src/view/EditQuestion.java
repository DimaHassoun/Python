package view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import controller.QuestionManagerLogic;

import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;

public class EditQuestion extends JFrame {

    // Fields for question editing
    private JTextField idField;
    private JTextArea questionArea;
    private JTextArea[] optionAreas; // Array for options A-D
    private JRadioButton diff1, diff2, diff3, diff4;
    private ButtonGroup difficultyGroup; // To ensure only one difficulty is selected
    private JRadioButton ansA, ansB, ansC, ansD;
    private ButtonGroup answerGroup; // To ensure only one answer is selected

    // Constructor takes the table model, row index, and path to CSV
    public EditQuestion(DefaultTableModel model, int rowIndex, String csvPath) {
        setTitle("Edit Question");
        setSize(1200, 700);
        setLocationRelativeTo(null); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLayout(new BorderLayout(10, 10));
        BackgroundPanel editQuestion = new BackgroundPanel("src/background.jpg");
        editQuestion.setLayout(new BoxLayout(editQuestion, BoxLayout.Y_AXIS));
        editQuestion.setBorder(BorderFactory.createEmptyBorder(20, 50, 10, 50));
        editQuestion.setBackground(new Color(0, 0, 0, 0)); 

        // ID 
        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        idPanel.setOpaque(false);
        JLabel idLabel = new JLabel("Question ID:");
        idLabel.setForeground(new Color(255, 230, 80));
        idLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        idPanel.add(idLabel);

        // Pre-fill the ID from the table model
        idField = new JTextField(model.getValueAt(rowIndex, 0).toString(), 15);
        idField.setBackground(new Color(230, 230, 250));
        idPanel.add(idField);
        editQuestion.add(idPanel);
        editQuestion.add(Box.createRigidArea(new Dimension(0, 10)));

        //Question Text
        JPanel questionPanel = new JPanel(new BorderLayout());
        questionPanel.setOpaque(false);
        JLabel questionLabel = new JLabel("Question Text:", SwingConstants.LEFT);
        questionLabel.setForeground(new Color(255, 230, 80));
        questionLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        questionPanel.add(questionLabel, BorderLayout.NORTH);

        // Pre-fill the question text
        questionArea = new JTextArea(model.getValueAt(rowIndex, 1).toString(), 3, 30);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setBackground(new Color(230, 230, 250));
        questionPanel.add(new JScrollPane(questionArea), BorderLayout.CENTER);
        editQuestion.add(questionPanel);
        editQuestion.add(Box.createRigidArea(new Dimension(0, 10)));

        //Difficulty
        JPanel diffPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        diffPanel.setOpaque(false);
        JLabel diffLabel = new JLabel("Difficulty:");
        diffLabel.setForeground(new Color(255, 230, 80));
        diffLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        diffPanel.add(diffLabel);

        // Create difficulty radio buttons
        diff1 = new JRadioButton("1 (Easy)");
        diff2 = new JRadioButton("2 (Medium)");
        diff3 = new JRadioButton("3 (Hard)");
        diff4 = new JRadioButton("4 (Expert)");

        JRadioButton[] diffs = {diff1, diff2, diff3, diff4};
        difficultyGroup = new ButtonGroup();
        for (int i = 0; i < diffs.length; i++) {
            diffs[i].setForeground(new Color(255, 230, 80));
            diffs[i].setOpaque(false);
            difficultyGroup.add(diffs[i]);
            diffPanel.add(diffs[i]);
        }

        // Pre-select difficulty from model
        int diffValue = Integer.parseInt(model.getValueAt(rowIndex, 2).toString());
        if (diffValue >= 1 && diffValue <= 4) {
            diffs[diffValue - 1].setSelected(true);
        }

        editQuestion.add(diffPanel);
        editQuestion.add(Box.createRigidArea(new Dimension(0, 10)));

        //Options 
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        optionsPanel.setOpaque(false);
        TitledBorder border = BorderFactory.createTitledBorder("Options");
        border.setTitleColor(new Color(255, 230, 80));
        border.setTitleFont(new Font("Tahoma", Font.BOLD, 20));
        optionsPanel.setBorder(border);
        optionsPanel.setPreferredSize(new Dimension(1100, 300));

        optionAreas = new JTextArea[4];
        for (int i = 0; i < 4; i++) {
            optionAreas[i] = new JTextArea(model.getValueAt(rowIndex, 3 + i).toString());
            optionAreas[i].setLineWrap(true);
            optionAreas[i].setWrapStyleWord(true);
            optionAreas[i].setBackground(new Color(230, 230, 250));
            optionsPanel.add(labeledArea(Character.toString((char) ('A' + i)), optionAreas[i]));
        }
        editQuestion.add(optionsPanel);
        editQuestion.add(Box.createRigidArea(new Dimension(0, 10)));

        // Correct Answer  
        JPanel answerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        answerPanel.setOpaque(false);
        JLabel ansLabel = new JLabel("Correct Answer:");
        ansLabel.setForeground(new Color(255, 230, 80));
        ansLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        answerPanel.add(ansLabel);

        ansA = new JRadioButton("A");
        ansB = new JRadioButton("B");
        ansC = new JRadioButton("C");
        ansD = new JRadioButton("D");

        JRadioButton[] answers = {ansA, ansB, ansC, ansD};
        answerGroup = new ButtonGroup();
        for (JRadioButton ans : answers) {
            ans.setForeground(new Color(255, 230, 80));
            ans.setOpaque(false);
            answerGroup.add(ans);
            answerPanel.add(ans);
        }

        // Pre-select correct answer
        String correctAnswer = model.getValueAt(rowIndex, model.getColumnCount() - 1).toString();
        switch (correctAnswer) {
            case "A" -> ansA.setSelected(true);
            case "B" -> ansB.setSelected(true);
            case "C" -> ansC.setSelected(true);
            case "D" -> ansD.setSelected(true);
        }

        editQuestion.add(answerPanel);
        editQuestion.add(Box.createRigidArea(new Dimension(0, 10)));

        // Buttons 
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        RoundedButton saveBtn = new RoundedButton("Save Changes");
        saveBtn.setPreferredSize(new Dimension(180, 45));
        RoundedButton backBtn = new RoundedButton("Back");
        backBtn.setPreferredSize(new Dimension(180, 45));
        backBtn.addActionListener(e -> {
            new QuestionManagerScreen().setVisible(true);
            this.dispose();
        });

        // Save button functionality
        saveBtn.addActionListener(e -> {
        	try {
        		String newIdText = idField.getText().trim();
                int newId = Integer.parseInt(newIdText);

                //The old ID that appears before editing
                int oldId = Integer.parseInt(model.getValueAt(rowIndex, 0).toString());

                 // If the user changed ID → need to check if it already exists in the system
                if (newId != oldId) {
                    if (QuestionManagerLogic.isIdExistsInCSV(csvPath, newId)) {
                        JOptionPane.showMessageDialog(this,
                                "Error: The ID you entered already exists in the system!",
                                "Duplicate ID",
                                JOptionPane.ERROR_MESSAGE);
                        return; // Unsave
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "ID must be a number!",
                        "Invalid ID",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error checking ID in CSV: " + ex.getMessage(),
                        "IO Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        	
        	//validation for empty fields
            //check question text
            if (questionArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "You must enter a question!",
                        "Missing Question",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            //check options
            for (int i = 0; i < 4; i++) {
                if (optionAreas[i].getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Option " + (char) ('A' + i) + " cannot be empty!",
                            "Missing Option",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Update model with new data
            model.setValueAt(idField.getText().trim(), rowIndex, 0);
            model.setValueAt(questionArea.getText().trim(), rowIndex, 1);

            // Update difficulty
            int selectedDifficulty = 0;
            for (int i = 0; i < diffs.length; i++) {
                if (diffs[i].isSelected()) {
                    selectedDifficulty = i + 1;
                    break;
                }
            }
            model.setValueAt(selectedDifficulty, rowIndex, 2);

            // Update options
            for (int i = 0; i < 4; i++) {
                model.setValueAt(optionAreas[i].getText().trim(), rowIndex, 3 + i);
            }

            // Update correct answer
            char selectedAnswer = ' ';
            for (int i = 0; i < answers.length; i++) {
                if (answers[i].isSelected()) {
                    selectedAnswer = (char) ('A' + i);
                    break;
                }
            }
            model.setValueAt(String.valueOf(selectedAnswer), rowIndex, model.getColumnCount() - 1);

            // Save changes to CSV
            try {
                QuestionManagerLogic.saveTableToCSV(model, csvPath);
                JOptionPane.showMessageDialog(this, "Question updated successfully!");
                this.dispose();
                new QuestionManagerScreen().setVisible(true);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving CSV: " + ex.getMessage());
            }
        });

        // Buttons
        buttonPanel.add(saveBtn);
        buttonPanel.add(backBtn);
        editQuestion.add(buttonPanel);
        add(editQuestion, BorderLayout.CENTER);

        setVisible(true);
    }

    // Helper method to create a panel with a label and text area
    private JPanel labeledArea(String label, JTextArea area) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setForeground(new Color(255, 230, 80));
        lbl.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    // Custom rounded button class
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
            Color bgColor = new Color(80, 0, 120, 170); // Semi-transparent background
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
