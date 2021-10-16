package nl.bingley.entropyoflife.listeners;

import nl.bingley.entropyoflife.Universe;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@Component
public class KeyInputListener implements KeyListener {

    private final Universe universe;
    private final Timer universeUpdateTimer;

    public KeyInputListener(Universe universe, Timer universeUpdateTimer) {
        this.universe = universe;
        this.universeUpdateTimer = universeUpdateTimer;
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
                universe.reset();
                break;
            case KeyEvent.VK_UP:
                changeGenerationsPerSecond(0.5f);
                break;
            case KeyEvent.VK_DOWN:
                changeGenerationsPerSecond(2f);
                break;
            case KeyEvent.VK_RIGHT:
                if (!universeUpdateTimer.isRunning()) {
                    universe.nextGeneration();
                }
                break;
            case KeyEvent.VK_P:
                System.out.println("Universe initial state:");
                System.out.println(universe.toString());
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
