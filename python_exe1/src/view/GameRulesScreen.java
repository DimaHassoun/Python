package view;

import javax.swing.*;
import java.awt.*;

public class GameRulesScreen extends JFrame {

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

                // OBJECTIVE
                + card("🎯 Objective",
                "Reveal as many safe cells as possible, avoid losing all hearts, and achieve the highest score.<br>"
                + "Each player has their own board and plays in turns.")

             // BOARD STRUCTURE
                + card("📐 Board Structure",
                    "Each board contains:<br>"
                    + "<ul>"
                    + "<li>Mines</li>"
                    + "<li>Number cells</li>"
                    + "<li>Empty cells</li>"
                    + "<li>Question cells</li>"
                    + "<li>Surprise cells</li>"
                    + "</ul><br>"

                    + "Each difficulty level has different board dimensions and cell distribution:<br>"

                    + "<ul>"
                    + "<li><b>Easy</b> — 9 × 9 (81 cells)<br>"
                    + "<span style='font-size:14px;'>10 Mines • 6 Questions • 2 Surprises • 10 Hearts • Good Surprise: +1 • Bad Surprise: −1</span></li><br>"

                    + "<li><b>Medium</b> — 13 × 13 (169 cells)<br>"
                    + "<span style='font-size:14px;'>26 Mines • 7 Questions • 3 Surprises • 8 Hearts • Good Surprise: +1 • Bad Surprise: −1</span></li><br>"

                    + "<li><b>Hard</b> — 16 × 16 (256 cells)<br>"
                    + "<span style='font-size:14px;'>44 Mines • 11 Questions • 4 Surprises • 6 Hearts • Good Surprise: +1 • Bad Surprise: −1</span></li>"
                    + "</ul>"
                )


                // CELL TYPES
                + card("💣 Cell Types",
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
                + "<li>Triggers cascading auto-reveal.</li>"
                + "</ul>"

                + "<b>4️ Surprise Cell</b><br>"
                + "<ul>"
                + "<li>Acts like empty cell until activated.</li>"
                + "<li>50% good / 50% bad surprise.</li>"
                + "<li>One-time use.</li>"
                + "</ul>"

                + "<b>5 Question Cell</b><br>"
                + "<ul>"
                + "<li>Multiple-choice question (4 answers).</li>"
                + "<li>4 difficulty levels.</li>"
                + "<li>Correct/incorrect answers change hearts & points.</li>"
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
                + "<li>Hearts reach 0</li>"
                + "<li>A player reveals all mines on the board</li>"
                + "</ul>"
                + "Remaining hearts convert into points.")

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
