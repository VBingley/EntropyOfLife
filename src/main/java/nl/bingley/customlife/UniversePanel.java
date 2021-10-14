package nl.bingley.customlife;

import nl.bingley.customlife.model.Cell;
import nl.bingley.customlife.model.Space;
import nl.bingley.customlife.model.Universe;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UniversePanel extends JPanel {
    private static final long serialVersionUID = 119486406615542676L;

    private int width = 0;
    private int height = 0;
    private int translateX = 0;
    private int translateY = 0;
    private int cellSize = 5;
    private boolean painting = false;

    private final Universe universe;

    public UniversePanel(Universe initialState) {
        super();
        universe = initialState;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        painting = true;
        paintBackground(graphics);

        Space space = universe.getSpace();
        List<Cell> allCells = space.getAllCells();

        paintCells(graphics, allCells);
        paintInfo(graphics, allCells);

        graphics.dispose();
        painting = false;
    }

    private void paintBackground(Graphics graphics) {
        graphics.setColor(Color.BLACK);
        Rectangle bounds = graphics.getClipBounds();
        graphics.fillRect(0, 0, bounds.width, bounds.height);
    }

    private void paintInfo(Graphics graphics, List<Cell> allCells) {
        List<Cell> aliveCells = allCells.stream()
                .filter(cell -> cell.value > Space.aliveThreshold)
                .collect(Collectors.toList());
        graphics.setColor(Color.RED);
        graphics.drawString("Gen:  " + universe.getGeneration(), 10, 20);
        graphics.drawString("GpS:  " + universe.getGenPerSecCounter() + "/" + universe.getGenPerSec(), 10, 40);
        graphics.drawString("Net E: " + Math.round(allCells.stream().map(cell -> cell.value).reduce(Float::sum).orElse(-1f) * 100) * 0.01d, 10, 60);
        graphics.drawString("Abs E: " + Math.round(allCells.stream().map(cell -> Math.abs(cell.value)).reduce(Float::sum).orElse(-1f) * 100) * 0.01d, 10, 80);
        graphics.drawString("Alive: " + aliveCells.size(), 10, 100);
    }

    private void paintCells(Graphics graphics, Collection<Cell> cells) {
        Rectangle bounds = graphics.getClipBounds();
        width = bounds.width;
        height = bounds.height;

        cells = cells.stream().sorted((cell1, cell2) -> (Float.compare(cell1.value, cell2.value))).collect(Collectors.toList());
        float cellValue = -99;
        int drawSize = cellSize > 3 ? cellSize - 1 : cellSize;
        for (Cell cell : cells) {
            int posX = cell.x * cellSize + translateX;
            int posY = cell.y * cellSize + translateY;
            if (posX > -cellSize && posX < width && posY > -cellSize && posY < height) {
                if (cell.value > cellValue + 0.01) {
                    graphics.setColor(getCellColor(cell.value));
                }
                paintCell(graphics, posX, posY, drawSize);
            }
        }
    }

    private Color getCellColor(float cellValue) {
        if (cellValue > Space.minEnergyState) {
            float red = cellValue > Space.aliveThreshold ?
                    gradient(cellValue, Space.highEnergyState - Space.lowEnergyState, 1) : 0;
            float green = cellValue > Space.aliveThreshold ?
                    0.5f * gradient(cellValue, Space.highEnergyState - Space.lowEnergyState, Space.highEnergyState) : 0;
            float blue = 0.5f * gradient(cellValue, Space.aliveThreshold - Space.minEnergyState, Space.aliveThreshold);
            return new Color(red, green, blue);
        } else {
            float abs = Math.abs(cellValue);
            float red = abs > 1 ? 1 : abs;
            return new Color(red * 0.5f, 0, 0);
        }
    }

    private float gradient(float value, float radius, float peak) {
        // min = 0 mid = pi/2 max = pi
        if (value < 0 || value < peak - radius || value > peak + radius) {
            return 0;
        }
        float location = value - peak + radius;
        float result = (float) Math.sin((location / (radius * 2)) * Math.PI);
        return result < 0 ? 0 : result;
    }

    private void paintCell(Graphics graphics, int posX, int posY, int size) {
        graphics.fillRect(posX, posY, size, size);
    }

    public void zoomIn(int rawTranslateX, int rawTranslateY) {
        if (cellSize < 64) {
            translateX = (int) (translateX * ((cellSize * 2) / (double) cellSize));
            translateY = (int) (translateY * ((cellSize * 2) / (double) cellSize));
            translateX = translateX + rawTranslateX;
            translateY = translateY + rawTranslateY;
            cellSize = cellSize * 2;
        }
    }

    public void zoomOut(int rawTranslateX, int rawTranslateY) {
        if (cellSize > 1) {
            translateX = (int) (translateX * ((cellSize / 2) / (double) cellSize));
            translateY = (int) (translateY * ((cellSize / 2) / (double) cellSize));
            translateX = translateX - rawTranslateX / 2;
            translateY = translateY - rawTranslateY / 2;
            cellSize = cellSize / 2;
        }
    }

    public boolean isPainting() {
        return painting;
    }

    public void addTranslateX(int translateX) {
        this.translateX += translateX;
    }

    public void addTranslateY(int translateY) {
        this.translateY += translateY;
    }
}