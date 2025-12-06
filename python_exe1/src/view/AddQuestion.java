package view;

import javax.swing.*;
import controller.QuestionManagerLogic;
import java.awt.*;

public class AddQuestion extends JFrame {

    public JTextField idField;
    public JTextArea questionArea;
    public JTextArea option1Field;
	public JTextArea option2Field;
	public JTextArea option3Field;
	public JTextArea option4Field;
    public JRadioButton diff1;
	private JRadioButton diff2;
	private JRadioButton diff3;
	private JRadioButton diff4;
    private ButtonGroup difficultyGroup;
    public JRadioButton ansA;
	private JRadioButton ansB;
	private JRadioButton ansC;
	private JRadioButton ansD;
    private ButtonGroup answerGroup;
    private WindowSizeManager windowSizeManager;

    public AddQuestion() {
        setTitle("Add Question");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        windowSizeManager = WindowSizeManager.getInstance();
        
        // Apply saved window size BEFORE setting location
        windowSizeManager.applyToFrame(this);
        setLocationRelativeTo(null);

        // Background Panel
        BackgroundPanel addQuestion = new BackgroundPanel("src/resource/background.jpg");
        addQuestion.setLayout(new BoxLayout(addQuestion, BoxLayout.Y_AXIS));
        addQuestion.setBorder(BorderFactory.createEmptyBorder(20, 50, 10, 50));
  
        // ID
        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        idPanel.setOpaque(false);
        JLabel idLabel = new JLabel("Question ID:");
        idLabel.setForeground(new Color(255, 230, 80));
        idPanel.add(idLabel);
        idField = new JTextField(15);
        idField.setBackground(new Color(230, 230, 250));
        idPanel.add(idField);
        addQuestion.add(idPanel);
        addQuestion.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Question text
        JPanel qPanel = new JPanel(new BorderLayout());
        qPanel.setOpaque(false);
        JLabel qLabel = new JLabel("Question Text:", SwingConstants.LEFT);
        qLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        qLabel.setForeground(new Color(255, 230, 80));
        qPanel.add(qLabel, BorderLayout.NORTH);
        questionArea = new JTextArea(3, 30);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setBackground(new Color(230, 230, 250));
        qPanel.add(new JScrollPane(questionArea), BorderLayout.CENTER);
        addQuestion.add(qPanel);
        addQuestion.add(Box.createRigidArea(new Dimension(0, 10)));

        // Difficulty
        JPanel diffPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        diffPanel.setOpaque(false);
        JLabel diffLabel = new JLabel("Difficulty:");
        diffLabel.setForeground(new Color(255, 230, 80));
        diffPanel.add(diffLabel);
        diff1 = new JRadioButton("1 (Easy)");
        diff1.setForeground(new Color(255, 230, 80));
        diff2 = new JRadioButton("2 (Medium)");
        diff2.setForeground(new Color(255, 230, 80));
        diff3 = new JRadioButton("3 (Hard)");
        diff3.setForeground(new Color(255, 230, 80));
        diff4 = new JRadioButton("4 (Expert)");
        diff4.setForeground(new Color(255, 230, 80));

        diff1.setOpaque(false);
        diff2.setOpaque(false);
        diff3.setOpaque(false);
        diff4.setOpaque(false);

        difficultyGroup = new ButtonGroup();
        difficultyGroup.add(diff1);
        difficultyGroup.add(diff2);
        difficultyGroup.add(diff3);
        difficultyGroup.add(diff4);
        diffPanel.add(diff1);
        diffPanel.add(diff2);
        diffPanel.add(diff3);
        diffPanel.add(diff4);
        addQuestion.add(diffPanel);

        // Options
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        optionsPanel.setOpaque(false);
        javax.swing.border.TitledBorder border = BorderFactory.createTitledBorder("Options");
        border.setTitleColor(new Color(255, 230, 80)); 
        border.setTitleFont(new Font("Tahoma", Font.BOLD, 20));
        optionsPanel.setBorder(border);
        optionsPanel.setPreferredSize(new Dimension(450, 400));

        option1Field = new JTextArea();
        option2Field = new JTextArea();
        option3Field = new JTextArea();
        option4Field = new JTextArea();

        option1Field.setLineWrap(true);
        option1Field.setWrapStyleWord(true);
        option1Field.setBackground(new Color(230, 230, 250));
        option2Field.setLineWrap(true);
        option2Field.setWrapStyleWord(true);
        option2Field.setBackground(new Color(230, 230, 250));
        option3Field.setLineWrap(true);
        option3Field.setWrapStyleWord(true); 
        option3Field.setBackground(new Color(230, 230, 250));
        option4Field.setLineWrap(true);
        option4Field.setWrapStyleWord(true);
        option4Field.setBackground(new Color(230, 230, 250));

        optionsPanel.add(labeledArea("A", option1Field));
        optionsPanel.add(labeledArea("B", option2Field));
        optionsPanel.add(labeledArea("C", option3Field));
        optionsPanel.add(labeledArea("D", option4Field));

        addQuestion.add(optionsPanel);

        // correct answer
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BorderLayout());
        JPanel answerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        answerPanel.setOpaque(false);
        JLabel ansLabel = new JLabel("Correct Answer:");
        ansLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        ansLabel.setForeground(new Color(255, 230, 80));
        answerPanel.add(ansLabel);
        ansA = new JRadioButton("A");
        ansA.setForeground(new Color(255, 230, 80));
        ansB = new JRadioButton("B");
        ansB.setForeground(new Color(255, 230, 80));
        ansC = new JRadioButton("C");
        ansC.setForeground(new Color(255, 230, 80));
        ansD = new JRadioButton("D");
        ansD.setForeground(new Color(255, 230, 80));

        ansA.setOpaque(false);
        ansB.setOpaque(false);
        ansC.setOpaque(false);
        ansD.setOpaque(false);

        answerGroup = new ButtonGroup();
        answerGroup.add(ansA);
        answerGroup.add(ansB);
        answerGroup.add(ansC);
        answerGroup.add(ansD);
        answerPanel.add(ansA);
        answerPanel.add(ansB);
        answerPanel.add(ansC);
        answerPanel.add(ansD);
        bottomPanel.add(answerPanel, BorderLayout.NORTH);

         // add question and back buttons
        JButton addButton = new RoundedButton("Add Question");
        addButton.setPreferredSize(new Dimension(180, 45));

        JButton backButton = new RoundedButton("Back");
        backButton.setPreferredSize(new Dimension(180, 45));
        backButton.addActionListener(e -> {
            new QuestionManagerScreen().setVisible(true);
            this.dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0)); 
        buttonPanel.setOpaque(false);
        buttonPanel.add(addButton);
        buttonPanel.add(backButton);

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        addQuestion.add(bottomPanel);

        // Add everything to frame
        add(addQuestion, BorderLayout.CENTER);

        setVisible(true);
        addButton.addActionListener(e -> onAddQuestion());
    }

    private JPanel labeledArea(String label, JTextArea area) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setForeground(new Color(255, 230, 80));
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    // Reading the data entered ,checking and adding it to the system
    public void onAddQuestion() {
        try {
        	 // validate inputs
            validateInputs();

            // After validation, parse ID:
            int id = Integer.parseInt(idField.getText().trim());

            String question = questionArea.getText().trim();
            String option1 = option1Field.getText().trim();
            String option2 = option2Field.getText().trim();
            String option3 = option3Field.getText().trim();
            String option4 = option4Field.getText().trim();

            int difficulty = diff1.isSelected() ? 1 :
                             diff2.isSelected() ? 2 :
                             diff3.isSelected() ? 3 : 4;

            char correct = ansA.isSelected() ? 'A' :
                           ansB.isSelected() ? 'B' :
                           ansC.isSelected() ? 'C' : 'D';

            boolean success = QuestionManagerLogic.addToCSV(
                    id, question, difficulty, option1, option2, option3, option4, correct
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Question added successfully!");
                clearFields();
                    // return to QuestionManagerScreen
                    new QuestionManagerScreen().setVisible(true);
                    this.dispose();  
                }
            else {
                JOptionPane.showMessageDialog(this, "Failed to add question, check the ID", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid number format for ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
         // Show specific message for the missing input
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Validates user input fields.
     * 
     * @throws Exception if one of the fields is invalid
     */
    public void validateInputs() throws Exception {

        // Check Question ID
        String idText = idField.getText().trim();
        if (idText.isEmpty()) {
            throw new Exception("Please enter Question ID.");
        }

        try {
            int id = Integer.parseInt(idText);
            if (id <= 0) {
                throw new Exception("ID must be a positive number.");
            }
        } catch (NumberFormatException e) {
            throw new Exception("Invalid number format for ID.");
        }

        // Check question text
        if (questionArea.getText().trim().isEmpty()) {
            throw new Exception("Please enter the question text.");
        }

        // Options
        if (option1Field.getText().trim().isEmpty()) throw new Exception("Please fill Option 1.");
        if (option2Field.getText().trim().isEmpty()) throw new Exception("Please fill Option 2.");
        if (option3Field.getText().trim().isEmpty()) throw new Exception("Please fill Option 3.");
        if (option4Field.getText().trim().isEmpty()) throw new Exception("Please fill Option 4.");

        // Difficulty
        if (!diff1.isSelected() && !diff2.isSelected() &&
            !diff3.isSelected() && !diff4.isSelected()) {
            throw new Exception("Please select a difficulty level.");
        }

        // Answer selection
        if (!ansA.isSelected() && !ansB.isSelected() &&
            !ansC.isSelected() && !ansD.isSelected()) {
            throw new Exception("Please select the correct answer.");
        }
    }

    
    // Helper method to clear all input fields and selections after successful submission
    private void clearFields() {
        idField.setText("");
        questionArea.setText("");
        difficultyGroup.clearSelection();
        option1Field.setText("");
        option2Field.setText("");
        option3Field.setText("");
        option4Field.setText("");
        answerGroup.clearSelection();
    }

    // Rounded Button
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

