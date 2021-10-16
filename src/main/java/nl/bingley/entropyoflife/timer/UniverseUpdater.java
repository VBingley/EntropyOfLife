package nl.bingley.entropyoflife.timer;

import nl.bingley.entropyoflife.Universe;
import nl.bingley.entropyoflife.UniversePanel;
import org.springframework.stereotype.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
public class UniverseUpdater implements ActionListener {

    private final Universe universe;
    private final UniversePanel panel;

    private boolean isPaused = false;
    private long genPerSecTimer;
    private int genPerSecCounter = 0;

    public UniverseUpdater(Universe universe, UniversePanel universePanel) {
        this.universe = universe;
        this.panel = universePanel;
        genPerSecTimer = System.currentTimeMillis();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (!isPaused) {
            updateGenPerSecTimer();
            universe.nextGeneration();
        }
    }

    private void updateGenPerSecTimer() {
        if (System.currentTimeMillis() > genPerSecTimer + 1000) {
            genPerSecTimer = System.currentTimeMillis();
            panel.setGenPerSec(genPerSecCounter);
            genPerSecCounter = 0;
        }
        genPerSecCounter++;
    }
}
