package view;

import javax.sound.sampled.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton + Observer Pattern
 * Manages background music and notifies all screens when state changes
 */
public class MusicManager {
    
    private static MusicManager instance;
    private Clip clip;
    private boolean isPlaying;
    private boolean isMuted;
    private String currentMusicFile;
    private float volume = 0.6f;
    private FloatControl volumeControl;
    
    // ========== OBSERVER PATTERN ==========
    private List<MusicStateListener> listeners = new ArrayList<>();
    // ======================================
    
    private MusicManager() {
        isPlaying = false;
        isMuted = false;
    }
    
    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }
    
    // ========== OBSERVER PATTERN METHODS ==========
    
    /**
     * Register a screen to receive music state updates
     */
    public void addMusicStateListener(MusicStateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            System.out.println("✓ Music listener added: " + listener.getClass().getSimpleName());
        }
    }
    
    /**
     * Unregister a screen
     */
    public void removeMusicStateListener(MusicStateListener listener) {
        listeners.remove(listener);
        System.out.println("✓ Music listener removed: " + listener.getClass().getSimpleName());
    }
    
    /**
     * Notify all registered screens about state change
     */
    private void notifyListeners() {
        for (MusicStateListener listener : listeners) {
            listener.onMusicStateChanged();
        }
    }
    
    // ==============================================
    
    /**
     * Play background music from a file
     */
    public void playMusic(String musicFilePath) {
        try {
            if (clip != null) {
                clip.stop();
                clip.close();
            }

            File musicFile = new File(musicFilePath);
            if (!musicFile.exists()) {
                System.err.println("Music file not found: " + musicFilePath);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);

            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                if (isMuted) {
                    mute();
                } else {
                    setVolume(volume);
                }
            }

            clip.loop(Clip.LOOP_CONTINUOUSLY);

            if (!isMuted) {
                clip.start();
                isPlaying = true;
            } else {
                isPlaying = false;
            }

            currentMusicFile = musicFilePath;
            notifyListeners(); // ← NOTIFY ALL SCREENS!

            System.out.println("✓ Music loaded: " + musicFilePath + " | Muted: " + isMuted);

        } catch (Exception e) {
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
            notifyListeners(); // ← NOTIFY ALL SCREENS!
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
        notifyListeners(); // ← NOTIFY ALL SCREENS!
    }
    
    /**
     * Pause the music
     */
    public void pauseMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            isPlaying = false;
            notifyListeners(); // ← NOTIFY ALL SCREENS!
            System.out.println("⏸ Music paused");
        }
    }
    
    /**
     * Resume the music
     */
    public void resumeMusic() {
        if (clip != null) {
            if (!clip.isRunning()) {
                if (!isMuted) {
                    clip.start();
                    isPlaying = true;
                    notifyListeners(); // ← NOTIFY ALL SCREENS!
                    System.out.println("▶ Music resumed");
                } else {
                    isPlaying = false;
                    System.out.println("🔇 Music is muted, not resuming");
                }
            }
        } else if (currentMusicFile != null) {
            playMusic(currentMusicFile);
            if (isMuted) {
                mute();
            }
        }
    }

    /**
     * Toggle mute
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
            notifyListeners(); // ← NOTIFY ALL SCREENS!
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
            notifyListeners(); // ← NOTIFY ALL SCREENS!
            System.out.println("🔊 Music unmuted");
        }
    }
    
    /**
     * Set the volume (0.0 to 1.0)
     */
    public void setVolume(float newVolume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, newVolume));
        
        if (volumeControl != null) {
            try {
                float min = volumeControl.getMinimum();
                float max = volumeControl.getMaximum();
                
                float dB;
                if (this.volume == 0.0f) {
                    dB = min;
                } else {
                    dB = min + (max - min) * this.volume;
                }
                
                volumeControl.setValue(dB);
                isMuted = false;
                notifyListeners(); // ← NOTIFY ALL SCREENS!
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
    
    // ========== GETTERS ==========
    
    public float getVolume() {
        return volume;
    }
    
    public int getVolumePercent() {
        return (int)(volume * 100);
    }
    
    public boolean isPlaying() {
        return isPlaying;
    }
    
    public boolean isMuted() {
        return isMuted;
    }
    
    public String getCurrentMusicFile() {
        return currentMusicFile;
    }
    
    public long getPosition() {
        if (clip != null) {
            return clip.getMicrosecondPosition();
        }
        return 0;
    }
    
    public long getLength() {
        if (clip != null) {
            return clip.getMicrosecondLength();
        }
        return 0;
    }
    
    public void restart() {
        if (clip != null) {
            clip.setMicrosecondPosition(0);
            if (!isPlaying) {
                clip.start();
                isPlaying = true;
                notifyListeners(); // ← NOTIFY ALL SCREENS!
            }
            System.out.println("🔄 Music restarted");
        }
    }
    
    // ========== LISTENER INTERFACE ==========
    
    /**
     * Interface that all screens must implement to receive music updates
     */
    public interface MusicStateListener {
        void onMusicStateChanged();
    }
}