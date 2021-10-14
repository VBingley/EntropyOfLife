package nl.bingley.customlife.model;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Universe {

    public static final int size = 128;

    private static final int spawnSize = 64;

    private boolean paused = false;

    private int genPerSec = 2;
    private int genPerSecCounter = 0;
    private int generation;
    private Space space;

    public Universe() {
        space = new Space(size, spawnSize);
    }

    public void reset() {
        space = new Space(size, spawnSize);
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
