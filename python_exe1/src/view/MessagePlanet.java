package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSlider;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
/**
 * Utility class providing customized, styled message dialogs and popups for the UI.
 * 
 * This class centralizes the creation of consistent, modern-looking notification and dialog
 * windows with custom rounded borders, semi-transparent backgrounds, and styled buttons.
 * 
 * It supports both non-blocking popups and blocking modal dialogs, including:
 * <ul>
 *   <li>Non-blocking informational popups that stack vertically without interrupting user flow</li>
 *   <li>Blocking message dialogs with a single OK button</li>
 *   <li>Blocking confirmation dialogs with customizable OK and Cancel buttons</li>
 *   <li>Password input dialogs with a toggleable visibility (eye icon)</li>
 *   <li>Volume control dialog with a custom styled slider</li>
 * </ul>
 * 
 * All dialogs use consistent styling with rounded corners, colored borders, and transparent backgrounds.
 */
public class MessagePlanet {
	/**
     * Displays a non-blocking popup message that automatically disappears after a given duration.
     * 
     * The popup uses a semi-transparent black background with a colored rounded border.
     * Multiple popups stack vertically starting at ~40% from the top of the screen, centered horizontally.
     * This method is useful for showing brief notifications without interrupting user actions.
     * 
     *  message          The message text to display. Newlines ("\n") are supported.
     *  color            The color used for the rounded border around the popup.
     *  duration         How long (in milliseconds) the popup remains visible before closing automatically.
     *  delayBeforeShow  Delay (in milliseconds) before the popup is displayed after this method is called.
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

	/**
     * Shows a blocking modal message dialog with a single OK button.
     * 
     * The dialog uses the same styling as the non-blocking popup: rounded corners,
     * semi-transparent black background, and a colored border. It blocks until the user
     * clicks OK.
     * 
     *  parent       The parent component used to center the dialog on screen. Can be null.
     *  message      The message text to display. Supports multi-line text via "\n".
     *  borderColor  The color used for the dialog’s rounded border and buttons.
     *  title        The title displayed in the dialog window's title bar.
     *  ButtonText   The text to display on the OK button.
     */
	public static void showBlockingMessageDialog(Component parent, String message, Color borderColor, String title, String ButtonText) {
		// Create a modal dialog
		JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), title, Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setUndecorated(true); // no default window borders
		dialog.setBackground(new Color(0, 0, 0, 0));

		// Panel with rounded corners and custom painting
		JPanel panel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// Semi-transparent black background
				g2.setColor(new Color(0, 0, 0, 200));
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

				// Border in specified color
				g2.setColor(borderColor);
				g2.setStroke(new BasicStroke(3));
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

				g2.dispose();
			}
		};
		panel.setLayout(new BorderLayout());
		panel.setOpaque(false);

		// Label for message, center aligned with HTML formatting
		JLabel label = new JLabel("<html><div style='text-align: center; font-family: Arial; font-weight: bold; font-size: 12px; color: white;'>"
				+ message.replace("\n", "<br>") + "</div></html>");
		label.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

		JButton okButton = new JButton(ButtonText) {
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
				});
			}

			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// Background color changes on hover
				Color bgColor = isHovered ? borderColor.darker() : borderColor;
				g2.setColor(bgColor);

				// Fill rounded rect as background
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

				// Border color changes on hover (lighter/darker)
				Color bColor = isHovered ? borderColor.brighter() : borderColor.darker();
				g2.setColor(bColor);
				g2.setStroke(new BasicStroke(2));
				g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 30, 30);

				g2.dispose();

				super.paintComponent(g);
			}
		};

		okButton.setFont(new Font("Arial", Font.BOLD, 16));
		okButton.setForeground(Color.WHITE);

		// Important so default painting doesn’t interfere with our custom paint
		okButton.setFocusPainted(false);
		okButton.setContentAreaFilled(false);
		okButton.setBorderPainted(false);
		okButton.setOpaque(false);
		okButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		// Add listener to close dialog when clicked
		okButton.addActionListener(e -> dialog.dispose());

		// Button panel with some padding and centered
		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.add(okButton);

		panel.add(label, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.SOUTH);

		dialog.add(panel);
		dialog.pack();

		// Position dialog centered near 40% from top (like your popup)
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width - dialog.getWidth()) / 2;
		int y = (int) (screenSize.height * 0.40) - (dialog.getHeight() / 2);
		dialog.setLocation(x, y);

		dialog.setVisible(true); // blocking call until dialog is closed
	}
	/**
     * Displays a blocking confirmation dialog with customizable OK and Cancel buttons.
     * 
     * The dialog is styled consistently with rounded corners, semi-transparent background,
     * and colored borders/buttons. It returns true if the user presses OK, or false if Cancel.
     * 
     *  parent           The parent component for dialog centering.
     *  message          The message text to display. Supports "\n" for new lines.
     *  borderColor      The border and OK button color.
     *  title            The title of the dialog window.
     *  okButtonText     The text for the OK button.
     *  cancelButtonText The text for the Cancel button.
     * return                 true if the user confirms (OK), false if cancelled.
     */
	public static boolean showConfirmDialog(Component parent,String message,Color borderColor,String title,String okButtonText,String cancelButtonText) {
		final boolean[] confirmed = { false };

		JDialog dialog = new JDialog(
				SwingUtilities.getWindowAncestor(parent),
				title,
				Dialog.ModalityType.APPLICATION_MODAL
				);
		dialog.setUndecorated(true);
		dialog.setBackground(new Color(0, 0, 0, 0));

		JPanel panel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// Background
				g2.setColor(new Color(0, 0, 0, 200));
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

				// Border
				g2.setColor(borderColor);
				g2.setStroke(new BasicStroke(3));
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

				g2.dispose();
			}
		};
		panel.setLayout(new BorderLayout());
		panel.setOpaque(false);

		JLabel label = new JLabel(
				"<html><div style='text-align:center; font-family:Arial; font-size:14px; font-weight:bold; color:white;'>"
						+ message.replace("\n", "<br>")
						+ "</div></html>"
				);
		label.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

		// ---- OK BUTTON ----
		JButton okButton = createDialogButton(okButtonText, borderColor);
		okButton.addActionListener(e -> {
			confirmed[0] = true;
			dialog.dispose();
		});

		// ---- CANCEL BUTTON ----
		JButton cancelButton = createDialogButton(cancelButtonText, Color.GRAY);
		cancelButton.addActionListener(e -> dialog.dispose());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		panel.add(label, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.SOUTH);

		dialog.add(panel);
		dialog.pack();

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screen.width - dialog.getWidth()) / 2;
		int y = (int) (screen.height * 0.40) - (dialog.getHeight() / 2);
		dialog.setLocation(x, y);

		dialog.setVisible(true); // BLOCKS

		return confirmed[0];
	}
	 /**
     * Creates a custom styled JButton with rounded corners and color changes on hover.
     * 
     * This method centralizes button styling used across dialogs.
     * 
     *  text   The text displayed on the button.
     *  color  The base color of the button background and border.
     * return       A JButton instance styled with rounded corners and hover effects.
     */
	private static JButton createDialogButton(String text, Color color) {
		JButton button = new JButton(text) {
			private boolean hover = false;

			{
				addMouseListener(new java.awt.event.MouseAdapter() {
					@Override public void mouseEntered(java.awt.event.MouseEvent e) {
						hover = true;
						repaint();
					}
					@Override public void mouseExited(java.awt.event.MouseEvent e) {
						hover = false;
						repaint();
					}
				});
			}

			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2.setColor(hover ? color.darker() : color);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

				g2.setColor(color.brighter());
				g2.setStroke(new BasicStroke(2));
				g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 30, 30);

				g2.dispose();
				super.paintComponent(g);
			}
		};

		button.setFont(new Font("Arial", Font.BOLD, 14));
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setOpaque(false);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setPreferredSize(new Dimension(110, 40));

		return button;
	}
	/**
     * Displays a blocking password input dialog with an eye icon toggle to show/hide the password.
     * 
     * The dialog is styled with rounded corners and semi-transparent black background.
     * Returns the password entered as a String if OK is pressed, or null if cancelled.
     * 
     *  parent       The parent component for centering the dialog.
     *  title        The title for the dialog window.
     *  borderColor  The color of the dialog’s rounded border and buttons.
     * return             The entered password as a String, or null if the user cancels.
     */
	public static String showPasswordDialog(Component parent, String title, Color borderColor) {
		final JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), title, Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setUndecorated(true);
		dialog.setBackground(new Color(0, 0, 0, 0));

		JPanel panel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2.setColor(new Color(0, 0, 0, 200));
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

				g2.setColor(borderColor);
				g2.setStroke(new BasicStroke(3));
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

				g2.dispose();
			}
		};
		panel.setLayout(new BorderLayout(10, 10));
		panel.setOpaque(false);

		JLabel promptLabel = new JLabel("Please enter Admin Password:");
		promptLabel.setForeground(Color.WHITE);
		promptLabel.setFont(new Font("Arial", Font.BOLD, 16));
		promptLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));

		JPasswordField passwordField = new JPasswordField(15);
		passwordField.setEchoChar('•');
		passwordField.setFont(new Font("Arial", Font.PLAIN, 16));

		JLabel eyeLabel = new JLabel("👀");
		eyeLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
		eyeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		eyeLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		eyeLabel.setForeground(Color.WHITE);

		JPanel passPanel = new JPanel(new BorderLayout());
		passPanel.add(passwordField, BorderLayout.CENTER);
		passPanel.add(eyeLabel, BorderLayout.EAST);
		passPanel.setOpaque(false);
		passPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

		// Eye toggle for password visibility
		eyeLabel.addMouseListener(new MouseAdapter() {
			private boolean visible = false;

			@Override
			public void mouseClicked(MouseEvent e) {
				visible = !visible;
				if (visible) {
					passwordField.setEchoChar((char) 0);
					eyeLabel.setText("🙈");
				} else {
					passwordField.setEchoChar('•');
					eyeLabel.setText("👀");
				}
			}
		});

		// Buttons panel
		JButton okButton = createDialogButton("OK", borderColor);
		JButton cancelButton = createDialogButton("Cancel", Color.GRAY);

		final String[] result = { null }; // To hold password result

		okButton.addActionListener(e -> {
			char[] passChars = passwordField.getPassword();
			result[0] = new String(passChars);
			java.util.Arrays.fill(passChars, '0'); // clear for security
			dialog.dispose();
		});

		cancelButton.addActionListener(e -> {
			result[0] = null;
			dialog.dispose();
		});

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setOpaque(false);
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);

		panel.add(promptLabel, BorderLayout.NORTH);
		panel.add(passPanel, BorderLayout.CENTER);
		panel.add(buttonsPanel, BorderLayout.SOUTH);

		dialog.add(panel);
		dialog.pack();

		// Position near 40% from top, centered horizontally
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width - dialog.getWidth()) / 2;
		int y = (int) (screenSize.height * 0.40) - (dialog.getHeight() / 2);
		dialog.setLocation(x, y);

		dialog.setVisible(true); // BLOCKING until closed

		return result[0];
	}
	 /**
     * Shows a blocking volume control dialog with a custom styled slider matching the dialog theme.
     * 
     * The slider updates the provided MusicManager's volume in real-time as it is adjusted.
     * The dialog blocks until the user clicks OK.
     * 
     *  parent        Parent component for dialog centering (can be null).
     *  musicManager  An object exposing methods getVolumePercent() and setVolume(float) to manage volume.
     *  borderColor   The color used for the dialog border, slider thumb, and buttons.
     */
	public static void showVolumeControlDialog(Component parent, MusicManager musicManager, Color borderColor) {
		JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Volume Control", Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setUndecorated(true);
		dialog.setBackground(new Color(0, 0, 0, 0));

		JPanel panel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2.setColor(new Color(0, 0, 0, 200));
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

				g2.setColor(borderColor);
				g2.setStroke(new BasicStroke(3));
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

				g2.dispose();
			}
		};
		panel.setLayout(new BorderLayout(10, 10));
		panel.setOpaque(false);
		panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		JLabel volumeLabel = new JLabel("Volume: " + musicManager.getVolumePercent() + "%", JLabel.CENTER);
		volumeLabel.setFont(new Font("Arial", Font.BOLD, 16));
		volumeLabel.setForeground(Color.WHITE);

		JSlider volumeSlider = new JSlider(0, 100, musicManager.getVolumePercent());
		volumeSlider.setMajorTickSpacing(25);
		volumeSlider.setMinorTickSpacing(5);
		volumeSlider.setPaintTicks(true);
		volumeSlider.setPaintLabels(true);

		// Custom slider UI to paint thumb and track with your colors
		volumeSlider.setUI(new javax.swing.plaf.metal.MetalSliderUI() {
			@Override
			public void paintThumb(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2.setColor(borderColor); // Use borderColor for the thumb fill
				g2.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);

				g2.setColor(Color.WHITE); // White border for thumb
				g2.setStroke(new BasicStroke(2));
				g2.drawOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);

				g2.dispose();
			}

			@Override
			public void paintTrack(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// Draw track background (dark gray, semi-transparent)
				g2.setColor(new Color(50, 50, 50, 150));
				g2.fillRoundRect(trackRect.x, trackRect.y + trackRect.height / 3, trackRect.width, trackRect.height / 3, 10, 10);

				// Draw progress portion (from left to thumb center)
				int progressWidth = thumbRect.x + thumbRect.width / 2 - trackRect.x;
				g2.setColor(borderColor);
				g2.fillRoundRect(trackRect.x, trackRect.y + trackRect.height / 3, progressWidth, trackRect.height / 3, 10, 10);

				g2.dispose();
			}
		});

		volumeSlider.setFont(new Font("Arial", Font.BOLD, 12));
		volumeSlider.setForeground(Color.WHITE);
		volumeSlider.setOpaque(false);

		volumeSlider.addChangeListener(e -> {
			int value = volumeSlider.getValue();
			musicManager.setVolume(value / 100.0f);
			volumeLabel.setText("Volume: " + value + "%");
		});

		panel.add(volumeLabel, BorderLayout.NORTH);
		panel.add(volumeSlider, BorderLayout.CENTER);

		JButton okButton = createDialogButton("OK", borderColor);
		okButton.addActionListener(e -> dialog.dispose());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.add(okButton);

		panel.add(buttonPanel, BorderLayout.SOUTH);

		dialog.add(panel);
		dialog.pack();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width - dialog.getWidth()) / 2;
		int y = (int) (screenSize.height * 0.40) - (dialog.getHeight() / 2);
		dialog.setLocation(x, y);

		dialog.setVisible(true);
	}

}
