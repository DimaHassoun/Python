package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import Model.Consts;
import controller.QuestionManagerLogic;
import view.EditQuestion.RoundedButton;

public class DeleteQuestions extends JFrame {

	private JTable table;
	private DefaultTableModel model;
	private JButton deleteSelectedBtn;
	private WindowSizeManager windowSizeManager;
	private int flashRow = -1;

	// Constructs the "Delete Questions" window, allowing the user to view, search, and delete questions from a table.
	public DeleteQuestions(DefaultTableModel originalModel, QuestionManagerScreen parent) {
		setTitle("Delete Questions");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(true);

		windowSizeManager = WindowSizeManager.getInstance();

		// Apply saved window size BEFORE setting location
		windowSizeManager.applyToFrame(this);
		setLocationRelativeTo(null);

		// --- Background Panel ---
		BackgroundPanel panel = new BackgroundPanel("/resource/background.jpg");
		panel.setLayout(new BorderLayout(20, 20));
		panel.setBorder(new EmptyBorder(30, 50, 30, 50));
		setContentPane(panel);

		// -------------------------
		// TOP COMPONENTS
		// -------------------------
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);

		// -------------------------
		// SEARCH BAR (Find by ID)
		// -------------------------
		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
		searchPanel.setOpaque(false);

		JLabel searchLabel = new JLabel("you can search by ID:");
		searchLabel.setFont(new Font("Verdana", Font.BOLD, 18));
		searchLabel.setForeground(new Color(246, 230, 138));

		JTextField searchField = new JTextField();
		searchField.setPreferredSize(new Dimension(120, 35));
		searchField.setFont(new Font("Arial", Font.PLAIN, 16));

		RoundedButton searchBtn = new RoundedButton("Search");
		searchBtn.setPreferredSize(new Dimension(150, 45));

		searchPanel.add(searchLabel);
		searchPanel.add(searchField);
		searchPanel.add(searchBtn);

		searchBtn.addActionListener(e -> {
			String searchID = searchField.getText().trim();

			int rowIndex = QuestionManagerLogic.searchQuestionByID(model, searchID);

			if (rowIndex == -1) {
				MessagePlanet.showNonBlockingMessage(
						"No question found with ID: " + searchID 
						,new Color(255, 165, 0), 
						4000 // 4 seconds
						,0 // delay
						);
			} else {
				 // Scroll to row
			    table.scrollRectToVisible(table.getCellRect(rowIndex, 0, true));

			    // Flash highlight
			    flashRow = rowIndex;
			    table.repaint();

			    // Remove highlight after 600 ms
			    new javax.swing.Timer(600, ev -> {
			        flashRow = -1;
			        table.repaint();
			    }).start();
			}
		});

		JLabel instruction = new JLabel("Select the questions you want to delete from the list", SwingConstants.CENTER);
		instruction.setFont(new Font("Verdana", Font.BOLD, 24));
		instruction.setForeground(new Color(246, 230, 138));
		instruction.setBorder(new EmptyBorder(20, 0, 10, 0));

		// A vertical panel combines Back + Instruction + Search
		JPanel topContainer = new JPanel();
		topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
		topContainer.setOpaque(false);

		// Instruction
		JPanel instructionHolder = new JPanel();
		instructionHolder.setOpaque(false);
		instructionHolder.add(instruction);
		topContainer.add(instructionHolder);

		// Search Panel
		JPanel searchHolder = new JPanel();
		searchHolder.setOpaque(false);
		searchHolder.add(searchPanel);
		topContainer.add(searchHolder);
		panel.add(topContainer, BorderLayout.NORTH);

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

		// 5. Column Widths
		table.getColumnModel().getColumn(0).setMaxWidth(80);
		table.getColumnModel().getColumn(0).setPreferredWidth(80);
		table.getColumnModel().getColumn(1).setMaxWidth(100);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		
		// 6. table UI customization & click to select behavior
		table.setRowSelectionAllowed(false);
		table.setCellSelectionEnabled(false);
		table.setColumnSelectionAllowed(false);
		table.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        int row = table.rowAtPoint(e.getPoint());
		        int col = table.columnAtPoint(e.getPoint());

		        if (row == -1) return;

		        // If he clicks directly on the checkbox, let JTable behave normally.
		        if (col == 0) return;

		        // Change the value of the checkbox
		        Boolean current = (Boolean) model.getValueAt(row, 0);
		        model.setValueAt(!current, row, 0);
		    }
		});


		// Scroll Pane
		JScrollPane scroll = new JScrollPane(table);
		scroll.getViewport().setOpaque(true);
		scroll.getViewport().setBackground(new Color(230, 210, 240));
		scroll.setOpaque(false);
		scroll.setBorder(BorderFactory.createLineBorder(new Color(180, 160, 200), 2));

		panel.add(scroll, BorderLayout.CENTER);


		// -------------------------
		// BOTTOM BUTTONS: Back + Delete Selected
		// -------------------------
		deleteSelectedBtn = new RoundedButton("Delete Selected");
		deleteSelectedBtn.setPreferredSize(new Dimension(185, 45));

		RoundedButton backBtn = new RoundedButton("Back");
		backBtn.setPreferredSize(new Dimension(185, 45));
		backBtn.addActionListener(e -> {
			new QuestionManagerScreen().setVisible(true);
			this.dispose();
		});
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
		buttonPanel.setOpaque(false);

		buttonPanel.add(deleteSelectedBtn);
		buttonPanel.add(backBtn);

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
					MessagePlanet.showNonBlockingMessage(
							deleted + " questions deleted successfully." 
							,Color.green, 
							4000 // 4 second
							,0 // delay
							);
					new QuestionManagerScreen();
					dispose();
				} catch (Exception ex) {
					MessagePlanet.showNonBlockingMessage(
							"Error saving CSV: " + ex.getMessage() 
							,Color.red, 
							4000 // 4 second
							,0 // delay
							);
				}
			} else {
				MessagePlanet.showNonBlockingMessage(
						"No questions selected." 
						,new Color(255, 165, 0), 
						4000 // 4 second
						,0 // delay
						);
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
		// Constructs the renderer, initializing the JPanel and JTextArea with proper layout, wrapping, and styling.
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

		/* Returns the component used for drawing the cell. This method
		 * configures the text and adjusts the background and foreground
		 * depending on selection.*/
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus,
				int row, int column) {
			textArea.setText(value == null ? "" : value.toString());
			// Set preferred width for proper wrapping
			int width = table.getColumnModel().getColumn(column).getWidth();
			textArea.setSize(new Dimension(width - 10, 100));
			// Adjust colors depending on selection
			if (row == flashRow) {
			    panel.setBackground(new Color(255, 240, 180));
			    textArea.setForeground(Color.BLACK);
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
