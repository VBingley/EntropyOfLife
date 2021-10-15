package nl.bingley.customlife.listeners;

import nl.bingley.customlife.Universe;
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
                universe.setPaused(!universe.isPaused());
                break;
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_R:
            case KeyEvent.VK_BACK_SPACE:
                universe.reset();
                break;
            case KeyEvent.VK_UP:
                universeUpdateTimer.setDelay((int) Math.floor(universeUpdateTimer.getDelay() * 0.5));
                break;
            case KeyEvent.VK_DOWN:
                universeUpdateTimer.setDelay((int) Math.floor(universeUpdateTimer.getDelay() * 2));
                break;
            case KeyEvent.VK_RIGHT:
                if (universe.isPaused()) {
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
}
