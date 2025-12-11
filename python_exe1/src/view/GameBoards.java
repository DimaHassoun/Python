package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;

import Model.GameResult;
import controller.GameController;
import controller.GameHistoryController;

/**
 * GameBoards - המחלקה הראשית המנהלת את התצוגה הגרפית של לוח המשחק.
 * כוללת את הלוחות, הניקוד, התפריטים והאינטראקציה עם המשתמש.
 */
public class GameBoards extends JFrame {

    // ============================================================
    // 1. FIELDS & VARIABLES
    // ============================================================

    // --- Game State Variables ---
    private int rows, cols;
    private int leftMines, rightMines;
    private int score = 0;
    private int gamenum;
    private String player1Name, player2Name;

    // --- Managers ---
    private MusicManager musicManager;
    private WindowSizeManager windowSizeManager;

    // --- UI Components: Labels ---
    private JLabel exitLabel, settingsLabel, musicLabel;
    private JLabel leftPlayerLabel, rightPlayerLabel;
    private JLabel leftMinesLabel, rightMinesLabel;
    private JLabel gameNumberLabel, scoreLabel, difficultyLabel;

    // --- UI Components: Panels ---
    private RoundedPanel leftBackground, rightBackground;
    private RoundedPanel leftPlayerPanel, rightPlayerPanel;
    private RoundedPanel leftBoardPanel, rightBoardPanel;
    private JPanel heartsPanel, leftGridPanel, rightGridPanel;
    
    // --- UI Components: Boards ---
    private JButton[][] leftBoard, rightBoard;
    
    // --- Assets ---
    private ImageIcon heartIcon;

    // ============================================================
    // 2. CONSTRUCTOR & INITIALIZATION
    // ============================================================

    public GameBoards(int rows, int cols, int leftMines, int rightMines, String nameL, String nameR, int gamenum) {
        // Init State
        this.rows = rows;
        this.cols = cols;
        this.leftMines = leftMines;
        this.rightMines = rightMines;
        this.gamenum = gamenum;
        this.player1Name = nameL;
        this.player2Name = nameR;

        // Init Managers
        musicManager = MusicManager.getInstance();
        windowSizeManager = WindowSizeManager.getInstance();

        // Window Setup
        setTitle("Two Board Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        windowSizeManager.applyToFrame(this); // Apply saved size

        // 1. Setup Main Background
        setupMainBackground();

        // 2. Setup Top Panel (Stats, Settings)
        setupTopPanel();

        // 3. Setup Center Panel (Boards)
        setupCenterPanel();

        // 4. Setup South Panel (Hearts, Exit)
        setupSouthPanel();

        // 5. Finalize Setup
        highlightCurrentPlayer(GameController.GameGetCurrentPlayer(gamenum));
        windowSizeManager.applyToFrame(this); // Ensure layout is correct
        setLocationRelativeTo(null);
    }

    /**
     * מגדיר את הרקע הראשי של החלון עם תמונה.
     */
    private void setupMainBackground() {
        JPanel mainBackground = new JPanel(new BorderLayout()) {
            private Image bgImage;
            {
                URL imgURL = getClass().getResource("/resource/background.jpg");
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
    }

    // ============================================================
    // 3. UI CREATION METHODS
    // ============================================================

    private void setupTopPanel() {
        // Settings Icon
        settingsLabel = new JLabel("⚙");
        settingsLabel.setFont(new Font("Dialog", Font.BOLD, 35));
        settingsLabel.setForeground(new Color(246, 230, 138));
        settingsLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        settingsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { showSettingsMenu(); }
        });

        // Music Icon
        musicLabel = new JLabel(musicManager.isPlaying() ? "♪" : "🔇");
        musicLabel.setFont(new Font("Dialog", Font.BOLD, 35));
        musicLabel.setForeground(musicManager.isPlaying() ? new Color(246, 230, 138) : new Color(180, 180, 180));
        musicLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        musicLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { toggleMusic(); }
        });

        // Info Labels
        gameNumberLabel = new JLabel("Game No. " + gamenum, SwingConstants.CENTER);
        scoreLabel = new JLabel("Score: " + score, SwingConstants.CENTER);
        difficultyLabel = new JLabel("Difficulty Level: " + GameController.GameGetDifficulty(gamenum), SwingConstants.CENTER);

        Font topFont = new Font("Arial", Font.BOLD, 16);
        Color goldColor = new Color(255, 215, 0);
        gameNumberLabel.setFont(topFont); gameNumberLabel.setForeground(goldColor);
        scoreLabel.setFont(topFont); scoreLabel.setForeground(goldColor);
        difficultyLabel.setFont(topFont); difficultyLabel.setForeground(goldColor);

        // Container Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel topLeftIcons = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        topLeftIcons.setOpaque(false);
        topLeftIcons.add(settingsLabel);
        topLeftIcons.add(musicLabel);

        JPanel topCenterPanel = new JPanel(new GridLayout(3, 1, 2, 2));
        topCenterPanel.setOpaque(false);
        topCenterPanel.add(gameNumberLabel);
        topCenterPanel.add(scoreLabel);
        topCenterPanel.add(difficultyLabel);

        topPanel.add(topLeftIcons, BorderLayout.WEST);
        topPanel.add(topCenterPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
    }

    private void setupCenterPanel() {
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setOpaque(false);

        // --- Left Player Construction ---
        leftPlayerPanel = new RoundedPanel(15, Color.white);
        leftPlayerPanel.setPreferredSize(new Dimension(200, 50));
        leftPlayerPanel.setLayout(new BorderLayout());
        leftPlayerLabel = new JLabel(player1Name, SwingConstants.CENTER);
        leftPlayerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        leftPlayerPanel.add(leftPlayerLabel, BorderLayout.CENTER);
        
        JPanel leftWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        leftWrapper.setOpaque(false);
        leftWrapper.add(leftPlayerPanel);

        leftBoardPanel = new RoundedPanel(15);
        leftBoardPanel.setPreferredSize(new Dimension(500, 500));
        leftBoardPanel.setLayout(new BorderLayout());
        JPanel leftBoardInner = createBoard(true); // Create Grid
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

        // --- Right Player Construction ---
        rightPlayerPanel = new RoundedPanel(15, Color.white);
        rightPlayerPanel.setPreferredSize(new Dimension(200, 50));
        rightPlayerPanel.setLayout(new BorderLayout());
        rightPlayerLabel = new JLabel(player2Name, SwingConstants.CENTER);
        rightPlayerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        rightPlayerPanel.add(rightPlayerLabel, BorderLayout.CENTER);

        JPanel rightWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rightWrapper.setOpaque(false);
        rightWrapper.add(rightPlayerPanel);

        rightBoardPanel = new RoundedPanel(15);
        rightBoardPanel.setPreferredSize(new Dimension(500, 500));
        rightBoardPanel.setLayout(new BorderLayout());
        JPanel rightBoardInner = createBoard(false); // Create Grid
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

        // Add to main panel
        centerPanel.add(leftBackground);
        centerPanel.add(rightBackground);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupSouthPanel() {
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);

        // Hearts Display
        heartsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        heartsPanel.setOpaque(false);
        loadHeartImage();
        setSharedHearts(GameController.GameGetSharedLives(gamenum));
        southPanel.add(heartsPanel, BorderLayout.CENTER);

        // Exit Button
        JPanel bottomRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        bottomRightPanel.setOpaque(false);
        exitLabel = new JLabel("Exit");
        exitLabel.setFont(new Font("Verdana", Font.BOLD, 30));
        exitLabel.setForeground(new Color(246, 230, 138));
        exitLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exitLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleButtonClick("Exit", -1, -1, false);
            }
        });
        bottomRightPanel.add(exitLabel);
        southPanel.add(bottomRightPanel, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * יוצר את לוח הכפתורים (Grid) עבור שחקן.
     */
    private JPanel createBoard(boolean isLeft) {
        JPanel boardPanel = new JPanel(new GridLayout(rows, cols, 3, 3));
        JButton[][] board = new JButton[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JButton cell = new JButton();
                cell.setPreferredSize(new Dimension(30, 30));
                final int row = r, col = c;
                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
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
 // ============================================================
    // 4. CORE GAME LOGIC (CONTROLLER INTERFACE)
    // ============================================================

    /**
     * הפונקציה המרכזית המטפלת בלחיצה על כפתור בלוח או בתפריט.
     * תוקן: הבאג בלולאה (שורה 469 במקור)
     */
    private void handleButtonClick(String source, int row, int col, boolean isFlag) {
        // 1. Handle Exit
        if (source.equals("Exit")) {
            new NewGameScreen().setVisible(true);
            GameBoards.this.dispose();
            return;
        }

        // 2. Validate Game State
        if (GameController.GameIsGameOver(gamenum)) return;

        // 3. Validate Turn
        int currentPlayer = GameController.GameGetCurrentPlayer(gamenum);
        if ((currentPlayer == 1 && source.equals("Right")) || (currentPlayer == 2 && source.equals("Left"))) {
            JOptionPane.showMessageDialog(this, "It's not your turn!", "Wait", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean isLeft = source.equals("Left");
        JButton[][] buttons = isLeft ? leftBoard : rightBoard;
        boolean shouldSwitchTurn = true;

        String cellType = GameController.GetCellType(gamenum, isLeft, row, col);
        
        // 4. Handle Logic based on Cell State
        if (GameController.IsCellRevealed(gamenum, isLeft, row, col) && !cellType.equals("SURPRISE") && !cellType.equals("QUESTION")) {
            return; // Ignore clicks on revealed regular cells
        }

        // --- Logic: Revealed Special Cells ---
        if (GameController.IsCellRevealed(gamenum, isLeft, row, col)) {
            if (cellType.equals("SURPRISE")) {
                shouldSwitchTurn = handleActionOfSurprise(row, col, isLeft, buttons);
            } else if (cellType.equals("QUESTION")) {
                handleActionOfQuestion(gamenum, row, col, isLeft, buttons);
                shouldSwitchTurn = CheckCanSwitch(row, col, isLeft, buttons);
            }
        }
        // --- Logic: Flag Action ---
        else if (isFlag) {
            handleFlagAction(isLeft, row, col, buttons);
        }
        // --- Logic: Reveal Action ---
        else {
            if (GameController.IsCellFlagged(gamenum, isLeft, row, col)) {
                return; // Cannot reveal flagged cell without unflagging first
            }
            handleRevealAction(row, col, isLeft, buttons);
        }

        // 5. Update Global Game State
        updateGameStateAndCheckEnd(shouldSwitchTurn);
    }

    /**
     * עדכון מצב המשחק (ניקוד, חיים) ובדיקת תנאי ניצחון/הפסד.
     */
    private void updateGameStateAndCheckEnd(boolean shouldSwitchTurn) {
        updateScore(GameController.getSharedPoints(gamenum));
        setSharedHearts(GameController.getSharedLivesGame(gamenum));
        updateLeftMines(GameController.getRemainingMines(gamenum, true));
        updateRightMines(GameController.getRemainingMines(gamenum, false));

        // Check Defeat
        if (GameController.getSharedLivesGame(gamenum) <= 0) {
            handleDefeat();
            return;
        }

        // Check Victory
        if (GameController.IsGameVictory(gamenum)) {
            handleVictory();
            return;
        }

        // Switch Turn if needed
        if (shouldSwitchTurn) {
            GameController.switchTurn(gamenum, this);
        }
    }

    private void handleDefeat() {
        revealAllCells();
        DefeatScreen defeatScreen = new DefeatScreen(GameController.getSharedPoints(gamenum), this);
        defeatScreen.setVisible(true);
        GameHistoryController.createHistoryEntry(GameController.getGame(gamenum), GameResult.Defeat);
        GameController.GameFinish(gamenum);
    }

    private void handleVictory() {
        revealAllCells();
        int bonusPoints = convertRemainingLivesToPoints();
        updateScore(GameController.getSharedPoints(gamenum)); // Update with bonus

        if (bonusPoints > 0) {
            JOptionPane.showMessageDialog(this,
                "Bonus! +" + bonusPoints + " points for remaining lives!",
                "Victory Bonus", JOptionPane.INFORMATION_MESSAGE);
        }

        VictoryScreen victoryScreen = new VictoryScreen(GameController.getSharedPoints(gamenum), this);
        victoryScreen.setVisible(true);
        GameHistoryController.createHistoryEntry(GameController.getGame(gamenum), GameResult.Victory);
        GameController.GameFinish(gamenum);
    }

    // --- Action Handlers ---

    private void handleFlagAction(Boolean isLeft, int row, int col, JButton[][] buttons) {
        if (GameController.IsCellFlagged(gamenum, isLeft, row, col)) {
            // Unflag
            GameController.UnFlaggedCell(gamenum, isLeft, row, col);
            String cellType = GameController.GetCellType(gamenum, isLeft, row, col);
            GameController.RevealCell(gamenum, isLeft, row, col);
            showCell(buttons[row][col], row, col, isLeft);
        } else {
            // Flag
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

    
    private void handleRevealAction(int row, int col, Boolean isLeft, JButton[][] buttons) {
        if (GameController.IsCellRevealed(gamenum, isLeft, row, col)) return;

        boolean wasMine = GameController.GetCellType(gamenum, isLeft, row, col).equals("MINE");
        GameController.PlayCascadeReveal(gamenum, isLeft, row, col);
        
        // Update UI for revealed cells - תוקן הבאג כאן!
        int SizeNum = GameController.getBoardSize(gamenum, isLeft);
        for (int r = 0; r < SizeNum; r++) {
            for (int c = 0; c < SizeNum; c++) {
                // תוקן: משתמש ב-r,c במקום row,col!
                if (GameController.GetCellType(gamenum, isLeft, r, c).equals("MINE") &&
                    GameController.IsCellRevealed(gamenum, isLeft, r, c) &&
                    !GameController.IsCellCounted(gamenum, isLeft, r, c)) {
                    GameController.decrementRemainingMinesInBoard(gamenum, isLeft);
                    GameController.setCountedAsCounted(gamenum, isLeft, r, c);
                }
                if (GameController.IsCellRevealed(gamenum, isLeft, r, c))
                    showCell(buttons[r][c], r, c, isLeft);
            }
        }

        if (wasMine) {
            GameController.UpdateSharedLivesGame(gamenum, GameController.getSharedLivesGame(gamenum) - 1);
            showTimedMessage("Boom! -1 Life", Color.red, buttons[row][col]);
        } else {
            GameController.UpdateSharedPoints(gamenum, 1);
            showTimedMessage("+1 Point", new Color(0, 200, 0), buttons[row][col]);
        }
    }

    // ============================================================
    // 5. SPECIAL CELLS LOGIC (SURPRISE / QUESTION)
    // ============================================================

    private boolean handleActionOfSurprise(int row, int col, Boolean isLeft, JButton[][] buttons) {
        boolean wasSurprise = GameController.GetCellType(gamenum, isLeft, row, col).equals("SURPRISE");
        
        if (wasSurprise && GameController.IsCellRevealed(gamenum, isLeft, row, col)) {
            if (!GameController.iscellUsed(gamenum, isLeft, row, col)) {
                int cost = GameController.GetGameSurpriseQuestionCoust(gamenum);
                int choice = JOptionPane.showConfirmDialog(this,
                    "Activating a Surprise costs " + cost + " points.\nDo you want to continue?",
                    "Surprise Cost", JOptionPane.OK_CANCEL_OPTION);

                if (choice != JOptionPane.OK_OPTION) return false;
            }

            String result = GameController.ActivateSurpriseCell(gamenum, isLeft, row, col);
            String[] parts = result.split(":");
            String type = parts[0];

            if (parts.length > 1) {
                int points = Integer.parseInt(parts[1]);
                String displayEmoji = GameController.getCellDisplay(gamenum, isLeft, row, col);
                Color msgColor = type.equals("GOOD") ? new Color(0, 200, 0) : Color.red;
                String msgText = type.equals("GOOD") ? "Surprise Good! +Life & +" : "Surprise Bad! -Life & -";
                
                showTimedMessage(msgText + points + " Points", msgColor, buttons[row][col]);
                updateScore(GameController.getSharedPoints(gamenum));
                setSharedHearts(GameController.getSharedLivesGame(gamenum));
                buttons[row][col].setIcon(new ImageIcon(renderEmojiToImage(displayEmoji, buttons[row][col].getWidth(), buttons[row][col].getHeight())));
                return true;
            } else if ("ALREADY_USED".equals(type)) {
                showTimedMessage("Already used this turn!", Color.gray, buttons[row][col]);
                return false;
            }
        }
        return true;
    }

    private void handleActionOfQuestion(int gameNumm, int row, int col, Boolean isLeft, JButton[][] buttons) {
        if (GameController.iscellUsed(gameNumm, isLeft, row, col)) {
            showTimedMessage("Already used this turn!", Color.gray, buttons[row][col]);
            GameController.setCanSwitch(false);
            return;
        }

        int cost = GameController.GetGameSurpriseQuestionCoust(gameNumm);
        int choice = JOptionPane.showConfirmDialog(this,
            "This question costs " + cost + " points to attempt.\nDo you want to proceed?",
            "Question Cost", JOptionPane.OK_CANCEL_OPTION);

        if (choice != JOptionPane.OK_OPTION) {
            GameController.setCanSwitch(false);
            return;
        }

        GameController.ActivateQuestion(gameNumm);
        updateScore(GameController.getSharedPoints(gamenum));

        if (!GameController.iscellUsed(gameNumm, isLeft, row, col)) {
            QuestionView view = new QuestionView(null, gameNumm, row, col, isLeft);
            view.setVisible(true);

            // Update after dialog
            String displayEmoji = GameController.getCellDisplay(gamenum, isLeft, row, col);
            updateScore(GameController.getSharedPoints(gamenum));
            setSharedHearts(GameController.getSharedLivesGame(gamenum));
            buttons[row][col].setIcon(new ImageIcon(renderEmojiToImage(displayEmoji, buttons[row][col].getWidth(), buttons[row][col].getHeight())));

            // Handle Question Rewards
            GameController.QuestionAction action = GameController.getPendingQuestionAction();
            if (action == GameController.QuestionAction.REVEAL_RANDOM_MINE) {
                int[] rc = GameController.revealRandomHiddenMine(gameNumm, isLeft);
                if (rc != null) {
                    String emoji = GameController.getCellDisplay(gameNumm, isLeft, rc[0], rc[1]);
                    buttons[rc[0]][rc[1]].setIcon(new ImageIcon(renderEmojiToImage(emoji, buttons[rc[0]][rc[1]].getWidth(), buttons[rc[0]][rc[1]].getHeight())));
                    buttons[rc[0]][rc[1]].setBackground(new Color(255, 180, 80));
                }
            } else if (action == GameController.QuestionAction.REVEAL_3X3) {
                ArrayList<int[]> cells = GameController.reveal3x3RandomGrid(gameNumm, isLeft);
                for (int[] cell : cells) {
                    showCell(buttons[cell[0]][cell[1]], cell[0], cell[1], isLeft);
                }
            }
            GameController.clearPendingQuestionAction();
        }
    }

    private boolean CheckCanSwitch(int row, int col, Boolean isLeft, JButton[][] buttons) {
        return GameController.isCanSwitch();
    }

    // ============================================================
    // 6. MENUS & SETTINGS
    // ============================================================

    private void showSettingsMenu() {
        JPopupMenu settingsMenu = new JPopupMenu();
        settingsMenu.setBackground(new Color(60, 0, 90, 230));
        settingsMenu.setBorder(BorderFactory.createLineBorder(new Color(246, 230, 138), 2));

        JMenuItem volumeItem = createMenuItem("🔊 Volume Control", e -> showVolumeControl());
        JMenuItem restartItem = createMenuItem("🔄 Restart Game", e -> restartGame());
        JMenuItem stopItem = createMenuItem("⏹ Stop Game", e -> stopGame());

        settingsMenu.add(volumeItem);
        settingsMenu.addSeparator();
        settingsMenu.add(restartItem);
        settingsMenu.add(stopItem);

        settingsMenu.show(settingsLabel, 0, settingsLabel.getHeight());
    }

    private JMenuItem createMenuItem(String title, java.awt.event.ActionListener action) {
        JMenuItem item = new JMenuItem(title);
        item.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        item.setForeground(new Color(246, 230, 138));
        item.setBackground(new Color(60, 0, 90));
        item.addActionListener(action);
        return item;
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
        musicLabel.setText(musicManager.isPlaying() ? "♪" : "🔇");
        musicLabel.setForeground(musicManager.isPlaying() ? new Color(246, 230, 138) : new Color(180, 180, 180));
    }
 // ============================================================
    // 7. VIEW UPDATES
    // ============================================================

    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void updateLeftMines(int remaining) {
        leftMinesLabel.setText("Remaining Mines: " + remaining);
    }

    public void updateRightMines(int remaining) {
        rightMinesLabel.setText("Remaining Mines: " + remaining);
    }

    public void setSharedHearts(int lives) {
        heartsPanel.removeAll();
        int displayLives = Math.min(lives, 10);
        for (int i = 0; i < displayLives; i++) heartsPanel.add(new JLabel(heartIcon));
        heartsPanel.revalidate();
        heartsPanel.repaint();
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

    private void showCell(JButton button, int r, int c, Boolean isLeft) {
        if (!GameController.IsCellRevealed(gamenum, isLeft, r, c)) return;

        String displayEmoji = GameController.getCellDisplay(gamenum, isLeft, r, c);
        String cellType = GameController.GetCellType(gamenum, isLeft, r, c);
        
        button.setIcon(new ImageIcon(renderEmojiToImage(displayEmoji, button.getWidth(), button.getHeight())));

        // Set Background Colors
        if (cellType.equals("EMPTY")) button.setBackground(new Color(200, 200, 255));
        else if (cellType.equals("NUMBER")) button.setBackground(new Color(180, 255, 180));
        else if (cellType.equals("MINE")) button.setBackground(new Color(255, 180, 80));
        else if (cellType.equals("SURPRISE")) button.setBackground(Color.yellow);
        else if (cellType.equals("QUESTION")) button.setBackground(new Color(255, 180, 255));

        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setEnabled(false);
    }
    
    /**
     * תוקן: מחשב בונוס לפי activationCost ומעדכן את הניקוד
     */
    private int convertRemainingLivesToPoints() {
        int lives = GameController.getSharedLivesGame(gamenum);
        int activationCost = GameController.GetGameSurpriseQuestionCoust(gamenum);
        int bonus = lives * activationCost;
        GameController.UpdateSharedPoints(gamenum, bonus);
        return bonus;
    }
    
    private void revealAllCells() {
         int size = rows;
         for(int r=0; r<size; r++){
             for(int c=0; c<size; c++){
                 showCell(leftBoard[r][c], r, c, true);
                 showCell(rightBoard[r][c], r, c, false);
             }
         }
    }

    // ============================================================
    // 8. HELPERS & UTILITIES
    // ============================================================

    private void loadHeartImage() {
        heartIcon = new ImageIcon(getClass().getResource("/resource/NewHeartImage.png"));
        Image img = heartIcon.getImage();
        Image scaledImg = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        heartIcon = new ImageIcon(scaledImg);
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

    private void showTimedMessage(String message, Color color, JButton button) {
        JComponent msg = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
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
                return new Dimension(fm.stringWidth(message) + 20, fm.getHeight() + 20);
            }
        };

        msg.setOpaque(false);
        JWindow popup = new JWindow();
        popup.setBackground(new Color(0, 0, 0, 0));
        popup.add(msg);
        popup.pack();

        Point btnOnScreen = button.getLocationOnScreen();
        int x = btnOnScreen.x + button.getWidth()/2 - popup.getWidth()/2;
        int y = btnOnScreen.y - popup.getHeight() - 5;
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (x < 0) x = 10;
        else if (x + popup.getWidth() > screenSize.width) x = screenSize.width - popup.getWidth() - 10;
        
        popup.setLocation(x, y);
        popup.setVisible(true);

        Timer timer = new Timer(1500, e -> popup.dispose());
        timer.setRepeats(false);
        timer.start();
    }

    // --- Inner Class: RoundedPanel ---
    static class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color backgroundColor, borderColor;

        public RoundedPanel(int radius) { this(radius, Color.white); }
        public RoundedPanel(int radius, Color bgColor) {
            cornerRadius = radius;
            backgroundColor = bgColor;
            setOpaque(false);
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
}