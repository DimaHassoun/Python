package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

public class NonBlockingMessage {
	/**
	 * Displays a non-blocking popup message that stacks vertically on the screen.
	 * 
	 * This popup shows informational messages without blocking user interaction,
	 * unlike JOptionPane dialogs which require user confirmation.
	 * 
	 * It is designed to be reusable across many view classes in the MVC pattern,
	 * centralizing the popup message display logic in one place for consistency.
	 * 
	 * Usage:
	 * Call this method with the message text, border color, display duration (ms), 
	 * and an optional delay before showing the popup (ms).
	 * 
	 * The popup automatically positions itself centered horizontally, 
	 * approximately 40% from the top of the screen, and stacks vertically 
	 * if multiple popups are shown simultaneously.
	 */

 	public static void showNonBlockingMessage(String message, Color color, int duration, int delayBeforeShow) {
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
}
