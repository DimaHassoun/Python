package view;

import javax.swing.*;

import Model.GameResult;
import controller.GameController;
import controller.GameHistoryController;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GameBoards extends JFrame {

	private int rows, cols, leftMines, rightMines, score = 0, gamenum;
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

		setTitle("Two Board Game");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);
		
		// Apply saved window size
		windowSizeManager.applyToFrame(this);

		// Main background
		JPanel mainBackground = new JPanel(new BorderLayout()) {
		    private Image bgImage;

		    {
		        // Load image correctly (no "src/")
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
		JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
		centerPanel.setOpaque(false);

		// LEFT PLAYER
		leftPlayerPanel = new RoundedPanel(15, Color.white);
		leftPlayerPanel.setPreferredSize(new Dimension(200, 50));
		leftPlayerPanel.setLayout(new BorderLayout());
		leftPlayerLabel = new JLabel(nameL, SwingConstants.CENTER);
		leftPlayerLabel.setFont(new Font("Arial", Font.BOLD, 18));
		leftPlayerPanel.add(leftPlayerLabel, BorderLayout.CENTER);
		JPanel leftWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
		leftWrapper.setOpaque(false);
		leftWrapper.add(leftPlayerPanel);

		leftBoardPanel = new RoundedPanel(15);
		leftBoardPanel.setPreferredSize(new Dimension(500, 500));
		leftBoardPanel.setLayout(new BorderLayout());
		JPanel leftBoardInner = createBoard(true);
		leftBoardPanel.add(leftBoardInner, BorderLayout.CENTER);
		leftGridPanel = leftBoardInner;

		leftMinesLabel = new JLabel("Remaining Mines: " + leftMines, SwingConstants.CENTER);
		leftMinesLabel.setForeground(new Color(255, 215, 0));
		leftMinesLabel.setFont(new Font("Arial", Font.BOLD, 16));

		JPanel leftTopInfo = new JPanel();
		leftTopInfo.setLayout(new BoxLayout(leftTopInfo, BoxLayout.Y_AXIS));
		leftTopInfo.setOpaque(false);
		leftTopInfo.add(leftWrapper);
		leftTopInfo.add(Box.createVerticalStrut(5));
		leftTopInfo.add(leftBoardPanel);
		leftTopInfo.add(Box.createVerticalStrut(5));
		leftTopInfo.add(leftMinesLabel);

		leftBackground = new RoundedPanel(15, new Color(128, 0, 128, 128));
		leftBackground.setBorderColor(new Color(255, 215, 0));
		leftBackground.setLayout(new BorderLayout());
		leftBackground.add(leftTopInfo, BorderLayout.NORTH);

		// RIGHT PLAYER
		rightPlayerPanel = new RoundedPanel(15, Color.white);
		rightPlayerPanel.setPreferredSize(new Dimension(200, 50));
		rightPlayerPanel.setLayout(new BorderLayout());
		rightPlayerLabel = new JLabel(nameR, SwingConstants.CENTER);
		rightPlayerLabel.setFont(new Font("Arial", Font.BOLD, 18));
		rightPlayerPanel.add(rightPlayerLabel, BorderLayout.CENTER);
		JPanel rightWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
		rightWrapper.setOpaque(false);
		rightWrapper.add(rightPlayerPanel);

		rightBoardPanel = new RoundedPanel(15);
		rightBoardPanel.setPreferredSize(new Dimension(500, 500));
		rightBoardPanel.setLayout(new BorderLayout());
		JPanel rightBoardInner = createBoard(false);
		rightBoardPanel.add(rightBoardInner, BorderLayout.CENTER);
		rightGridPanel = rightBoardInner;
		rightGridPanel.setOpaque(false);

		rightMinesLabel = new JLabel("Remaining Mines: " + rightMines, SwingConstants.CENTER);
		rightMinesLabel.setForeground(new Color(255, 215, 0));
		rightMinesLabel.setFont(new Font("Arial", Font.BOLD, 16));

		JPanel rightTopInfo = new JPanel();
		rightTopInfo.setLayout(new BoxLayout(rightTopInfo, BoxLayout.Y_AXIS));
		rightTopInfo.setOpaque(false);
		rightTopInfo.add(rightWrapper);
		rightTopInfo.add(Box.createVerticalStrut(5));
		rightTopInfo.add(rightBoardPanel);
		rightTopInfo.add(Box.createVerticalStrut(5));
		rightTopInfo.add(rightMinesLabel);

		rightBackground = new RoundedPanel(15);
		rightBackground.setBorderColor(new Color(255, 215, 0));
		rightBackground.setLayout(new BorderLayout());
		rightBackground.add(rightTopInfo, BorderLayout.NORTH);

		centerPanel.add(leftBackground);
		centerPanel.add(rightBackground);
		add(centerPanel, BorderLayout.CENTER);

		// ========================= SOUTH PANEL =========================
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.setOpaque(false);

		heartsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		heartsPanel.setOpaque(false);
		loadHeartImage();
		setSharedHearts(GameController.GameGetSharedLives(gamenum));
		southPanel.add(heartsPanel, BorderLayout.CENTER);

		// Exit button
		JPanel bottomRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
		bottomRightPanel.setOpaque(false);
		exitLabel = new JLabel("Exit");
		exitLabel.setFont(new Font("Verdana", Font.BOLD, 30));
		exitLabel.setForeground(new Color(246, 230, 138));
		exitLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		exitLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				handleButtonClick("Exit", -1, -1, false);
			}
		});
		bottomRightPanel.add(exitLabel);
		southPanel.add(bottomRightPanel, BorderLayout.EAST);
		add(southPanel, BorderLayout.SOUTH);

		// ========================= TOP PANEL =========================
		settingsLabel = new JLabel("⚙");
		settingsLabel.setFont(new Font("Dialog", Font.BOLD, 35));
		settingsLabel.setForeground(new Color(246, 230, 138));
		settingsLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		settingsLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				showSettingsMenu();
			}
		});

		musicLabel = new JLabel(musicManager.isPlaying() ? "♪" : "🔇");
		musicLabel.setFont(new Font("Dialog", Font.BOLD, 35));
		musicLabel.setForeground(musicManager.isPlaying() ? new Color(246, 230, 138) : new Color(180, 180, 180));
		musicLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		musicLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				toggleMusic();
			}
		});

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);

		JPanel topLeftIcons = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
		topLeftIcons.setOpaque(false);
		topLeftIcons.add(settingsLabel);
		topLeftIcons.add(musicLabel);

		gameNumberLabel = new JLabel("Game No. " + gamenum, SwingConstants.CENTER);
		scoreLabel = new JLabel("Score: " + score, SwingConstants.CENTER);
		difficultyLabel = new JLabel("Difficulty Level: " + GameController.GameGetDifficulty(gamenum), SwingConstants.CENTER);

		gameNumberLabel.setForeground(new Color(255, 215, 0));
		scoreLabel.setForeground(new Color(255, 215, 0));
		difficultyLabel.setForeground(new Color(255, 215, 0));

		Font topFont = new Font("Arial", Font.BOLD, 16);
		gameNumberLabel.setFont(topFont);
		scoreLabel.setFont(topFont);
		difficultyLabel.setFont(topFont);

		JPanel topCenterPanel = new JPanel(new GridLayout(3, 1, 2, 2));
		topCenterPanel.setOpaque(false);
		topCenterPanel.add(gameNumberLabel);
		topCenterPanel.add(scoreLabel);
		topCenterPanel.add(difficultyLabel);

		topPanel.add(topLeftIcons, BorderLayout.WEST);
		topPanel.add(topCenterPanel, BorderLayout.CENTER);
		add(topPanel, BorderLayout.NORTH);

		highlightCurrentPlayer(GameController.GameGetCurrentPlayer(gamenum));
		
		// Apply window size again to ensure it's set after all components are added
		windowSizeManager.applyToFrame(this);
		setLocationRelativeTo(null);
	}

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
		volumeItem.addActionListener(e -> showVolumeControl());
		settingsMenu.add(volumeItem);

		settingsMenu.addSeparator();

		// Restart Game
		JMenuItem restartItem = new JMenuItem("🔄‌ Restart Game");
		restartItem.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
		restartItem.setForeground(new Color(246, 230, 138));
		restartItem.setBackground(new Color(60, 0, 90));
		restartItem.addActionListener(e -> restartGame());
		settingsMenu.add(restartItem);

		// Stop Game
		JMenuItem stopItem = new JMenuItem("⏹ Stop Game");
		stopItem.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
		stopItem.setForeground(new Color(246, 230, 138));
		stopItem.setBackground(new Color(60, 0, 90));
		stopItem.addActionListener(e -> stopGame());
		settingsMenu.add(stopItem);

		settingsMenu.show(settingsLabel, 0, settingsLabel.getHeight());
	}

	// Show volume control dialog
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

	private void restartGame() {
		int response = JOptionPane.showConfirmDialog(this,
			"Are you sure you want to restart the game? All progress will be lost.",
			"Restart Game", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		if (response == JOptionPane.YES_OPTION) {
			String difficulty = GameController.GameGetDifficulty(gamenum);
			Model.Game newGame = GameController.createNewGame(player1Name, player2Name, difficulty);
			newGame.start();

			GameBoards newGameBoard = new GameBoards(
				newGame.getBoard1().getSize(), newGame.getBoard1().getSize(),
				newGame.getBoard1().getTotalMines(), newGame.getBoard2().getTotalMines(),
				player1Name, player2Name, newGame.getId());
			newGameBoard.setVisible(true);
			GameBoards.this.dispose();
		}
	}

	private void stopGame() {
		int response = JOptionPane.showConfirmDialog(this,
			"Are you sure you want to stop the game and return to the main menu?",
			"Stop Game", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		if (response == JOptionPane.YES_OPTION) {
			new FirstScreen();
			GameBoards.this.dispose();
		}
	}

	private void toggleMusic() {
		musicManager.toggleMusic();
		updateMusicIcon();
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

	private JPanel createBoard(boolean isLeft) {
		JPanel boardPanel = new JPanel(new GridLayout(rows, cols, 3, 3));
		JButton[][] board = new JButton[rows][cols];

		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				JButton cell = new JButton();
				cell.setPreferredSize(new Dimension(30, 30));
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
	//Button Actions
	private void handleButtonClick(String source, int row, int col, boolean isFlag) {
		//Exit
		if (source.equals("Exit")) {
			new NewGameScreen().setVisible(true);
			GameBoards.this.dispose();
			return;
		}
		if (GameController.GameIsGameOver(gamenum)) return;

		int currentPlayer = GameController.GameGetCurrentPlayer(gamenum);
		if ((currentPlayer == 1 && source.equals("Right")) || (currentPlayer == 2 && source.equals("Left"))) {
			JOptionPane.showMessageDialog(this, "It's not your turn!", "Wait", JOptionPane.WARNING_MESSAGE);
			return;
		}
		boolean isLeft = source.equals("Left");
	
		JButton[][] buttons = source.equals("Left") ? leftBoard : rightBoard;
		boolean shouldSwitchTurn = true;
		String cellType = GameController.GetCellType(gamenum, isLeft, row, col);
		Boolean IsCellUsed=GameController.iscellUsed(gamenum, isLeft, row, col);
		
		if (GameController.IsCellRevealed(gamenum, isLeft, row, col)&&!cellType.equals("SURPRISE")&&!cellType.equals("QUESTION")){return;}
		//Cell Revealed: if is Surprise or Question: Do
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
		//Flag Action
		else if (isFlag) {
			handleFlagAction(isLeft, row, col, buttons);
		} 
		//if cell is flagged: u can only "Unflag"
		if (GameController.IsCellFlagged(gamenum, isLeft, row, col)&&!isFlag) {
			return;
		}
		//Not action flag
		 if (!isFlag) {
		    handleRevealAction(row, col, isLeft, buttons);
		}

		updateScore(GameController.getSharedPoints(gamenum));
		setSharedHearts(GameController.getSharedLivesGame(gamenum));
		updateLeftMines(GameController.getRemainingMines(gamenum, true));
		updateRightMines(GameController.getRemainingMines(gamenum, false));

		if (GameController.getSharedLivesGame(gamenum) <= 0) {
			DefeatScreen defeatScreen = new DefeatScreen(GameController.getSharedPoints(gamenum) , this);
			 // Reveal all cells before showing defeat screen
		    revealAllCells();
		    
	        defeatScreen.setVisible(true);
			
			// save history game:
			 GameHistoryController.createHistoryEntry(
				        GameController.getGame(gamenum),
				        GameResult.Defeat
				    );
			GameController.GameFinish(gamenum);
			return;
		}
		if (GameController.IsGameVictory(gamenum)) {
		    // Reveal all cells first
		    revealAllCells();
		    
		    // Convert remaining lives to bonus points
		    int bonusPoints = convertRemainingLivesToPoints();
		    
		    // Update score display
		    updateScore(GameController.getSharedPoints(gamenum));
		    
		    // Show victory screen with updated score
		    int finalScore = GameController.getSharedPoints(gamenum);
		    
		    // Optional: Show bonus message if there were remaining lives
		    if (bonusPoints > 0) {
		        JOptionPane.showMessageDialog(this,
		            "Bonus! +" + bonusPoints + " points for " + 
		            (bonusPoints / GameController.GetGameSurpriseQuestionCoust(gamenum)) + 
		            " remaining lives!",
		            "Victory Bonus",
		            JOptionPane.INFORMATION_MESSAGE);
		    }
		    
		    VictoryScreen victoryScreen = new VictoryScreen(finalScore, this);
		    victoryScreen.setVisible(true);
		    
		    // Save history game
		    GameHistoryController.createHistoryEntry(
		        GameController.getGame(gamenum),
		        GameResult.Victory
		    );
		    GameController.GameFinish(gamenum);
		    return;
		}
		if (shouldSwitchTurn) {
		    GameController.switchTurn(gamenum, this);
		}
	}
	//--------------------------------------Flag-----------------------------------------------
	private void handleFlagAction(Boolean isLeft, int row, int col, JButton[][] buttons) {
		if (GameController.IsCellFlagged(gamenum, isLeft, row, col)) {
			GameController.UnFlaggedCell(gamenum, isLeft, row, col);
			String cellType = GameController.GetCellType(gamenum, isLeft, row, col);
			if (cellType.equals("MINE")) GameController.IncrementRemainingMinesInBoard(gamenum, isLeft);
			GameController.RevealCell(gamenum, isLeft, row, col);
			showCell(buttons[row][col], row, col, isLeft);
		} else {
			GameController.FlaggedCell(gamenum, isLeft, row, col);
			String cellType = GameController.GetCellType(gamenum, isLeft, row, col);
			if (cellType.equals("MINE")) {
				GameController.UpdateSharedPoints(gamenum, 1);
				showTimedMessage("MINE: +1 points", new Color(0, 200, 0), buttons[row][col]);
				GameController.RevealCell(gamenum, isLeft, row, col);
				GameController.decrementRemainingMinesInBoard(gamenum, isLeft);
				showCell(buttons[row][col], row, col, isLeft);
			} else {
				GameController.UpdateSharedPoints(gamenum, -3);
				showTimedMessage("Not MINE: -3 Points", Color.red, buttons[row][col]);
				buttons[row][col].setIcon(new ImageIcon(
						renderEmojiToImage("🚩", buttons[row][col].getWidth(), buttons[row][col].getHeight())));
			}
		}
	}
	//--------------------------------------Surprise-----------------------------------------------
	private boolean handleActionOfSurprise(int row, int col, Boolean isLeft, JButton[][] buttons) {
		//Surprise
	    boolean wasSurprise = GameController.GetCellType(gamenum, isLeft, row, col).equals("SURPRISE");
	    if (wasSurprise && GameController.IsCellRevealed(gamenum, isLeft, row, col)) {
	        if(!GameController.iscellUsed(gamenum, isLeft, row, col)) {
	        int cost = GameController.GetGameSurpriseQuestionCoust(gamenum);
	        int choice = JOptionPane.showConfirmDialog(
	                this,
	                "Activating a Surprise costs " + cost + " points.\nDo you want to continue?",
	                "Surprise Cost",
	                JOptionPane.OK_CANCEL_OPTION
	        );

	        if (choice != JOptionPane.OK_OPTION) {
	            return false;
	        }
	        //NOTHING BELOW IS CHANGED
	        }
	    	String result = GameController.ActivateSurpriseCell(gamenum, isLeft, row, col);
	    	String[] parts = result.split(":");
	    	String type = parts[0]; // GOOD, BAD, or ALREADY_USED

	    	if (parts.length > 1) {
	    		int points = Integer.parseInt(parts[1]);
	    		String displayEmoji = GameController.getCellDisplay(gamenum, isLeft, row, col);

	    		switch(type) {
	    		case "GOOD":
	    			showTimedMessage("Surprise Good! +Life & +" + points + " Points", new Color(0,200,0), buttons[row][col]);
	    			updateScore(GameController.getSharedPoints(gamenum));
	    			setSharedHearts(GameController.getSharedLivesGame(gamenum));
	    			buttons[row][col].setIcon(new ImageIcon(renderEmojiToImage(displayEmoji, buttons[row][col].getWidth(), buttons[row][col].getHeight())));
	    			return true;
	    		case "BAD":
	    			showTimedMessage("Surprise Bad! -Life & -" + points + " Points", Color.red, buttons[row][col]);
	    			updateScore(GameController.getSharedPoints(gamenum));
	    			setSharedHearts(GameController.getSharedLivesGame(gamenum));
	    			buttons[row][col].setIcon(new ImageIcon(renderEmojiToImage(displayEmoji, buttons[row][col].getWidth(), buttons[row][col].getHeight())));
	    			return true;
	    		}
	    	} else if ("ALREADY_USED".equals(type)) {
	    		showTimedMessage("Already used this turn!", Color.gray, buttons[row][col]);
	    		return false;
	    	} else {
	    		System.err.println("Unexpected surprise result: " + result);
	    		return false;
	    	}
	    }
	    return true;
	}
	//--------------------------------------Question-----------------------------------------------
	private void handleActionOfQuestion(int gameNumm,int row, int col, Boolean isLeft, JButton[][] buttons) {
		if (GameController.iscellUsed(gameNumm, isLeft, row, col)) {
			showTimedMessage("Already used this turn!", Color.gray, buttons[row][col]);
			GameController.setCanSwitch(false);
			return;
			}
		// Ask player if they want to pay the cost
		int cost = GameController.GetGameSurpriseQuestionCoust(gameNumm);
		int choice = JOptionPane.showConfirmDialog(
				this,
				"This question costs " + cost + " points to attempt.\nDo you want to proceed?",
				"Question Cost",
				JOptionPane.OK_CANCEL_OPTION
				);

		if (choice != JOptionPane.OK_OPTION) {
			// Player canceled -> don't charge points and allow them to answer later
			GameController.setCanSwitch(false); // will make shouldSwitchTurn = false
			return;
		}
		// Deduct cost
		GameController.ActivateQuestion(gameNumm);
		updateScore(GameController.getSharedPoints(gamenum));

		if (!GameController.iscellUsed(gameNumm, isLeft, row, col)) {
			QuestionView view = new QuestionView(null, gameNumm, row, col, isLeft);
			view.setVisible(true); 

			// after dialog closes, update GUI
			String displayEmoji = GameController.getCellDisplay(gamenum, isLeft, row, col);
			updateScore(GameController.getSharedPoints(gamenum));
			setSharedHearts(GameController.getSharedLivesGame(gamenum));
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
					 buttons[rc[0]][rc[1]].setBackground(new Color(255, 100, 100));
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
	}
	//Switch
	private boolean CheckCanSwitch(int row, int col, Boolean isLeft, JButton[][] buttons) {
		Boolean CanSwitch= GameController.isCanSwitch();
		return CanSwitch;

	}
	//--------------------------------------Reveal-----------------------------------------------
	private void handleRevealAction(int row, int col, Boolean isLeft, JButton[][] buttons) {
		if(GameController.IsCellRevealed(gamenum, isLeft, row, col))return;
		//Mine
		boolean wasMine = GameController.GetCellType(gamenum, isLeft, row, col).equals("MINE");
		GameController.PlayCascadeReveal(gamenum, isLeft, row, col);
		int SizeNum = GameController.getBoardSize(gamenum, isLeft);
		for (int r = 0; r < SizeNum; r++) {
			for (int c = 0; c < SizeNum; c++) {
				if (GameController.GetCellType(gamenum, isLeft, row, col).equals("MINE") &&
					GameController.IsCellRevealed(gamenum, isLeft, row, col) &&
					!GameController.IsCellCounted(gamenum, isLeft, row, col)) {
					GameController.decrementRemainingMinesInBoard(gamenum, isLeft);
					GameController.setCountedAsCounted(gamenum, isLeft, row, col);
				}
				if (GameController.IsCellRevealed(gamenum, isLeft, row, col))
					showCell(buttons[r][c], r, c, isLeft);
			}
		}
		if (wasMine){
			GameController.UpdateSharedLivesGame(gamenum, GameController.getSharedLivesGame(gamenum) - 1);
			showTimedMessage("Boom! -1 Life", Color.red, buttons[row][col]);
		}
		else {
			GameController.UpdateSharedPoints(gamenum, 1);
			showTimedMessage("+1 Point", new Color(0, 200, 0),buttons[row][col]);
		}
		if (isLeft) updateLeftMines(GameController.getRemainingMines(gamenum, isLeft));
		else updateRightMines(GameController.getRemainingMines(gamenum, isLeft));
	}

	public void updateScore(int score) { scoreLabel.setText("Score: " + score); }
	public void updateLeftMines(int remaining) { leftMinesLabel.setText("Remaining Mines: " + remaining); }
	public void updateRightMines(int remaining) { rightMinesLabel.setText("Remaining Mines: " + remaining); }

	public void setSharedHearts(int lives) {
		heartsPanel.removeAll();
		int displayLives = Math.min(lives, 10);
		for (int i = 0; i < displayLives; i++) heartsPanel.add(new JLabel(heartIcon));
		heartsPanel.revalidate();
		heartsPanel.repaint();
	}

	private void loadHeartImage() {
		heartIcon = new ImageIcon(getClass().getResource("/resource/heart_image.jpg"));
		Image img = heartIcon.getImage();
		Image scaledImg = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		heartIcon = new ImageIcon(scaledImg);
	}

	private void showCell(JButton button, int r, int c, Boolean isLeft) {
		if (!GameController.IsCellRevealed(gamenum, isLeft, r, c)) return;

		String displayEmoji = GameController.getCellDisplay(gamenum, isLeft, r, c);
		String cellType = GameController.GetCellType(gamenum, isLeft, r, c);
		button.setIcon(new ImageIcon(renderEmojiToImage(displayEmoji, button.getWidth(), button.getHeight())));
		
		if (cellType.equals("EMPTY")) button.setBackground(new Color(200, 200, 255));
		if (cellType.equals("NUMBER")) button.setBackground(new Color(180, 255, 180));
		if (cellType.equals("MINE")) button.setBackground(new Color(255, 100, 100));
		if (cellType.equals("SURPRISE")) button.setBackground(Color.yellow);
		if (cellType.equals("QUESTION")) button.setBackground(new Color(255, 180, 255));
		
		button.setOpaque(true);
		button.setBorderPainted(false);
		button.setEnabled(false);
	}

	private BufferedImage renderEmojiToImage(String emoji, int width, int height) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, height - 4));
		FontMetrics fm = g.getFontMetrics();
		int x = (width - fm.stringWidth(emoji)) / 2;
		int y = ((height - fm.getHeight()) / 2) + fm.getAscent();
		g.setColor(Color.BLACK);
		g.drawString(emoji, x, y);
		g.dispose();
		return img;
	}

	public void highlightCurrentPlayer(int player) {
		if (player == 1) {
			leftPlayerPanel.setBackgroundColor(new Color(255, 215, 0));
			enableBoard(leftBoard, true);
			rightPlayerPanel.setBackgroundColor(Color.lightGray);
			enableBoard(rightBoard, false);
		} else {
			rightPlayerPanel.setBackgroundColor(new Color(255, 215, 0));
			enableBoard(rightBoard, true);
			leftPlayerPanel.setBackgroundColor(Color.lightGray);
			enableBoard(leftBoard, false);
		}
	}

	private void enableBoard(JButton[][] board, boolean enable) {
		for (int r = 0; r < board.length; r++)
			for (int c = 0; c < board[0].length; c++)
				board[r][c].setEnabled(enable);
	}

	static class RoundedPanel extends JPanel {
		private int cornerRadius;
		private Color backgroundColor, borderColor;

		public RoundedPanel(int radius) { this(radius, Color.white); }
		public RoundedPanel(int radius, Color bgColor) {
			cornerRadius = radius;
			backgroundColor = bgColor;
		}

		public void setBorderColor(Color color) { borderColor = color; }
		public void setBackgroundColor(Color color) {
			backgroundColor = color;
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(backgroundColor);
			g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
			if (borderColor != null) {
				g2.setColor(borderColor);
				g2.setStroke(new BasicStroke(2));
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
			}
		}
	}
	//show Messages
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
}