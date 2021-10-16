package nl.bingley.customlife.model;

public class Cell {

    public int x;
    public int y;
    public float oldValue;
    public float value;

    public Cell(int x, int y, float value) {
        this.x = x;
        this.y = y;
        this.value = value;
        oldValue = value;
    }

    public synchronized void addToNewValue(float value) {
        this.value += value;
    }
}
