package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class to manage window size across all screens
 */
public class WindowSizeManager {
    
    private static WindowSizeManager instance;
    private Dimension windowSize;
    private int extendedState = Frame.NORMAL; // Track maximized state
    private List<WindowSizeListener> listeners = new ArrayList<>();
    
    // Default size
    private static final Dimension DEFAULT_SIZE = new Dimension(1200, 750);
    
    private WindowSizeManager() {
        windowSize = DEFAULT_SIZE;
    }
    
    /**
     * Get the singleton instance
     */
    public static WindowSizeManager getInstance() {
        if (instance == null) {
            instance = new WindowSizeManager();
        }
        return instance;
    }
    
    /**
     * Add a listener to be notified of window size changes
     */
    public void addWindowSizeListener(WindowSizeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Remove a listener
     */
    public void removeWindowSizeListener(WindowSizeListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notify all listeners of size changes
     */
    private void notifyListeners() {
        for (WindowSizeListener listener : listeners) {
            listener.onWindowSizeChanged(windowSize, extendedState);
        }
    }
    
    /**
     * Update the window size
     */
    public void setWindowSize(Dimension size) {
        if (size != null && (size.width != windowSize.width || size.height != windowSize.height)) {
            this.windowSize = new Dimension(size);
            notifyListeners();
        }
    }
    
    /**
     * Update the extended state (NORMAL, MAXIMIZED, etc.)
     */
    public void setExtendedState(int state) {
        if (this.extendedState != state) {
            this.extendedState = state;
            notifyListeners();
        }
    }
    
    /**
     * Get current window size
     */
    public Dimension getWindowSize() {
        return new Dimension(windowSize);
    }
    
    /**
     * Get current extended state
     */
    public int getExtendedState() {
        return extendedState;
    }
    
    /**
     * Check if window is maximized
     */
    public boolean isMaximized() {
        return extendedState == Frame.MAXIMIZED_BOTH;
    }
    
    /**
     * Apply the saved window size and state to a JFrame
     */
    public void applyToFrame(JFrame frame) {
        frame.setSize(getWindowSize());
        frame.setExtendedState(getExtendedState());
        
        // Add listeners to track changes
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Only update if not maximized (to preserve actual size)
                if (frame.getExtendedState() == Frame.NORMAL) {
                    setWindowSize(frame.getSize());
                }
            }
        });
        
        frame.addWindowStateListener(new WindowAdapter() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                setExtendedState(e.getNewState());
                // Update size when returning from maximized
                if (e.getNewState() == Frame.NORMAL) {
                    setWindowSize(frame.getSize());
                }
            }
        });
    }
    
    /**
     * Interface for listening to window size changes
     */
    public interface WindowSizeListener {
        void onWindowSizeChanged(Dimension newSize, int extendedState);
    }
}