package view;

import javax.swing.*;
import javax.swing.border.Border;

import controller.GameController;
import controller.GameHistoryController;


import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GameBoards extends JFrame implements MusicManager.MusicStateListener {

	private int rows, cols, leftMines, rightMines, score = 0;
	private static int gamenum;
	private JButton[][] leftBoard, rightBoard;
	private JLabel exitLabel, settingsLabel, musicLabel;
	private JLabel leftPlayerLabel, rightPlayerLabel, leftMinesLabel, rightMinesLabel;
	private JLabel gameNumberLabel, scoreLabel, difficultyLabel;
	private RoundedPanel leftBackground, rightBackground, leftPlayerPanel, rightPlayerPanel;
	private RoundedPanel leftBoardPanel, rightBoardPanel;
	private JPanel heartsPanel, leftGridPanel, rightGridPanel;
	private ImageIcon heartIcon;
	private String player1Name, player2Name;
	private MusicManager musicManager;
	private WindowSizeManager windowSizeManager;
	private JPanel leftWrapper,rightWrapper;
	private  boolean warningscore = false;
  
	private boolean player1HintUsed = false; // Track if hint was already used
	private boolean player2HintUsed = false; // Track if hint was already used
	private JButton hintButton;// Reference to hint button for updates

	private boolean isBadSurprise; //to delay Massage of negative Show 
	
	// boards color:
	private static final Color PLAYER1_ACTIVE_COLOR = new Color(180, 160, 220);  
	private static final Color PLAYER2_ACTIVE_COLOR = new Color(140, 80, 100);   
	private static final Color DISABLED_BOARD_COLOR = new Color(105, 105, 105);  

	public int GetGameNum() {
		return gamenum;
	}
	public GameBoards(int rows, int cols, int leftMines, int rightMines, String nameL, String nameR, int gamenum) {
		this.rows = rows;
		this.cols = cols;
		this.leftMines = leftMines;
		this.rightMines = rightMines;
		this.gamenum = gamenum;
		this.player1Name = nameL;
		this.player2Name = nameR;	
		// Get music manager instance
		musicManager = MusicManager.getInstance();	
		// Initialize window size manager
		windowSizeManager = WindowSizeManager.getInstance();
		musicManager.addMusicStateListener(this);
		setTitle("Two Board Game");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);	
		// Apply saved window size
		windowSizeManager.applyToFrame(this);
		// Main background
		JPanel mainBackground = new JPanel(new BorderLayout()) {
			private Image bgImage;
			{ // Load image correctly
				java.net.URL imgURL = getClass().getResource("/resource/background.jpg");
				if (imgURL != null) {
					bgImage = new ImageIcon(imgURL).getImage();
				} else {
					System.err.println("Background image not found!");
				}
			}
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (bgImage != null) {
					g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
				}
			}
		};
		setContentPane(mainBackground);
		// ========================= CENTER PANEL =========================
		// Creates the center panel containing the left and right player boards.
		JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
		centerPanel.setOpaque(false);

		// ========================= LEFT PLAYER =========================
		// Player name panel
		leftPlayerPanel = new RoundedPanel(0, Color.white);
		leftPlayerPanel.setPreferredSize(new Dimension(200, 50));
		leftPlayerPanel.setLayout(new BorderLayout());
		leftPlayerLabel = new JLabel(nameL, SwingConstants.CENTER);
		leftPlayerLabel.setFont(new Font("Arial", Font.BOLD, 18));
		leftPlayerPanel.add(leftPlayerLabel, BorderLayout.CENTER);
		// Wrapper for centering the player panel
		leftWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
		leftWrapper.setOpaque(false);
		leftWrapper.add(leftPlayerPanel);
		// Minesweeper board
		leftBoardPanel = new RoundedPanel(15);
	
		leftBoardPanel.setLayout(new BorderLayout());
		JPanel leftBoardInner = createBoard(true);
		leftBoardPanel.add(leftBoardInner, BorderLayout.CENTER);
		leftGridPanel = leftBoardInner;
		leftGridPanel.setOpaque(false);
		// Remaining mines label
		leftMinesLabel = new JLabel("Remaining Mines: " + leftMines, SwingConstants.CENTER);
		leftMinesLabel.setBorder(BorderFactory.createEmptyBorder(-2, 0, 0, 0));
		leftMinesLabel.setForeground(new Color(255, 215, 0));
		leftMinesLabel.setFont(new Font("Arial", Font.BOLD, 16));
		// Top info layout (  board + mines)
		JPanel leftTopInfo = new JPanel();
		leftTopInfo.setLayout(new BoxLayout(leftTopInfo, BoxLayout.Y_AXIS));
		leftTopInfo.setOpaque(false);
		leftTopInfo.add(leftBoardPanel);
		leftTopInfo.add(Box.createVerticalStrut(5));
		leftTopInfo.add(leftMinesLabel);
		leftMinesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		// Background panel with rounded corners and semi-transparent purple
		leftBackground = new RoundedPanel(15);	
		leftBackground.setLayout(new BorderLayout());
		leftBackground.add(leftTopInfo, BorderLayout.CENTER);
		leftBackground.setBackgroundColor(new Color(0, 0, 0, 100));

		// ========================= RIGHT PLAYER =========================
		// Player name panel
		rightPlayerPanel = new RoundedPanel(0, Color.white);
		rightPlayerPanel.setPreferredSize(new Dimension(200, 50));
		rightPlayerPanel.setLayout(new BorderLayout());
		rightPlayerLabel = new JLabel(nameR, SwingConstants.CENTER);
		rightPlayerLabel.setFont(new Font("Arial", Font.BOLD, 18));
		rightPlayerPanel.add(rightPlayerLabel, BorderLayout.CENTER);		
		// Wrapper for centering the player panel
		rightWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
		rightWrapper.setOpaque(false);
		rightWrapper.add(rightPlayerPanel);		
		// Minesweeper board
		rightBoardPanel = new RoundedPanel(15);
	
		rightBoardPanel.setLayout(new BorderLayout());
		JPanel rightBoardInner = createBoard(false); // false = right player
		rightBoardPanel.add(rightBoardInner, BorderLayout.CENTER);
		rightGridPanel = rightBoardInner;
		rightGridPanel.setOpaque(false);
		// Remaining mines label
		rightMinesLabel = new JLabel("Remaining Mines: " + rightMines, SwingConstants.CENTER);
		rightMinesLabel.setBorder(BorderFactory.createEmptyBorder(-2, 0, 0, 0));
		rightMinesLabel.setForeground(new Color(255, 215, 0));
		rightMinesLabel.setFont(new Font("Arial", Font.BOLD, 16));
		// Top info layout (name + board + mines)
		JPanel rightTopInfo = new JPanel();
		rightTopInfo.setLayout(new BoxLayout(rightTopInfo, BoxLayout.Y_AXIS));
		rightTopInfo.setOpaque(false);
		rightTopInfo.add(rightBoardPanel);
		rightTopInfo.add(Box.createVerticalStrut(5));
		rightTopInfo.add(rightMinesLabel);
		rightMinesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		// Background panel with rounded corners and border
		rightBackground = new RoundedPanel(15);
		rightBackground.setLayout(new BorderLayout());
		rightBackground.add(rightTopInfo, BorderLayout.CENTER);
		rightBackground.setBackgroundColor(new Color(0, 0, 0, 150));

		// ========================= ADD TO CENTER PANEL =========================
		centerPanel.add(leftBackground);
		centerPanel.add(rightBackground);
		// Add center panel to the main frame or parent container
		//add(centerPanel, BorderLayout.CENTER);

		// ========================= SOUTH PANEL =========================
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.setOpaque(false);
		// ========================= HEARTS PANEL =========================
		// Panel displaying the players' shared lives using heart icons.
		heartsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		heartsPanel.setOpaque(false);
		loadHeartImage();// Load heart icon images
		setSharedHearts(GameController.getSharedLivesGame(gamenum));// Load heart icon images
		JPanel heartsWrapper = new JPanel(new BorderLayout());
		heartsWrapper.setOpaque(false);

		JPanel spacer = new JPanel();
		spacer.setOpaque(false);
		spacer.setPreferredSize(new Dimension(100, 1)); 
		heartsWrapper.add(spacer, BorderLayout.WEST);
		heartsWrapper.add(heartsPanel, BorderLayout.CENTER);
		southPanel.add(heartsWrapper, BorderLayout.CENTER);

		// ========================= EXIT BUTTON =========================
		JPanel bottomRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
		bottomRightPanel.setOpaque(false);
		// Create custom EXIT button with rounded design
		JButton exitButton = new JButton("EXIT") {
			private boolean isHovered = false;

			{
				addMouseListener(new java.awt.event.MouseAdapter() {
					@Override
					public void mouseEntered(java.awt.event.MouseEvent e) {
						isHovered = true;
						repaint();
					}

					@Override
					public void mouseExited(java.awt.event.MouseEvent e) {
						isHovered = false;
						repaint();
					}
					@Override
					public void mouseClicked(java.awt.event.MouseEvent e) {
					    handleButtonClick("Exit", -1, -1, false);
					}
				});
			}

			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// Background color - changes on hover
				Color bgColor = isHovered ? 
						new Color(100, 20, 140, 200) : 
							new Color(80, 0, 120, 170);

				g2.setColor(bgColor);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 45, 45);

				// Border - more visible on hover
				Color borderColor = isHovered ? 
						new Color(255, 255, 255, 80) : 
							new Color(255, 255, 255, 40);

				g2.setColor(borderColor);
				g2.setStroke(new BasicStroke(2));
				g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 45, 45);

				g2.dispose();
				super.paintComponent(g);
			}
		};

		// Style the button
		exitButton.setFont(new Font("Verdana", Font.BOLD, 20));
		exitButton.setForeground(new Color(246, 230, 138));
		exitButton.setFocusPainted(false);
		exitButton.setContentAreaFilled(false);
		exitButton.setBorderPainted(false);
		exitButton.setOpaque(false);
		exitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		exitButton.setPreferredSize(new Dimension(90, 40));
		bottomRightPanel.add(exitButton);
		// Add exit panel to the south panel
		southPanel.add(bottomRightPanel, BorderLayout.EAST);
		// Add the south panel to the main frame or parent container
		add(southPanel, BorderLayout.SOUTH);

		//======================Create HINT button ==================
		hintButton = new JButton("💡 Hint") {
		    private boolean isHovered = false;

		    {
		        addMouseListener(new java.awt.event.MouseAdapter() {
		            @Override
		            public void mouseEntered(java.awt.event.MouseEvent e) {
		                int currentPlayer = GameController.GameGetCurrentPlayer(gamenum);
		                boolean currentPlayerUsed = (currentPlayer == 1) ? player1HintUsed : player2HintUsed;
		                boolean bothUsed = player1HintUsed && player2HintUsed;
		                
		                if (!bothUsed && !currentPlayerUsed) {
		                    isHovered = true;
		                    repaint();
		                }
		            }

		            @Override
		            public void mouseExited(java.awt.event.MouseEvent e) {
		                isHovered = false;
		                repaint();
		            }

		            @Override
		            public void mouseClicked(java.awt.event.MouseEvent e) {
		                useHint();
		            }
		        });
		    }
		    
		    @Override
		    protected void paintComponent(Graphics g) {
		        Graphics2D g2 = (Graphics2D) g.create();
		        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		        int currentPlayer = GameController.GameGetCurrentPlayer(gamenum);
		        boolean currentPlayerUsed = (currentPlayer == 1) ? player1HintUsed : player2HintUsed;
		        boolean bothUsed = player1HintUsed && player2HintUsed;

		        // Background color - changes based on state
		        Color bgColor;
		        if (bothUsed) {
		            bgColor = new Color(80, 80, 80, 100); // Gray when both used
		        } else if (currentPlayerUsed) {
		            bgColor = new Color(120, 100, 80, 150); // Darker when current player used
		        } else if (isHovered) {
		            bgColor = new Color(100, 20, 140, 200); // Bright purple on hover
		        } else {
		            bgColor = new Color(80, 0, 120, 170); // Normal purple
		        }

		        g2.setColor(bgColor);
		        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 45, 45);

		        // Border
		        Color borderColor;
		        if (bothUsed) {
		            borderColor = new Color(100, 100, 100, 60);
		        } else if (currentPlayerUsed) {
		            borderColor = new Color(150, 130, 100, 60);
		        } else if (isHovered) {
		            borderColor = new Color(255, 255, 255, 80);
		        } else {
		            borderColor = new Color(255, 255, 255, 40);
		        }

		        g2.setColor(borderColor);
		        g2.setStroke(new BasicStroke(2));
		        g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 45, 45);

		        g2.dispose();
		        super.paintComponent(g);
		    }
		};
		
		// Style the hint button
		hintButton.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
		hintButton.setForeground(new Color(246, 230, 138));
		hintButton.setFocusPainted(false);
		hintButton.setContentAreaFilled(false);
		hintButton.setBorderPainted(false);
		hintButton.setOpaque(false);
		hintButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		hintButton.setPreferredSize(new Dimension(120, 40));
		//bottomRightPanel.add(hintButton);

	
		// ========================= TOP PANEL - SEPARATED STRUCTURE =========================
		settingsLabel = new JLabel("⚙");
		settingsLabel.setFont(new Font("Dialog", Font.BOLD, 35));
		settingsLabel.setForeground(new Color(246, 230, 138));
		settingsLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));// Change cursor to hand when hovering
		settingsLabel.setPreferredSize(new Dimension(40, 40));
		settingsLabel.setMinimumSize(new Dimension(40, 40));
		settingsLabel.setMaximumSize(new Dimension(40, 40));
		settingsLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				showSettingsMenu();// Call the method to display the settings menu when clicked
			}
		});

		musicLabel = new JLabel(musicManager.isPlaying() ? "♪" : "🔇");
		musicLabel.setFont(new Font("Dialog", Font.BOLD, 35));
		musicLabel.setForeground(musicManager.isPlaying() ? new Color(246, 230, 138) : new Color(180, 180, 180));
		musicLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		musicLabel.setPreferredSize(new Dimension(40, 40)); 
		musicLabel.setMinimumSize(new Dimension(40, 40));
		musicLabel.setMaximumSize(new Dimension(40, 40));
		musicLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				toggleMusic();// Toggle music playback when the label is clicked
			}
		});

		// Create main top panel
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);
		// ========== LEFT SIDE - Icons Panel (Settings & Music) ==========
		JPanel topLeftIcons = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
		topLeftIcons.setOpaque(false);
		topLeftIcons.add(settingsLabel);
		topLeftIcons.add(musicLabel);
	
		// Labels for game information
		gameNumberLabel = new JLabel("Game No. " + gamenum, SwingConstants.CENTER);
		scoreLabel = new JLabel("Score: " + score, SwingConstants.CENTER);
		difficultyLabel = new JLabel("Difficulty Level: " + GameController.GameGetDifficulty(gamenum), SwingConstants.CENTER);
		
		// Set text color to gold
		gameNumberLabel.setForeground(new Color(255, 215, 0));
		scoreLabel.setForeground(new Color(255, 215, 0));
		difficultyLabel.setForeground(new Color(255, 215, 0));
		
		// Set font for all top labels
		Font topFont = new Font("Arial", Font.BOLD, 16);
		gameNumberLabel.setFont(topFont);
		scoreLabel.setFont(topFont);
		difficultyLabel.setFont(topFont);
		
		// ========== RIGHT SIDE - Hint Button ==========
		JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
		topRightPanel.setOpaque(false);
		topRightPanel.add(hintButton);

		// ========== CENTER - Game Info Panel (separated from players) ==========
		JPanel gameCenterInfoPanel = new JPanel(new GridLayout(3, 1, 2, 2));
		gameCenterInfoPanel.setOpaque(false);
		gameCenterInfoPanel.add(difficultyLabel);
		gameCenterInfoPanel.add(gameNumberLabel);
		gameCenterInfoPanel.add(scoreLabel);

		// ========== ASSEMBLE TOP PANEL ==========
		topPanel.add(topLeftIcons, BorderLayout.WEST);
		topPanel.add(gameCenterInfoPanel, BorderLayout.CENTER);
		topPanel.add(topRightPanel, BorderLayout.EAST);
		
		JPanel playersNamesPanel = new JPanel(new GridLayout(1, 2, 20, 0));
		playersNamesPanel.setOpaque(false);
		playersNamesPanel.setMaximumSize(
		    new Dimension(Integer.MAX_VALUE, 50)
		);

		playersNamesPanel.add(leftWrapper);
		playersNamesPanel.add(rightWrapper);
		JPanel gamePanel = new JPanel();
		gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));
		gamePanel.setOpaque(false);

		gamePanel.add(playersNamesPanel);
		gamePanel.add(Box.createVerticalStrut(5));
		gamePanel.add(centerPanel); // הלוחות עצמם

		add(gamePanel, BorderLayout.CENTER);


		add(topPanel, BorderLayout.NORTH);

		// Highlight the current player
		highlightCurrentPlayer(GameController.GameGetCurrentPlayer(gamenum));

		// Apply window size again to ensure it's set after all components are added
		windowSizeManager.applyToFrame(this);
		setLocationRelativeTo(null);
	}

	//================== show count mines on row / col============
	private void useHint() {
	    int currentPlayer = GameController.GameGetCurrentPlayer(gamenum);
	    boolean isPlayer1 = (currentPlayer == 1);
	    
	    // Check if both players already used their hints
	    if (player1HintUsed && player2HintUsed) {
	    	MessagePlanet.showNonBlockingMessage(
	    		    "Both players have already used their hints!",    // message
	    		    new Color(255, 165, 0),                              
	    		    1500, // duration in milliseconds (e.g., 3 seconds)
	    		    0  // no delay before showing
	    		);
	        return;
	    }
	    
	    // Check if the current player already used their hint
	    boolean currentPlayerUsed = isPlayer1 ? player1HintUsed : player2HintUsed;
	    
	    if (currentPlayerUsed) {
	        String playerName = isPlayer1 ? player1Name : player2Name;
	        MessagePlanet.showNonBlockingMessage(
	        		playerName + " has already used their hint for this game!",    // message
	    		    Color.red,                              
	    		    1800, // duration in milliseconds (e.g., 3 seconds)
	    		    0  // no delay before showing
	    		);
	        return;
	    }

	    // Mark hint as used for current player
	    if (isPlayer1) {
	        player1HintUsed = true;
	        System.out.println("DEBUG: Player 1 (" + player1Name + ") used hint");
	    } else {
	        player2HintUsed = true;
	        System.out.println("DEBUG: Player 2 (" + player2Name + ") used hint");
	    }

	    // Update button appearance if both players have now used their hints
	    if (player1HintUsed && player2HintUsed) {
	        hintButton.setText("USED");
	        hintButton.setEnabled(false);
	        System.out.println("DEBUG: Both players used hints - button disabled");
	    }
	    
	    // Force button repaint
	    hintButton.repaint();

	    // Show the hint
	    boolean isLeft = isPlayer1;
	    showPartialVision(isLeft, gamenum);
	}

	private void showPartialVision(boolean isLeft, int gameNum) {
	    int currentPlayer = GameController.GameGetCurrentPlayer(gamenum);
	    String playerName = (currentPlayer == 1) ? player1Name : player2Name;
	    
	    int size = GameController.getBoardSize(gamenum, isLeft);
	    boolean showRow = Math.random() < 0.5;

	    String message;
	    int selectedIndex = -1;
	    
	    if (showRow) {
	        int row = (int)(Math.random() * size);
	        int mines = GameController.countMinesInRow(gamenum, isLeft, row);
	        boolean  isRowFullyRevealed= GameController.isRowFullyRevealed(gameNum, isLeft, row);
	        int attempts = 0;
	        while((isRowFullyRevealed || mines == 0 )&& attempts < size) {
	        // Try  to find a row with mines and unRevealed 
	   	        row = (int)(Math.random() * size);
	       	    mines = GameController.countMinesInRow(gamenum, isLeft, row);
	       	    isRowFullyRevealed= GameController.isRowFullyRevealed(gameNum, isLeft, row);
	            attempts++;
	        }
	        selectedIndex = row;
	        if (mines > 0) {
	            message = playerName + "'s hint:\nRow " + (row + 1) + " contains " + mines + " mine" + (mines > 1 ? "s" : "");
	            highlightRowOrColumn(isLeft, row, -1, true);
	        } else {
	            message = playerName + "'s hint:\nNo rows with mines found. Good luck!";
	        }
	    } else {
	        int col = (int)(Math.random() * size);
	        int mines = GameController.countMinesInColumn(gamenum, isLeft, col);
	        boolean  isColumnFullyRevealed= GameController.isColumnFullyRevealed(gameNum, isLeft, col);
	        int attempts = 0;
	        // Try  to find a column with mines
	        while ((isColumnFullyRevealed || mines == 0 )&& attempts < size) {
	            col = (int)(Math.random() * size);
	            mines = GameController.countMinesInColumn(gamenum, isLeft, col);
	            isColumnFullyRevealed= GameController.isColumnFullyRevealed(gameNum, isLeft, col);
	            attempts++;
	        }
	        selectedIndex = col;
	        if (mines > 0) {
	            message = playerName + "'s hint:\nColumn " + (col + 1) + " contains " + mines + " mine" + (mines > 1 ? "s" : "");
	            highlightRowOrColumn(isLeft, -1, col, false);
	        } else {
	            message = playerName + "'s hint:\nNo columns with mines found. Good luck!";
	        }
	    }
	    MessagePlanet.showBlockingMessageDialog(
	    	    this,                  // parent component
	    	    message,               // your message string
	    	    Color.green, 
	    	    "Hint for " + playerName, // dialog title
	    	    "OK"                   // button text
	    	);
	    if (selectedIndex != -1) {
	        removeHighlight(isLeft);
	    }
	    System.out.println("DEBUG: Showed hint to " + playerName);
	}


	//Highlights an entire row or column
	private void highlightRowOrColumn(boolean isLeft, int row, int col, boolean isRow) {
	    JButton[][] board = isLeft ? leftBoard : rightBoard;
	    Color highlightColor = new Color(255, 255, 0, 150); // צהוב שקוף
	    
	    if (isRow && row >= 0) {
	    	// Highlight the entire row
	        for (int c = 0; c < board[row].length; c++) {
	            if (!GameController.IsCellRevealed(gamenum, isLeft, row, c)) {
	                JButton btn = board[row][c];
	                // Save the original background color
	                btn.putClientProperty("originalColor", btn.getBackground());
	                // Apply highlight effect
	                btn.setBackground(highlightColor);
	                btn.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2, true));
	            }
	        }
	    } else if (!isRow && col >= 0) {
	        // Highlight the entire column
	        for (int r = 0; r < board.length; r++) {
	            if (!GameController.IsCellRevealed(gamenum, isLeft, r, col)) {
	                JButton btn = board[r][col];
	                // Save the original background color
	                btn.putClientProperty("originalColor", btn.getBackground());
	                // Apply highlight effect
	                btn.setBackground(highlightColor);
	                btn.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2, true));
	            }
	        }
	    }
	}

	//Removes the highlight effect and restores the original appearance of all unrevealed cells on the selected board.
	private void removeHighlight(boolean isLeft) {
    JButton[][] board = isLeft ? leftBoard : rightBoard;
    int currentPlayer = GameController.GameGetCurrentPlayer(gamenum);
    boolean isBoardActive = (isLeft && currentPlayer == 1) || (!isLeft && currentPlayer == 2);
    
    for (int r = 0; r < board.length; r++) {
        for (int c = 0; c < board[r].length; c++) {
            if (!GameController.IsCellRevealed(gamenum, isLeft, r, c)) {
                JButton btn = board[r][c];
                // Restore the original background color
                Color originalColor = (Color) btn.getClientProperty("originalColor");
                if (originalColor != null) {
                    btn.setBackground(originalColor);
                    btn.putClientProperty("originalColor", null);
                } else {
                	// If no saved color exists, restore based on board state
                    if (isBoardActive) {
                        btn.setBackground(isLeft ? PLAYER1_ACTIVE_COLOR : PLAYER2_ACTIVE_COLOR);
                    } else {
                        btn.setBackground(DISABLED_BOARD_COLOR);
                    }
                }
             // Remove the yellow highlight border
                btn.setBorder(null);
            }
        }
    }
}

	//==========================================================================
	// Show settings menu with advanced options
	private void showSettingsMenu() {
		JPopupMenu settingsMenu = new JPopupMenu();
		settingsMenu.setBackground(new Color(60, 0, 90, 230));
		settingsMenu.setBorder(BorderFactory.createLineBorder(new Color(246, 230, 138), 2));

		// Volume Control
		JMenuItem volumeItem = new JMenuItem("🔊 Volume Control");
		volumeItem.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
		volumeItem.setForeground(new Color(246, 230, 138));
		volumeItem.setBackground(new Color(60, 0, 90));
		volumeItem.addActionListener(e -> 
        MessagePlanet.showVolumeControlDialog(
                this,                     // parent component for dialog centering
                musicManager,             //  music manager instance
                new Color(255, 180, 255)  //  border color
            )
        );
		settingsMenu.add(volumeItem);
		settingsMenu.addSeparator();

		// Restart Game
		JMenuItem restartItem = new JMenuItem("🔄‌ Restart Game");
		restartItem.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
		restartItem.setForeground(new Color(246, 230, 138));
		restartItem.setBackground(new Color(60, 0, 90));
		restartItem.addActionListener(e -> restartGame());
		settingsMenu.add(restartItem);
		settingsMenu.show(settingsLabel, 0, settingsLabel.getHeight());
	}

	private void restartGame() {
		// Show a confirmation dialog to the user
		boolean confirmed = MessagePlanet.showConfirmDialog(
	            this,
	            "Are you sure you want to restart the game?\nAll progress will be lost.",
	            new Color(255, 165, 0),
	            "Restart Game","Ok","No"
	    );
		// If the user clicks "No"
		if (!confirmed) {
		        return;
		    }
		// If the user clicks "Yes"
			try {
				// Call the controller method which creates and returns a new GameBoards instance
				GameBoards newGameBoard = GameController.createNewGame(player1Name, player2Name, GameController.GameGetDifficulty(gamenum));		            
				// Show the new game window
				newGameBoard.setVisible(true);		            
				// Close current game window
				GameBoards.this.dispose();		            
			} catch (IllegalArgumentException ex) {
				// Show error if invalid input or difficulty
				 MessagePlanet.showBlockingMessageDialog(
			                this,
			                ex.getMessage(),
			                Color.RED,
			                "Error","Ok"
			        );
			}
		
	}
	private void toggleMusic() {
		musicManager.toggleMusic();// Toggle the music playback (play or pause)
	}
	// Updates the music icon based on the current playback state.
	private void updateMusicIcon() {
		if (musicLabel == null) return;
		if (musicManager.isPlaying()) {
			musicLabel.setText("♪");
			musicLabel.setForeground(new Color(246, 230, 138));
		} else {
			musicLabel.setText("🔇");
			musicLabel.setForeground(new Color(180, 180, 180));
		}
	}

	// Creates a game board represented as a JPanel containing a grid of buttons.
	private JPanel createBoard(boolean isLeft) {
	    JPanel boardPanel = new JPanel(new GridLayout(rows, cols, 2, 2));
	    JButton[][] board = new JButton[rows][cols];
	    
	    // Calculate fixed cell size based on difficulty
	    final int CELL_SIZE = calculateCellSize();
	    
	    for (int r = 0; r < rows; r++) {
	        for (int c = 0; c < cols; c++) {
	            JButton cell = new JButton() {
	                private final Dimension fixedSize = new Dimension(CELL_SIZE, CELL_SIZE);
	                
	                @Override
	                public Dimension getPreferredSize() {
	                    return fixedSize;
	                }
	                
	                @Override
	                public Dimension getMinimumSize() {
	                    return fixedSize;
	                }
	                
	                @Override
	                public Dimension getMaximumSize() {
	                    return fixedSize;
	                }
	                
	                @Override
	                protected void paintComponent(Graphics g) {
	                    Graphics2D g2 = (Graphics2D) g.create();
	                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	                    g2.setColor(getBackground());
	                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
	                    super.paintComponent(g2);
	                    g2.dispose();
	                }

	                @Override
	                protected void paintBorder(Graphics g) {
	                    Graphics2D g2 = (Graphics2D) g.create();
	                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	                    if (getBorder() != null) {
	                        g2.setColor(new Color(50, 40, 60));
	                        g2.setStroke(new BasicStroke(0));
	                        g2.drawRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
	                    }
	                    g2.dispose();
	                }
	            };

	            cell.setBackground(isLeft ? PLAYER1_ACTIVE_COLOR : PLAYER2_ACTIVE_COLOR);
	            cell.setOpaque(false);
	            cell.setContentAreaFilled(false);
	            cell.setBorderPainted(true);
	            
	            final int row = r, col = c;
	            cell.addMouseListener(new java.awt.event.MouseAdapter() {
	                @Override
	                public void mouseClicked(java.awt.event.MouseEvent e) {
	                    if (SwingUtilities.isLeftMouseButton(e))
	                        handleButtonClick(isLeft ? "Left" : "Right", row, col, false);
	                    else if (SwingUtilities.isRightMouseButton(e))
	                        handleButtonClick(isLeft ? "Left" : "Right", row, col, true);
	                }
	            });

	            board[r][c] = cell;
	            boardPanel.add(cell);
	        }
	    }
	    if (isLeft) leftBoard = board;
	    else rightBoard = board;
	    return boardPanel;
	}
	/**
	 * Calculate appropriate cell size based on board dimensions
	 * Ensures cells fit properly in the window while maintaining square shape
	 */
	private int calculateCellSize() {
	    switch (rows) {
	        case 9:  // Easy
	            return 40;
	        case 13: // Medium
	            return 32;
	        case 16: // Hard
	            return 26;
	        default:
	            return 30;
	    }
	}
	//Handles actions triggered by clicking a cell button on either the left or right board.
	private void handleButtonClick(String source, int row, int col, boolean isFlag) {
		// Handle the "Exit" action: prompt user to confirm exit and return to the new game screen
		if (source.equals("Exit")) {
		    boolean confirmed = MessagePlanet.showConfirmDialog(
		        this,
		        "Are you sure you want to exit? The game progress will NOT be saved.",
		        new Color(255, 165, 0),
		        "Confirm Exit","Yes","No"
		    );
		    if (confirmed) {
		        new NewGameScreen().setVisible(true);
		        GameBoards.this.dispose();
		    }
		    return;
		}
		// If the game is already over, ignore further clicks
		if (GameController.GameIsGameOver(gamenum)) return;
		// Check if the current player is allowed to click on this board
		int currentPlayer = GameController.GameGetCurrentPlayer(gamenum);
		if ((currentPlayer == 1 && source.equals("Right")) || (currentPlayer == 2 && source.equals("Left"))) {
			MessagePlanet.showNonBlockingMessage(
				    "It's not your turn! \nWait",        // message
				    Color.ORANGE,                 // border color to indicate warning (you can choose)
				    1500,                        // duration in milliseconds (e.g., 2 seconds)
				    0                            // no delay before showing
				);
			return;
		}
		boolean isLeft = source.equals("Left");	
		JButton[][] buttons = source.equals("Left") ? leftBoard : rightBoard;
		boolean shouldSwitchTurn = true;
		String cellType = GameController.GetCellType(gamenum, isLeft, row, col);
		Boolean IsCellUsed=GameController.iscellUsed(gamenum, isLeft, row, col);
		// Ignore clicks on already revealed cells unless it's a SURPRISE or QUESTION cell
		if (GameController.IsCellRevealed(gamenum, isLeft, row, col)&&!cellType.equals("SURPRISE")&&!cellType.equals("QUESTION")){return;}
		// Handle clicks on revealed special cells
		if (GameController.IsCellRevealed(gamenum, isLeft, row, col)) {
			if (cellType.equals("SURPRISE")) {
				shouldSwitchTurn = handleActionOfSurprise(row, col, isLeft, buttons);
			}
			else {
				if(cellType.equals("QUESTION"))
				{
					handleActionOfQuestion(gamenum,row, col, isLeft, buttons);
					shouldSwitchTurn=CheckCanSwitch( row,  col,  isLeft,  buttons) ;
				}
			}
		}
		// Handle flag placement or removal
		else if (isFlag) {
		    handleFlagAction(isLeft, row, col, buttons);
		    shouldSwitchTurn = false; 
		}
		//if cell is flagged: u can only "Unflag"
		if (GameController.IsCellFlagged(gamenum, isLeft, row, col)&&!isFlag) {
			return;
		}
		// Reveal the cell if it's not a flag action
		if (!isFlag) {
			handleRevealAction(row, col, isLeft, buttons);
		}
		// Update shared game state in the UI
		updateScore(GameController.getSharedPoints(gamenum));
		setSharedHearts(GameController.getSharedLivesGame(gamenum));
		updateLeftMines(GameController.getRemainingMines(gamenum, true));
		updateRightMines(GameController.getRemainingMines(gamenum, false));		
		// Check for negative score warning
		int delayMassage=0;
		if(isBadSurprise==true) {
			delayMassage =500;
		}
		int currentScore = GameController.getSharedPoints(gamenum);
		String diffG= GameController.GameGetDifficulty(gamenum);
		if (currentScore < 0){
			if ( diffG== "EASY" && currentScore > -30 && warningscore == false) {
				showNonBlockingMessage(
						"Negative Score Warning:" +
								"\n Warning! Your score is negative: " + currentScore + 
								"\nIf you reach -30 points, the game will end!" 
								,new Color(255, 165, 0), // Orange
			            4000 // 4 seconds
			            ,delayMassage /** 
			             * Show message after a 500ms delay if triggered after a bad surprise,
			             * allowing time for the bad surprise message to be displayed first.
			             */
			            );
				warningscore = true;
			}
			//Handle defeat condition - score reaches -30
			if ( diffG== "EASY" && currentScore <= -30 ) {
				String player1 = GameController.getGame(gamenum).getPlayer1Name();
				String player2 = GameController.getGame(gamenum).getPlayer2Name();

				DefeatScreenByScore defeatScreen = new DefeatScreenByScore(currentScore, this, player1, player2);
				// Reveal all cells before showing defeat screen
				revealAllCells();						
				defeatScreen.setVisible(true);						
				// Save game history
				GameHistoryController.createHistoryEntry(
						GameController.getGame(gamenum),
						"Defeat"
						);
				GameController.GameFinish(gamenum);
				return;
			}
			if ( diffG== "MEDIUM" && currentScore > -40 && warningscore == false) {
				showNonBlockingMessage(
						"Negative Score Warning:" +
								"\n Warning! Your score is negative: " + currentScore + 
								"\nIf you reach -40 points, the game will end!" 
								,new Color(255, 165, 0), // Orange
			            4000 // 4 seconds
			            ,delayMassage /** 
			             * Show message after a 500ms delay if triggered after a bad surprise,
			             * allowing time for the bad surprise message to be displayed first.
			             */
			            );
				warningscore = true;
			}
			//Handle defeat condition - score reaches -40
			if ( diffG== "MEDIUM" && currentScore <= -40 ) {
				String player1 = GameController.getGame(gamenum).getPlayer1Name();
				String player2 = GameController.getGame(gamenum).getPlayer2Name();
				DefeatScreenByScore defeatScreen = new DefeatScreenByScore(currentScore, this, player1, player2);
				// Reveal all cells before showing defeat screen
				revealAllCells();
				defeatScreen.setVisible(true);

				// Save game history
				GameHistoryController.createHistoryEntry(
						GameController.getGame(gamenum),
						"Defeat"
						);
				GameController.GameFinish(gamenum);
				return;
			}
			if ( diffG== "HARD" && currentScore > -60 && warningscore == false) {
				showNonBlockingMessage(
						"Negative Score Warning" +
								"\n Warning! Your score is negative: " + currentScore + 
								"\nIf you reach -60 points, the game will end!" 
								,new Color(255, 165, 0), // Orange
								4000 // 4 seconds
								,delayMassage /** 
			             * Show message after a 500ms delay if triggered after a bad surprise,
			             * allowing time for the bad surprise message to be displayed first.
			             */
			            );
				warningscore = true;
			}
			//Handle defeat condition - score reaches -60
			if ( diffG== "MEDIUM" && currentScore <= -60) {
				String player1 = GameController.getGame(gamenum).getPlayer1Name();
				String player2 = GameController.getGame(gamenum).getPlayer2Name();						
				DefeatScreenByScore defeatScreen = new DefeatScreenByScore(currentScore, this, player1, player2);
				// Reveal all cells before showing defeat screen
				revealAllCells();						
				defeatScreen.setVisible(true);						
				// Save game history
				GameHistoryController.createHistoryEntry(
						GameController.getGame(gamenum),
						"Defeat"
						);
				GameController.GameFinish(gamenum);
				return;
			}
		}

		// Handle defeat condition
		if (GameController.getSharedLivesGame(gamenum) <= 0) {
			String player1 = GameController.getGame(gamenum).getPlayer1Name();
			String player2 = GameController.getGame(gamenum).getPlayer2Name();
			DefeatScreenByHearts defeatScreen = new DefeatScreenByHearts(GameController.getSharedPoints(gamenum) , this, player1 ,player2);
			// Reveal all cells before showing defeat screen
			revealAllCells();		    
			defeatScreen.setVisible(true);			
			// Save game history
			GameHistoryController.createHistoryEntry(
					GameController.getGame(gamenum),
					"Defeat"
					);
			GameController.GameFinish(gamenum);
			return;
		}
		// Handle victory condition
		if (GameController.IsGameVictory(gamenum)) {
			if (GameController.IsGameVictory(gamenum)) {
			    // Calculate bonus immediately
			    int bonusPoints = convertRemainingLivesToPoints();
			    updateScore(GameController.getSharedPoints(gamenum));
			    int finalScore = GameController.getSharedPoints(gamenum);
			    
			    // Show victory screen IMMEDIATELY
			    String player1 = GameController.getGame(gamenum).getPlayer1Name();
			    String player2 = GameController.getGame(gamenum).getPlayer2Name();
			    VictoryScreen victoryScreen = new VictoryScreen(finalScore, this, player1, player2);
			    victoryScreen.setVisible(true);
			    
			    // Save history FIRST
			    GameHistoryController.createHistoryEntry(
			        GameController.getGame(gamenum),
			        "Victory"
			    );
			    GameController.GameFinish(gamenum);
			    
			    // Reveal cells in background AFTER screen is shown
			    SwingUtilities.invokeLater(() -> {
			        revealAllCells();
			        
			        // Show bonus message AFTER victory screen
			        if (bonusPoints > 0) {
			            MessagePlanet.showBlockingMessageDialog(
			                this,
			                "Bonus! +" + bonusPoints + " points for " +
			                (bonusPoints / GameController.GetGameSurpriseQuestionCoust(gamenum)) +
			                " remaining lives!",
			                Color.green,
			                "Victory Bonus",
			                "OK"
			            );
			        }
			    });
			    
			    return;
			}
		}
		// Switch the turn if applicable
		if (shouldSwitchTurn) {
			int newPlayer = GameController.switchTurn(gamenum);
			highlightCurrentPlayer(newPlayer);
		}
	}
	//--------------------------------------Flag-----------------------------------------------
	// Handles placing or removing a flag on a cell in the game board.
	private void handleFlagAction(Boolean isLeft, int row, int col, JButton[][] buttons) {
		/**Get the Flag-State Of Cell From Control: 
		 * Flagged -> un-Flag Cell (DESIGN DECISION),
		 *  or Mine -> show cell 
		 *  or Not Mine -> flag cell
		 */
		GameController.FlagResult result = GameController.handleFlag(gamenum, isLeft, row, col);
		switch (result) {
		case UNFLAGGED:
			//Revealed Cell
			showCell(buttons[row][col], row, col, isLeft);
			break;

		case FLAGGED_MINE:
			showTimedMessage(
					"MINE: +1 points",
					new Color(0, 200, 0),
					buttons[row][col]
					);
			showCell(buttons[row][col], row, col, isLeft);
			break;

		case FLAGGED_NOT_MINE:
			showTimedMessage(
					"Not MINE: -3 Points",
					Color.red,
					buttons[row][col]
					);
			buttons[row][col].setIcon(new ImageIcon(
					renderEmojiToImage(
							"🚩",
							buttons[row][col].getWidth(),
							buttons[row][col].getHeight()
							)
					));
			break;
		}
		// Update the screen
		updateScore(GameController.getSharedPoints(gamenum));
	}
	//--------------------------------------Surprise-----------------------------------------------
	// Handles the action triggered when a player interacts with a "SURPRISE" cell.
	private boolean handleActionOfSurprise(int row, int col, Boolean isLeft, JButton[][] buttons) {
		String checkSurprise = GameController.IsSurprise(gamenum, isLeft, row, col);
		if (checkSurprise.equals("SurpriseCell")) {
			// Check if player has enough points FIRST
			//Not enough cash → stop here
			int getIfPlayerHasEnoughPoints = GameController.CheckActivateCost(gamenum);
			if(getIfPlayerHasEnoughPoints < 0)
			{
				MessagePlanet.showBlockingMessageDialog(
				        this,
				        "You can't open the surprise!!\nYou need more " + Math.abs(getIfPlayerHasEnoughPoints) + " points to unlock the surprise.",
				        Color.ORANGE,   // warning color border
				        "Not Enough points",
				        "OK"
				    );
				return false;
			}
			String getSurpriseCostActivate = GameController.getSurpriseCostActivate(gamenum, isLeft, row, col);
			boolean confirmed = MessagePlanet.showConfirmDialog(
				    this,
				    getSurpriseCostActivate + " \nDo you want to continue?",
				    new Color(204, 204, 0), //  color border
				    "Surprise Cost","Ok","Cancel"
				);

				if (!confirmed) {
				    return false; // Player declined to activate the surprise
				}
			else {  //if player Agreed and enough cash → do active
				String result = GameController.ActivateSurpriseCell(gamenum, isLeft, row, col);
				String[] parts = result.split(":");
				String type = parts[0]; // GOOD, BAD
				int points = Integer.parseInt(parts[1]);
				String displayEmoji = GameController.getCellDisplay(gamenum, isLeft, row, col);
				switch(type) {
				case "GOOD":
					showTimedMessage("Surprise Good! +Life & +" + points + " Points", new Color(0,200,0), buttons[row][col]);
					updateScore(GameController.getSharedPoints(gamenum)); // update the score on this screen
					setSharedHearts(GameController.getSharedLivesGame(gamenum)); // update the hearts on this screen
					buttons[row][col].setIcon(new ImageIcon(renderEmojiToImage(displayEmoji, buttons[row][col].getWidth(), buttons[row][col].getHeight())));
					isBadSurprise=false;
					return false;
				case "BAD":
					showTimedMessage("Surprise Bad! -Life & -" + points + " Points", Color.red, buttons[row][col]);
					updateScore(GameController.getSharedPoints(gamenum));// update the score on this screen
					setSharedHearts(GameController.getSharedLivesGame(gamenum));// update the hearts on this screen
					buttons[row][col].setIcon(new ImageIcon(renderEmojiToImage(displayEmoji, buttons[row][col].getWidth(), buttons[row][col].getHeight())));
					isBadSurprise=true;
					return false;
				}
			}

		}
		if (checkSurprise.equals("NOT_SURPRISE")||checkSurprise.equals("NOT_REVEALED") ) {
			System.err.println("Unexpected surprise result " );
			return false; 
		}

		if (checkSurprise.equals("ALREADY_USED")) {
			showTimedMessage("Already used this turn!", Color.gray, buttons[row][col]);
			return false;
		}
		return false ;// No special action needed; turn can continue 
	}
	//--------------------------------------Question-----------------------------------------------
	// Handles the action triggered when a player interacts with a "QUESTION" cell.
	private void handleActionOfQuestion(int gameNumm,int row, int col, Boolean isLeft, JButton[][] buttons) {
		// First check if the cell was already used, skip popup if yes
		if (GameController.iscellUsed(gameNumm, isLeft, row, col)) {
			GameController.setCanSwitch(false);//don't switch
			showTimedMessage("Already used this turn!", Color.gray, buttons[row][col]);
			return;
		}
		//Check if player has enough points FIRST
		//Not enough cash → stop here
		int getIfPlayerHasEnoughPoints = GameController.CheckActivateCost(gamenum);
		if(getIfPlayerHasEnoughPoints < 0)
		{
			MessagePlanet.showBlockingMessageDialog(
			        this,
			        "You can't open the question!!\n You need more " + Math.abs(getIfPlayerHasEnoughPoints) + " points to unlock the question.",
			        Color.ORANGE,   // warning color border
			        "Not Enough points",
			        "OK"
			    );
			
			GameController.setCanSwitch(false);//don't switch
			return;
		}
		// Ask cost (UI)
		int cost = GameController.GetGameSurpriseQuestionCoust(gameNumm);
		boolean confirmed = MessagePlanet.showConfirmDialog(
			    this,
			    "This question costs " + cost + " points to attempt.\nDo you want to proceed?",
			    new Color(200, 140, 200),
			    "Question Cost","Ok","Cancel"
			);
		// Game logic
		GameController.QuestionResult result = GameController.handleQuestion(gameNumm, isLeft, row, col, confirmed );

		switch (result) {

		case CANCELED:
			return;

		case ACTIVATED:
			break;
		}
		updateScore(GameController.getSharedPoints(gamenum));

		// Show question dialog
		QuestionView view = new QuestionView(null, gameNumm, row, col, isLeft);
		view.setVisible(true); 

		// Update cell and GUI after dialog
		String displayEmoji = GameController.getCellDisplay(gamenum, isLeft, row, col);
		updateScore(GameController.getSharedPoints(gamenum));
		setSharedHearts(GameController.getSharedLivesGame(gamenum));
		//New Icon
		buttons[row][col].setIcon(new ImageIcon(renderEmojiToImage(displayEmoji,
				buttons[row][col].getWidth(),
				buttons[row][col].getHeight())));
		//Handle  question action
		GameController.QuestionAction action = GameController.getPendingQuestionAction();
		if (action == GameController.QuestionAction.REVEAL_RANDOM_MINE) {
			int[] rc = GameController.revealRandomHiddenMine(gameNumm, isLeft);
			if (rc != null) {
				String emoji = GameController.getCellDisplay(gameNumm, isLeft, rc[0], rc[1]);
				buttons[rc[0]][rc[1]].setIcon(
						new ImageIcon(renderEmojiToImage(
								emoji,
								buttons[rc[0]][rc[1]].getWidth(),
								buttons[rc[0]][rc[1]].getHeight()
								))
						);
				buttons[rc[0]][rc[1]].setBackground(new Color(255, 180, 80));
			}
		}
		if (action == GameController.QuestionAction.REVEAL_3X3) {
			ArrayList<int[]> cells = GameController.reveal3x3RandomGrid(gameNumm, isLeft);
			for (int[] cell : cells) {
				showCell(buttons[cell[0]][cell[1]], cell[0], cell[1], isLeft);
			}
		}
		GameController.clearPendingQuestionAction();
	}
	//Checks whether the turn can be switched after a question cell action.
	private boolean CheckCanSwitch(int row, int col, Boolean isLeft, JButton[][] buttons) {
		Boolean CanSwitch= GameController.isCanSwitch();
		return CanSwitch;
	}
	//--------------------------------------Reveal-----------------------------------------------
	private void handleRevealAction(int row, int col, Boolean isLeft, JButton[][] buttons) {
		GameController.RevealResult result = GameController.handleReveal(gamenum, isLeft, row, col);
		if (result == GameController.RevealResult.ALREADY_REVEALED) {
			return; // do nothing
		}
		int size = GameController.getBoardSize(gamenum, isLeft);
		// Update all revealed cells visually
		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				if (GameController.IsCellRevealed(gamenum, isLeft, r, c)) {
					showCell(buttons[r][c], r, c, isLeft);
				}
			}
		}
		switch (result) {
		case REVEALED_MINE:
			showTimedMessage("Boom! -1 Life", Color.red, buttons[row][col]);
			break;

		case REVEALED_SAFE:
			showTimedMessage("+1 Point", new Color(0, 200, 0), buttons[row][col]);
			break;
		}

		// Update mines count UI
		if (isLeft) {
			updateLeftMines(GameController.getRemainingMines(gamenum, isLeft));
		} else {
			updateRightMines(GameController.getRemainingMines(gamenum, isLeft));
		}
	}
	/**Updates**/
	// Update score display
	public void updateScore(int score) { scoreLabel.setText("Score: " + score); }
	// Update remaining mines for left board
	public void updateLeftMines(int remaining) { leftMinesLabel.setText("Remaining Mines: " + remaining); }
	// Update remaining mines for right board
	public void updateRightMines(int remaining) { rightMinesLabel.setText("Remaining Mines: " + remaining); }
	// Update the hearts panel showing remaining lives
	public void setSharedHearts(int lives) {
		heartsPanel.removeAll();
		int displayLives = Math.min(lives, 10);
		for (int i = 0; i < displayLives; i++) heartsPanel.add(new JLabel(heartIcon));
		heartsPanel.revalidate();
		heartsPanel.repaint();
	}

	private void loadHeartImage() {
		// Load the new PNG heart with transparency
		heartIcon = new ImageIcon(getClass().getResource("/resource/NewHeartImage.png"));
		// Scale the image to 30x30 smoothly
		Image img = heartIcon.getImage();
		Image scaledImg = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		// Set the scaled icon
		heartIcon = new ImageIcon(scaledImg);
	}
	/* Updates the visual representation of a single cell.
	 * Sets the icon (emoji) and background color based on cell type.*/
	private void showCell(JButton button, int r, int c, Boolean isLeft) {
	    if (!GameController.IsCellRevealed(gamenum, isLeft, r, c)) return;
	    
	    String displayEmoji = GameController.getCellDisplay(gamenum, isLeft, r, c);
	    String cellType = GameController.GetCellType(gamenum, isLeft, r, c);
	    
	    // Use fixed dimensions from the button itself
	    int iconSize = button.getPreferredSize().width;
	    button.setIcon(new ImageIcon(renderEmojiToImage(displayEmoji, iconSize, iconSize)));
	    
	    if (cellType.equals("EMPTY")) button.setBackground(Color.WHITE);
	    if (cellType.equals("NUMBER")) button.setBackground(new Color(180, 255, 180));
	    if (cellType.equals("MINE")) button.setBackground(new Color(255, 180, 80));
	    if (cellType.equals("SURPRISE")) button.setBackground(Color.yellow);
	    if (cellType.equals("QUESTION")) button.setBackground(new Color(255, 180, 255));
	    
	    button.setOpaque(false);
	    button.setContentAreaFilled(false);
	    button.setBorderPainted(true);
	    button.setEnabled(false);
	    
	    // Force repaint without revalidating layout
	    button.repaint();
	}
	// Converts a string emoji to a BufferedImage of specified width and height.
	private BufferedImage renderEmojiToImage(String emoji, int width, int height) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, height - 4));
		FontMetrics fm = g.getFontMetrics();
		int x = (width - fm.stringWidth(emoji)) / 2;
		int y = ((height - fm.getHeight()) / 2) + fm.getAscent() + 5;  // moved down by 5 pixels
		g.setColor(Color.BLACK);
		g.drawString(emoji, x, y);
		g.dispose();
		return img;
	}

	private Border createActiveBackgroundBorder() {
	    Border glow = BorderFactory.createCompoundBorder(
	        BorderFactory.createLineBorder(new Color(0, 200, 0, 180), 3, true),
	        BorderFactory.createLineBorder(new Color(0, 255, 0), 2, true)
	    );

	    Border padding = BorderFactory.createEmptyBorder(0, 0, 0, 0);
	    return BorderFactory.createCompoundBorder(glow, padding);
	}

	// Highlights the current player panel and enables/disables the respective board.
	public void highlightCurrentPlayer(int player) {
		Border activeBgBorder = createActiveBackgroundBorder();

		if (player == 1) {
			// leftPlayer  Active
			leftPlayerPanel.setBackgroundColor(new Color(212, 175, 55));
			leftPlayerPanel.setBorder(
					BorderFactory.createCompoundBorder(
							BorderFactory.createLineBorder(new Color(0, 100, 0, 150), 3, true), 
							BorderFactory.createCompoundBorder(
									BorderFactory.createLineBorder(new Color(0, 200, 0, 180), 3, true), 
									BorderFactory.createLineBorder(new Color(0, 255, 0), 2, true)       
									)
							)
					);
			enableBoard(leftBoard, true);
			leftBackground.setBorder(activeBgBorder);
			rightBackground.setBorder(null);

			updateBoardColors(leftBoard, true, true);
			//  rightPlayer UnActive
			rightPlayerPanel.setBackgroundColor(Color.lightGray);
			rightPlayerPanel.setBorder(null);
			enableBoard(rightBoard, false);
			updateBoardColors(rightBoard, false, false);

		} else {
			//  rightPlayer Active
			rightPlayerPanel.setBackgroundColor(new Color(212, 175, 55));
			rightPlayerPanel.setBorder(
					BorderFactory.createCompoundBorder(
							BorderFactory.createLineBorder(new Color(0, 100, 0, 150), 3, true), 
							BorderFactory.createCompoundBorder(
									BorderFactory.createLineBorder(new Color(0, 200, 0, 180), 3, true), 
									BorderFactory.createLineBorder(new Color(0, 255, 0), 2, true)       
									)
							)
					);
			enableBoard(rightBoard, true);
			rightBackground.setBorder(activeBgBorder);
			leftBackground.setBorder(null);

			updateBoardColors(rightBoard, true, false);
			// leftPlayer UnActive
			leftPlayerPanel.setBackgroundColor(Color.lightGray);
			leftPlayerPanel.setBorder(null);
			enableBoard(leftBoard, false);
			updateBoardColors(leftBoard, false, true); 
		}
		if (hintButton != null) {
	        hintButton.repaint();
	       // System.out.println("DEBUG: Hint button repainted for player " + player);
	    }
	}

	// Update and make sure boards color acorrding the turn
	private void updateBoardColors(JButton[][] board, boolean isActive, boolean isLeft) {
		Color cellColor;		    
		if (isActive) {
			// Active board color – purple for left, brown for right
			cellColor = isLeft ? PLAYER1_ACTIVE_COLOR : PLAYER2_ACTIVE_COLOR;
		} else {
			// Inactive board color – gray
			cellColor = DISABLED_BOARD_COLOR;
		}
		// Only apply color to cells that are not yet revealed
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[0].length; c++) {
				if (!GameController.IsCellRevealed(gamenum, isLeft, r, c)) {  
					board[r][c].setBackground(cellColor);
				}
			}
		}
	}

	//Enables or disables all buttons in a board.
	private void enableBoard(JButton[][] board, boolean enable) {
		for (int r = 0; r < board.length; r++)
			for (int c = 0; c < board[0].length; c++)
				board[r][c].setEnabled(enable);
	}

	//Shows a small temporary popup message near a JButton for feedback (points, life changes, etc.)
	private void showTimedMessage(String message, Color color, JButton button) {
		JComponent msg = new JComponent() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2.setFont(new Font("Arial", Font.BOLD, 26));
				FontMetrics fm = g2.getFontMetrics();
				int x = 5;
				int y = fm.getAscent() + 5;
				// Outline
				g2.setColor(Color.BLACK);
				for (int dx = -2; dx <= 2; dx++) {
					for (int dy = -2; dy <= 2; dy++) {
						g2.drawString(message, x + dx, y + dy);
					}
				}
				// Main color
				g2.setColor(color);
				g2.drawString(message, x, y);
				g2.dispose();
			}

			@Override
			public Dimension getPreferredSize() {
				FontMetrics fm = getFontMetrics(new Font("Arial", Font.BOLD, 26));
				int w = fm.stringWidth(message) + 20;
				int h = fm.getHeight() + 20;
				return new Dimension(w, h);
			}
		};
		msg.setOpaque(false);
		JWindow popup = new JWindow();
		popup.setBackground(new Color(0, 0, 0, 0));
		popup.add(msg);
		popup.pack();
		// IMPROVED POSITIONING - Keep within screen bounds
		Point btnOnScreen = button.getLocationOnScreen();
		int x = btnOnScreen.x + button.getWidth()/2 - popup.getWidth()/2;
		int y = btnOnScreen.y - popup.getHeight() - 5;
		// Get screen dimensions
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();	    
		// Adjust X if too far left or right
		if (x < 0) {
			x = 10; // Left edge padding
		} else if (x + popup.getWidth() > screenSize.width) {
			x = screenSize.width - popup.getWidth() - 10; // Right edge padding
		}	    
		// Adjust Y if too high (near top of screen)
		if (y < 0) {
			// Show below button instead
			y = btnOnScreen.y + button.getHeight() + 5;
		}	    
		// If still off bottom of screen, clamp it
		if (y + popup.getHeight() > screenSize.height) {
			y = screenSize.height - popup.getHeight() - 10;
		}	    
		popup.setLocation(x, y);
		popup.setVisible(true);
		new javax.swing.Timer(1500, e -> popup.dispose()).start();
	}
	// =============  Reveal all cells at game end =============
	private void revealAllCells() {
		// Reveal left board
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				if (!GameController.IsCellRevealed(gamenum, true, r, c)) {
					GameController.RevealCell(gamenum, true, r, c);
				}
				showCell(leftBoard[r][c], r, c, true);
			}
		}	    
		// Reveal right board
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				if (!GameController.IsCellRevealed(gamenum, false, r, c)) {
					GameController.RevealCell(gamenum, false, r, c);
				}
				showCell(rightBoard[r][c], r, c, false);
			}
		}
	}
	// ============= Convert remaining lives to points at game end =============
	private int convertRemainingLivesToPoints() {
		int remainingLives = GameController.getSharedLivesGame(gamenum);
		int activationCost = GameController.GetGameSurpriseQuestionCoust(gamenum);
		int bonusPoints = remainingLives * activationCost;

		if (bonusPoints > 0) {
			GameController.UpdateSharedPoints(gamenum, bonusPoints);
		}

		return bonusPoints;
	}
	/**
	 * Observer callback - automatically called when music state changes
	 */
	@Override
	public void onMusicStateChanged() {
		updateMusicIcon();
		System.out.println("📢 GameBoards: Music state updated");
	}
	// Enhanced non-blocking popup that stacks vertically
	private void showNonBlockingMessage(String message, Color color, int duration, int delayBeforeShow) {
		JWindow popup = new JWindow();
		popup.setBackground(new Color(0, 0, 0, 0));

		JPanel panel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// Semi-transparent background
				g2.setColor(new Color(0, 0, 0, 200));
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

				// Border
				g2.setColor(color);
				g2.setStroke(new BasicStroke(3));
				g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);

				g2.dispose();
			}
		};

		JLabel label = new JLabel("<html><div style='text-align: center;'>" + 
				message.replace("\n", "<br>") + "</div></html>");
		label.setFont(new Font("Arial", Font.BOLD, 18));
		label.setForeground(Color.WHITE);
		label.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

		panel.setLayout(new BorderLayout());
		panel.add(label);
		panel.setOpaque(false);

		popup.add(panel);
		popup.pack();

		// Get screen dimensions
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		// Calculate center position, then move up slightly (40% from top instead of 50%)
		int x = (screenSize.width - popup.getWidth()) / 2;
		int y = (int) (screenSize.height * 0.40) - (popup.getHeight() / 2);

		// Stack popups vertically if there are already visible ones
		Component[] windows = Window.getWindows();
		int visiblePopups = 0;
		for (Component w : windows) {
			if (w instanceof JWindow && w.isVisible()) {
				visiblePopups++;
			}
		}

		// Offset each popup vertically for stacking
		y += visiblePopups * (popup.getHeight() + 15);

		// Keep within screen bounds
		x = Math.max(10, Math.min(x, screenSize.width - popup.getWidth() - 10));
		y = Math.max(10, Math.min(y, screenSize.height - popup.getHeight() - 10));

		popup.setLocation(x, y);

		// Delay showing the popup by delayBeforeShow milliseconds
		new javax.swing.Timer(delayBeforeShow, e -> {
			popup.setVisible(true);

			// Timer to close the popup after 'duration' milliseconds
			javax.swing.Timer closeTimer = new javax.swing.Timer(duration, ev -> popup.dispose());
			closeTimer.setRepeats(false);
			closeTimer.start();

			// Stop the delay timer
			((javax.swing.Timer) e.getSource()).stop();
		}).start();
	}
	// ========== ADD CLEANUP ==========
	@Override
	public void dispose() {
		musicManager.removeMusicStateListener(this);
		System.out.println("✓ GameBoards: Unregistered from music updates");
		super.dispose();
	}
	// =================================
	public String getHintStatus() {
	    return "Player1 hint used: " + player1HintUsed + 
	           ", Player2 hint used: " + player2HintUsed +
	           ", Current player: " + GameController.GameGetCurrentPlayer(gamenum);
	}

}