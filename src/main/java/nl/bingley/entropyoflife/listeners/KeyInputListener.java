package nl.bingley.entropyoflife.listeners;

import nl.bingley.entropyoflife.timers.UniverseUpdater;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@Component
public class KeyInputListener implements KeyListener {

    private final Timer universeUpdateTimer;
    private final UniverseUpdater universeUpdater;

    public KeyInputListener(Timer universeUpdateTimer, UniverseUpdater universeUpdater) {
        this.universeUpdateTimer = universeUpdateTimer;
        this.universeUpdater = universeUpdater;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                togglePause();
                break;
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_R:
            case KeyEvent.VK_BACK_SPACE:
                universeUpdater.reset();
                break;
            case KeyEvent.VK_UP:
                changeGenerationsPerSecond(0.5f);
                break;
            case KeyEvent.VK_DOWN:
                changeGenerationsPerSecond(2f);
                break;
            case KeyEvent.VK_RIGHT:
                if (!universeUpdateTimer.isRunning()) {
                    universeUpdater.updateUniverse();
                }
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }

    private void togglePause() {
        if (universeUpdateTimer.isRunning()) {
            universeUpdateTimer.stop();
        } else {
            universeUpdateTimer.start();
        }
    }

    private void changeGenerationsPerSecond(float multiplier) {
        int delay = universeUpdateTimer.getDelay();
        if ((multiplier > 1 && delay < 1000) || (multiplier < 1 && delay > 16)) {
            universeUpdateTimer.setDelay((int) Math.floor(universeUpdateTimer.getDelay() * multiplier));
        }
    }
}
