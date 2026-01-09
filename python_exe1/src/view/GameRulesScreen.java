package view;

import javax.swing.*;
import java.awt.*;

/**
 * GameRulesScreen displays the official game rules to the player.
 *
 * This screen presents a styled, scrollable HTML-based explanation of:
 * - Game objective
 * - How to play
 * - Board structure and difficulty levels
 * - Cell types and their effects
 * - Turn system
 * - End game conditions
 */

public class GameRulesScreen extends JFrame {
	
	/**
	 * Constructs the Game Rules screen.
	 *
	 * Responsibilities:
	 * - Initializes the window properties (title, size, position)
	 * - Builds the main layout using BorderLayout
	 * - Creates and styles the title label
	 * - Generates rich HTML content describing the game rules
	 * - Displays the rules inside a scrollable JEditorPane
	 * - Adds a Back button that closes the screen
	 */

    public GameRulesScreen() {

        setTitle("Game Rules");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(28, 0, 48)); // Dark purple
        add(panel);

        // Title
        JLabel title = new JLabel("Game Rules", SwingConstants.CENTER);
        title.setFont(new Font("Verdana", Font.BOLD, 40));
        title.setForeground(new Color(246, 230, 138));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 20, 0));
        panel.add(title, BorderLayout.NORTH);

        // HTML content inside JEditorPane
        JEditorPane htmlPane = new JEditorPane();
        htmlPane.setContentType("text/html");
        htmlPane.setEditable(false);

        String rulesHTML =
                "<html>" +
                "<body style='background-color:#1C0030; color:#F6E68A; font-family:Verdana; "
                + "font-size:16px; padding:20px;'>"

                + "<div style='width:90%; margin:auto;'>"
                
               
                
                + card("🎯 MineSweeper",
                "Two players take turns revealing cells on separate boards with the goal of uncovering all mines and "
                + "achieving the highest score, "
                + "while using a shared pool of hearts and points.")
                
                + card("🕹️ How to play:<br>",
                        "1.Enter both players’ names.<br>"
                        + "2. Choose the game difficulty (Easy / Medium / Hard).<br>"
                        + "3.Click the START NEW GAME button to begin.<br>"
                        + "4.The game starts with 0 points and a shared number of hearts based on the selected difficulty.<br>"
                        + "5.On your turn, choose <b>one</b> action: reveal a cell or place a flag 🚩 (right-click).<br>"
                        + "6. Every action ends the turn and passes it to the other player.<br>"
                        +"<br><br>"
                        + "<i>Below are the game rules and explanations of each cell type and how the score calculate.</i>"
                    )
                

             // BOARD STRUCTURE
                + card("📐 Board Structure",
                    "Each board contains the following cell types:<br>"
                    + "<ul>"
                    + "<li>Mines cells</li>"
                    + "<li>Number cells</li>"
                    + "<li>Empty cells</li>"
                    + "<li>Question cells</li>"
                    + "<li>Surprise cells</li>"
                    + "</ul><br>"
                    
                    +"<b>🚩 Using a flag:</b>"+"<br>"
                    +"Using a flag lets you mark suspected mines on your board (right-click to place a flag),"
                    +" affecting score and ending your turn: correct flags earn points, while incorrect flags incur penalties.<br>"
                    +"If the cell is flagged, the only allowed action is to unflag it, and the content will be automatically uncovered."
                    + "<br>"
                    +"<br>"
                    
                    + "Each difficulty level has different board dimensions and cell distribution:<br>"

                    + "<ul>"
                    + "<li><b>Easy</b> — 9 × 9 (81 cells)<br>"
                    + "<span style='font-size:14px;'>10 Mines • 6 Questions • 2 Surprises • ❤️ 10 Hearts • Good Surprise: +8 pts & +1❤️  • Bad Surprise: -8 pts & -1❤️</span></li><br>"

                    + "<li><b>Medium</b> — 13 × 13 (169 cells)<br>"
                    + "<span style='font-size:14px;'>26 Mines • 7 Questions • 3 Surprises • ❤️ 8 Hearts • Good Surprise: +12 pts & +1❤️  • Bad Surprise: -12 pts & -1❤️</span></li><br>"

                    + "<li><b>Hard</b> — 16 × 16 (256 cells)<br>"
                    + "<span style='font-size:14px;'>44 Mines • 11 Questions • 4 Surprises • ❤️ 6 Hearts • Good Surprise: +16 pts & +1❤️  • Bad Surprise: -16 pts & -1❤️</span></li>"
                    + "</ul>"
                    + "<p>Maximum hearts: 10 ❤️. Extra hearts are automatically converted to points.</p>"
                )


                // CELL TYPES
                + card(" Cell Types",
                "<b>1️ Mine Cell</b><br>"
                + "<ul>"
                + "<li>Revealing a mine causes heart loss.</li>"
                + "<li>Correct flag grants points.</li>"
                + "<li>Mines revealed by questions do NOT give points.</li>"
                + "</ul>"

                + "<b>2️ Number Cell</b><br>"
                + "<ul>"
                + "<li>Shows number of nearby mines.</li>"
                + "<li>Reveal: +1 point</li>"
                + "<li>Wrong flag: −3 points</li>"
                + "</ul>"

                + "<b>3️ Empty Cell</b><br>"
                + "<ul>"
                + "<li>No adjacent mines.</li>"
                + "<li>Triggers cascading auto-reveal of connected empty cells and surrounding numbers.</li>"
                + "</ul>"

                + "<b>4️ Surprise Cell</b><br>"
                + "<ul>"
                + "<li>Can be activated only once.</li>"
                + "<li>50% chance: Good (gain heart & points) / Bad (lose heart & points)</li>"
                + "</ul>"

                + "<b>5 Question Cell</b><br>"
                + "<ul>"
                + "<li>Multiple-choice question (4 answers).</li>"
                + "<li>4 difficulty levels.</li>"
                + "<li>Correct answer: gain points, hearts, or reveal a mine.</li>"
                + "<li>Wrong answer: lose points or hearts</li>"
                + "</ul>"
                )

                // TURN SYSTEM
                + card("🎮 Turn System",
                "Players take turns.<br>"
                + "Any action (reveal, flag, activate) ends the turn.<br>"
                + "Hearts and points start shared based on difficulty.")

             // END GAME 
                + card("🏁 End of Game",
                    "The game ends when:<br>"
                    + "<ul>"
                    + "<li>Hearts reach 0 ❤️ </li>"
                    + "<li>A player reveals all mines on the board</li>"
                    + "<li>Player reaches the negative score limit by difficulty:</li>"
                    + "<ul>"
                    + "<li><b>Easy:</b> -30 points</li>"
                    + "<li><b>Medium:</b> -40 points</li>"
                    + "<li><b>Hard:</b> -60 points</li>"
                    + "</ul>"
                    + "</ul>"
                    + "Remaining hearts convert into points.<br>"
                   
                    
                )

                + "</div>"
                + "</body></html>";

        htmlPane.setText(rulesHTML);

        JScrollPane scroll = new JScrollPane(htmlPane);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        panel.add(scroll, BorderLayout.CENTER);

        // Back button
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Verdana", Font.BOLD, 22));
        backBtn.setForeground(new Color(246, 230, 138));
        backBtn.setBackground(new Color(80, 0, 120));
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(28, 0, 48));
        bottom.add(backBtn);

        backBtn.addActionListener(e -> dispose());

        panel.add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Helper to generate consistent card boxes
    private static String card(String title, String text) {
        return "<div style='border:2px solid #A37C00; border-radius:12px; padding:15px; "
                + "margin-bottom:20px; background-color:rgba(80,0,120,0.25); "
                + "box-shadow:0 0 15px rgba(243,220,107,0.35);'>"
                + "<h2 style='margin:0; font-size:24px; color:#F6E68A; "
                + "text-shadow:0 0 6px #E8D37A;'>" + title + "</h2>"
                + "<p style='margin-top:8px;'>" + text + "</p>"
                + "</div>";
    }
}
