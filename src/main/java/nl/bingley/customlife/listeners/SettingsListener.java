package nl.bingley.customlife.listeners;

import nl.bingley.customlife.model.Universe;
import org.springframework.stereotype.Component;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@Component
public class SettingsListener implements KeyListener {

    private final Universe universe;

    public SettingsListener(Universe universe) {
        this.universe = universe;
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
                universe.incrementGenPerSec();
                break;
            case KeyEvent.VK_DOWN:
                universe.decrementGenPerSec();
                break;
            case KeyEvent.VK_RIGHT:
                if (universe.isPaused()) {
                    universe.incrementGeneration();
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
