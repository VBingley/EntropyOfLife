package nl.bingley.customlife.model;

import nl.bingley.customlife.config.UniverseProperties;
import org.springframework.stereotype.Component;

@Component
public class Universe {

    private final UniverseProperties properties;
    private boolean paused = false;

    private int genPerSec = 2;
    private int genPerSecCounter = 0;
    private int generation;
    private Space space;

    public Universe(UniverseProperties properties) {
        this.properties = properties;
        space = new Space(properties, true);
    }

    public void reset() {
        space = new Space(properties, true);
        generation = 0;
    }

    public void incrementGeneration() {
        generation++;
        space.update();
    }

    public void incrementGenPerSec() {
        genPerSec++;
    }

    public void decrementGenPerSec() {
        if (genPerSec > 0) {
            genPerSec--;
        }
    }

    public int getGenPerSec() {
        return genPerSec;
    }

    public int getGenPerSecCounter() {
        return genPerSecCounter;
    }

    public void setGenPerSecCounter(int count) {
        genPerSecCounter = count;
    }

    public int getGeneration() {
        return generation;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public Space getSpace() {
        return space;
    }
}
