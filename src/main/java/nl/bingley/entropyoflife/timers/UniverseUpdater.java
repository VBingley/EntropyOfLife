package nl.bingley.entropyoflife.timers;

import nl.bingley.entropyoflife.services.UniverseStateCalculator;
import nl.bingley.entropyoflife.UniversePanel;
import org.springframework.stereotype.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
public class UniverseUpdater implements ActionListener {

    private final UniverseStateCalculator universeStateCalculator;
    private final UniversePanel panel;

    private boolean isPaused = false;
    private long genPerSecTimer;
    private int genPerSecCounter = 0;

    public UniverseUpdater(UniverseStateCalculator universeStateCalculator, UniversePanel universePanel) {
        this.universeStateCalculator = universeStateCalculator;
        this.panel = universePanel;
        genPerSecTimer = System.currentTimeMillis();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (!isPaused) {
            updateGenPerSecTimer();
            universeStateCalculator.nextGeneration();
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
