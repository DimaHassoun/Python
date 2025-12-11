package view;
import javax.swing.*;

import controller.GameController;
import controller.QuestionManagerLogic;
import java.awt.*;

public class QuestionView extends JDialog {
	private JLabel idLabel;
	private JLabel difficultyLabel;
	private JLabel questionLabel;
	private JRadioButton answerARadio;
	private JRadioButton answerBRadio;
	private JRadioButton answerCRadio;
	private JRadioButton answerDRadio;
	private ButtonGroup answerGroup;
	private JLabel okLabel;
	private  String Correct_Answer;
	private int gameNum;
	private int row;
	private int col;
	private boolean isLeft;
	private String Quation_ID="ID";
	private String difficulty="Difficulty";
	private boolean questionLoaded = false;//track whether a real question was loaded


	public QuestionView(Frame parent, int gameNum, int row, int col, boolean isLeft) {
		super(parent, "Question", true);
		this.gameNum=gameNum;
		this.row = row;
		this.col = col;
		this.isLeft = isLeft;
		initComponents();
	}

	// === Color Theme ===
	private static final Color MAIN_BG = new Color(183, 119, 183);   // #B777B7
	private static final Color PANEL_BG = new Color(200, 160, 210);
	private static final Color RADIO_PURPLE = new Color(96, 41, 122);
	private static final Color TEXT_DARK = new Color(60, 20, 80);

	private void initComponents() {
		// Set fixed size and prevent resizing
		setSize(750, 430);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent e) {
		        showSimpleTimedMessage("You can't close the window this way!");
		    }
		});

		 Quation_ID="ID";
		 difficulty="Difficulty";
		String QuestionString="Question text";
		String aString="Option A";
		String bString="Option B";
		String cString="Option C";
		String dString="Option D";

		// --- Load random question ---
		String msg = QuestionManagerLogic.fillQuestion();

		if (msg != null) { 
		    // Loading failed — show error and stop creating the dialog
		    JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
		    // Close this dialog safely and stop initialization
		    dispose();
		    return;
		} else {
		    // Successful load: populate fields from QuestionManagerLogic
		    Quation_ID = QuestionManagerLogic.getQuestion_ID(); // ID
		    difficulty = QuestionManagerLogic.getDifficulty(); // Difficulty
		    QuestionString = QuestionManagerLogic.getQuestionString(); // Question text
		    aString = QuestionManagerLogic.getaString(); // Option A
		    bString = QuestionManagerLogic.getbString(); // Option B
		    cString = QuestionManagerLogic.getcString(); // Option C
		    dString = QuestionManagerLogic.getdString(); // Option D
		    Correct_Answer = QuestionManagerLogic.getCorrect_Answer();
		    questionLoaded = true; // mark loaded
		}

		// Main panel with background image
		JPanel mainPanel = new JPanel() {
			Image bg = new ImageIcon(
					getClass().getResource("/resource/background.jpg")
					).getImage();

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
			}
		};
		
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		mainPanel.setBackground(MAIN_BG);

		// Top row panel (ID and Difficulty)
		JPanel topPanel = new JPanel(new GridLayout(1, 2, 30, 0));
		topPanel.setBackground(PANEL_BG);
		topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

		// ID field
		idLabel = new JLabel();
		JPanel idPanel = createLabelPanel("ID:", Quation_ID, idLabel);
		topPanel.add(idPanel);
		// Difficulty field
		difficultyLabel = new JLabel();
		JPanel diffPanel = createLabelPanel("Difficulty Level:", difficulty, difficultyLabel);
		topPanel.add(diffPanel);

		mainPanel.add(topPanel);
		mainPanel.add(Box.createVerticalStrut(15));

		// Question field
		questionLabel = new JLabel();
		JPanel questionPanel = createLabelPanel("Quation:", QuestionString ,questionLabel);
		//questionLabel = (JLabel) ((JPanel) questionPanel.getComponent(2)).getComponent(0);
		mainPanel.add(questionPanel);
		mainPanel.add(Box.createVerticalStrut(10));

		// Answer group for radio buttons
		answerGroup = new ButtonGroup();

		JPanel answerAPanel = createAnswerPanel();
		answerARadio = (JRadioButton) answerAPanel.getComponent(0);
		answerGroup.add(answerARadio);
		mainPanel.add(answerAPanel);
		mainPanel.add(Box.createVerticalStrut(8));

		JPanel answerBPanel = createAnswerPanel( );
		answerBRadio = (JRadioButton) answerBPanel.getComponent(0);
		answerGroup.add(answerBRadio);
		mainPanel.add(answerBPanel);
		mainPanel.add(Box.createVerticalStrut(8));

		JPanel answerCPanel = createAnswerPanel();
		answerCRadio = (JRadioButton) answerCPanel.getComponent(0);
		answerGroup.add(answerCRadio);
		mainPanel.add(answerCPanel);
		mainPanel.add(Box.createVerticalStrut(8));

		JPanel answerDPanel = createAnswerPanel();
		answerDRadio = (JRadioButton) answerDPanel.getComponent(0);
		answerGroup.add(answerDRadio);
		mainPanel.add(answerDPanel);
		//Answer String
		answerARadio.setText("<html>" + aString + "</html>");
		answerBRadio.setText("<html>" + bString + "</html>");
		answerCRadio.setText("<html>" + cString + "</html>");
		answerDRadio.setText("<html>" + dString + "</html>");
		answerGroup.clearSelection();

		mainPanel.add(Box.createVerticalStrut(15));

		// Button panel
		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.setOpaque(false);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 60, 0, 60));

		// OK (RIGHT)
		okLabel = createActionLabel("OK");
		okLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				handleButtonClick("OK");
			}
		});
		buttonPanel.add(okLabel, BorderLayout.EAST);

		mainPanel.add(buttonPanel);

		add(mainPanel);
	}
	
	//handleButtonClick
	private void handleButtonClick(String source) {
		if (!questionLoaded) {
			// if dialog somehow opened without a question, close it.
			JOptionPane.showMessageDialog(this, "No question loaded.", "Error", JOptionPane.ERROR_MESSAGE);
			dispose();
			return;
		}
		String message="";
		if ("OK".equals(source)) {
			// handle OK click
			String selected = getSelectedAnswer();
			if (selected == null) {
				JOptionPane.showMessageDialog(this, "No answer selected!");
				return;
			}
			boolean isCorrect = Correct_Answer.equals(selected);
			message += GameController.applyQuestionScoring(gameNum, this.difficulty, isCorrect, isLeft, row, col);
			
			Icon successIcon = loadAndScaleIcon("/resource/correctAnswer.png", 64, 64);
			Icon failIcon = loadAndScaleIcon("/resource/failedAnswer.png", 64, 64);

			
			if (isCorrect) {
				JOptionPane.showMessageDialog(
					    this,
					    "Selected answer: " + selected + "\nYou are RIGHT!\nAction: " + message,
					    "Success",
					    JOptionPane.INFORMATION_MESSAGE,
					    successIcon
					);
			} else {
				JOptionPane.showMessageDialog(
					    this,
					    "Selected answer: " + selected + "\nYou are WRONG!\nAction: " + message,
					    "Wrong Answer",
					    JOptionPane.WARNING_MESSAGE,
					    failIcon
					);
			}
			dispose();
		}
	}
	
	//scale the icon
	private Icon loadAndScaleIcon(String path, int width, int height) {
	    ImageIcon icon = new ImageIcon(getClass().getResource(path));
	    Image scaledImage = icon.getImage().getScaledInstance(
	            width, height, Image.SCALE_SMOOTH);
	    return new ImageIcon(scaledImage);
	}


	//button style
	private JLabel createActionLabel(String text) {
		JLabel label = new JLabel(text);

		label.setFont(new Font("Verdana", Font.BOLD, 24));
		label.setForeground(new Color(246, 230, 138));
		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label.setOpaque(false);

		return label;
	}

	private JPanel createLabelPanel(String labelText, String placeholderText, JLabel valueLabelRef) {
		JPanel panel = new JPanel(new BorderLayout(10, 0));
		panel.setBackground(PANEL_BG);

		// Left label
		JLabel label = new JLabel(labelText);
		label.setFont(new Font("Arial", Font.BOLD, 14));
		label.setForeground(TEXT_DARK);

		// Right value: use JTextArea for automatic line wrap
		JTextArea valueArea = new JTextArea(placeholderText);
		valueArea.setLineWrap(true);
		valueArea.setWrapStyleWord(true);
		valueArea.setEditable(false);
		valueArea.setFont(new Font("Arial", Font.PLAIN, 14));
		valueArea.setForeground(TEXT_DARK);
		valueArea.setBackground(Color.WHITE);
		valueArea.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(140, 70, 170)),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)
				));

		// Make JTextArea height fit content
		valueArea.setSize(600, Short.MAX_VALUE);
		valueArea.setPreferredSize(new Dimension(600, valueArea.getPreferredSize().height));

		// Assign text to reference for controller use
		valueLabelRef.setText(placeholderText);

		panel.add(label, BorderLayout.WEST);
		panel.add(valueArea, BorderLayout.CENTER);

		return panel;
	}



	private JPanel createAnswerPanel() { 
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setBackground(PANEL_BG);
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

		JRadioButton radioButton = new JRadioButton();
		radioButton.setPreferredSize(new Dimension(30, 35));
		radioButton.setOpaque(false);
		radioButton.setFocusPainted(false);
		radioButton.setBorderPainted(false);
		radioButton.setContentAreaFilled(false);

		panel.add(radioButton);
		panel.add(Box.createHorizontalStrut(5)); // small gap


		return panel;
	}


	// Getters for the controller
	public JLabel getIdLabel() {
		return idLabel;
	}

	public JLabel getDifficultyLabel() {
		return difficultyLabel;
	}

	public JLabel getQuestionLabel() {
		return questionLabel;
	}

	public JRadioButton getAnswerARadio() {
		return answerARadio;
	}

	public JRadioButton getAnswerBRadio() {
		return answerBRadio;
	}

	public JRadioButton getAnswerCRadio() {
		return answerCRadio;
	}

	public JRadioButton getAnswerDRadio() {
		return answerDRadio;
	}

	public String getSelectedAnswer() {
		if (answerARadio.isSelected()) return "A";
		if (answerBRadio.isSelected()) return "B";
		if (answerCRadio.isSelected()) return "C";
		if (answerDRadio.isSelected()) return "D";
		return null;
	}
	private void showSimpleTimedMessage(String message) {
	    JWindow popup = new JWindow(this); // 'this' is your dialog
	    JLabel label = new JLabel(message, SwingConstants.CENTER);
	    label.setOpaque(true);
	    label.setBackground(new Color(255, 255, 210)); // light yellow background
	    label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	    label.setFont(new Font("Arial", Font.PLAIN, 14));
	    label.setPreferredSize(new Dimension(250, 40));
	    popup.add(label);
	    popup.pack();

	    // Position popup at center of this dialog
	    Point loc = getLocationOnScreen();
	    int x = loc.x + (getWidth() - popup.getWidth()) / 2;
	    int y = loc.y + (getHeight() - popup.getHeight()) / 2;
	    popup.setLocation(x, y);

	    popup.setVisible(true);

	    // Dispose popup after 1.5 seconds
	    new javax.swing.Timer(1500, e -> popup.dispose()).start();
	}

}