package view;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class to manage background music playback across all screens
 */
public class MusicManager {
    
    private static MusicManager instance;
    private Clip clip;
    private boolean isPlaying;
    private boolean isMuted;
    private String currentMusicFile;
    private float volume = 0.6f; // Default volume (0.0 to 1.0) - 60%
    private FloatControl volumeControl;
    private List<MusicStateListener> listeners = new ArrayList<>();
    // Private constructor for singleton
    private MusicManager() {
        isPlaying = false;
        isMuted = false;
    }
    
    /**
     * Get the singleton instance
     */
    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }
    
    /**
     * Add a listener to be notified of music state changes
     */
    public void addMusicStateListener(MusicStateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Remove a listener
     */
    public void removeMusicStateListener(MusicStateListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notify all listeners of state changes
     */
    private void notifyListeners() {
        for (MusicStateListener listener : listeners) {
            listener.onMusicStateChanged();
        }
    }
    
    
    /**
     * Play background music from a file
     * @param musicFilePath Path to the music file (WAV format)
     */
    public void playMusic(String musicFilePath) {
        try {
            // Stop current music if playing
            if (clip != null && clip.isRunning()) {
                clip.stop();
                clip.close();
            }
            
            // Load the audio file
            File musicFile = new File(musicFilePath);
            if (!musicFile.exists()) {
                System.err.println("Music file not found: " + musicFilePath);
                System.err.println("Looking in: " + musicFile.getAbsolutePath());
                return;
            }
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            
            // Get volume control
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                setVolume(volume);
            }
            
            // Loop continuously
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
            
            isPlaying = true;
            isMuted = false;
            currentMusicFile = musicFilePath;
            
            System.out.println("✓ Music started: " + musicFilePath);
            System.out.println("✓ Volume: " + (int)(volume * 100) + "%");
            
        } catch (UnsupportedAudioFileException e) {
            System.err.println("✗— Unsupported audio format. Please use WAV files.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("✗— Error reading music file: " + e.getMessage());
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("✗— Audio line unavailable: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Stop the currently playing music
     */
    public void stopMusic() {
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.close();
            isPlaying = false;
            System.out.println("✓ Music stopped");
        }
    }
    
    /**
     * Toggle music on/off
     */
    public void toggleMusic() {
        if (isPlaying) {
            pauseMusic();
        } else {
            resumeMusic();
        }
    }
    
    /**
     * Pause the music
     */
    public void pauseMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            isPlaying = false;
            System.out.println("⏸ Music paused");
        }
    }
    
    /**
     * Resume the music
     */
    public void resumeMusic() {
        if (clip != null && !clip.isRunning()) {
            clip.start();
            isPlaying = true;
            System.out.println("▶ Music resumed");
        } else if (currentMusicFile != null && clip == null) {
            // If clip was closed, reload and play
            playMusic(currentMusicFile);
        }
    }
    
    /**
     * Mute/Unmute the music
     */
    public void toggleMute() {
        if (isMuted) {
            unmute();
        } else {
            mute();
        }
    }
    
    /**
     * Mute the music
     */
    public void mute() {
        if (volumeControl != null) {
            volumeControl.setValue(volumeControl.getMinimum());
            isMuted = true;
            System.out.println("🔇 Music muted");
        }
    }
    
    /**
     * Unmute the music
     */
    public void unmute() {
        if (volumeControl != null) {
            setVolume(volume);
            isMuted = false;
            System.out.println("🔊 Music unmuted");
        }
    }
    
    /**
     * Set the volume (0.0 to 1.0)
     */
    public void setVolume(float newVolume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, newVolume)); // Clamp between 0 and 1
        
        if (volumeControl != null) {
            try {
                float min = volumeControl.getMinimum();
                float max = volumeControl.getMaximum();
                
                // Convert linear volume (0.0-1.0) to decibels
                // Using logarithmic scale for better volume perception
                float dB;
                if (this.volume == 0.0f) {
                    dB = min;
                } else {
                    dB = min + (max - min) * this.volume;
                }
                
                volumeControl.setValue(dB);
                isMuted = false;
                System.out.println("🔊 Volume set to: " + (int)(this.volume * 100) + "%");
                
            } catch (Exception e) {
                System.err.println("Could not set volume: " + e.getMessage());
            }
        }
    }
    
    /**
     * Increase volume by 10%
     */
    public void increaseVolume() {
        float newVolume = volume + 0.1f;
        setVolume(newVolume);
    }
    
    /**
     * Decrease volume by 10%
     */
    public void decreaseVolume() {
        float newVolume = volume - 0.1f;
        setVolume(newVolume);
    }
    
    /**
     * Get current volume (0.0 to 1.0)
     */
    public float getVolume() {
        return volume;
    }
    
    /**
     * Get current volume as percentage (0-100)
     */
    public int getVolumePercent() {
        return (int)(volume * 100);
    }
    
    /**
     * Check if music is currently playing
     */
    public boolean isPlaying() {
        return isPlaying && clip != null && clip.isRunning();
    }
    
    /**
     * Check if music is muted
     */
    public boolean isMuted() {
        return isMuted;
    }
    
    /**
     * Get the current music file path
     */
    public String getCurrentMusicFile() {
        return currentMusicFile;
    }
    
    /**
     * Get current playback position in microseconds
     */
    public long getPosition() {
        if (clip != null) {
            return clip.getMicrosecondPosition();
        }
        return 0;
    }
    
    /**
     * Get total length of current track in microseconds
     */
    public long getLength() {
        if (clip != null) {
            return clip.getMicrosecondLength();
        }
        return 0;
    }
    
    /**
     * Restart the current track from the beginning
     */
    public void restart() {
        if (clip != null) {
            clip.setMicrosecondPosition(0);
            if (!isPlaying) {
                clip.start();
                isPlaying = true;
            }
            System.out.println("🔄 Music restarted");
        }
    }
    
    /**
     * Interface for listening to music state changes
     */
    public interface MusicStateListener {
        void onMusicStateChanged();
    }
}