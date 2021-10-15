package nl.bingley.customlife.timer;

import nl.bingley.customlife.Universe;
import org.springframework.stereotype.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
public class UniverseUpdater implements ActionListener {

    private final Universe universe;
    private long lastTick;
    private long genPerSecTimer;
    private int genPersecCounter = 0;

    public UniverseUpdater(Universe universe) {
        this.universe = universe;
        lastTick = System.currentTimeMillis();
        genPerSecTimer = System.currentTimeMillis();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (!universe.isPaused()) {
            updateGenPerSecTimer();
            universe.nextGeneration();
            lastTick = System.currentTimeMillis();
        } else if (universe.isPaused()) {
            lastTick = System.currentTimeMillis();
        }
    }

    private void updateGenPerSecTimer() {
        if (System.currentTimeMillis() > genPerSecTimer + 1000 ) {
            genPerSecTimer = System.currentTimeMillis();
            universe.setGenPerSec(genPersecCounter);
            genPersecCounter = 0;
        }
            genPersecCounter++;
    }
}
