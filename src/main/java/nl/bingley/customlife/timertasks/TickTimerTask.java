package nl.bingley.customlife.timertasks;

import nl.bingley.customlife.model.Universe;
import org.springframework.stereotype.Component;

import java.util.TimerTask;

@Component
public class TickTimerTask extends TimerTask {

    private final Universe universe;
    private long lastTick;
    private long genPerSecTimer;
    private int genPersecCounter = 0;

    public TickTimerTask(Universe universe) {
        this.universe = universe;
        lastTick = System.currentTimeMillis();
        genPerSecTimer = System.currentTimeMillis();
    }

    @Override
    public void run() {
        if (!universe.isPaused() && lastTick < System.currentTimeMillis() - Math.floor(1000d/universe.getGenPerSec())) {
            updateFpsTimer();
            universe.nextGeneration();
            lastTick = System.currentTimeMillis();
        } else if (universe.isPaused()) {
            lastTick = System.currentTimeMillis();
        }
    }

    private void updateFpsTimer() {
        if (System.currentTimeMillis() > genPerSecTimer + 1000 ) {
            genPerSecTimer = System.currentTimeMillis();
            universe.setGenPerSecCounter(genPersecCounter);
            genPersecCounter = 0;
        }
            genPersecCounter++;
    }
}
