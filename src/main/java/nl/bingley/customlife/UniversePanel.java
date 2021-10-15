package nl.bingley.customlife;

import nl.bingley.customlife.config.LifeProperties;
import nl.bingley.customlife.config.UniverseProperties;
import nl.bingley.customlife.model.Cell;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UniversePanel extends JPanel {
    private static final long serialVersionUID = 119486406615542676L;

    private final int universeSize;
    private final LifeProperties lifeProps;

    private int translateX = 0;
    private int translateY = 0;
    private int cellSize = 5;

    private final Universe universe;

    public UniversePanel(Universe universe, UniverseProperties universeProperties) {
        super();
        this.universe = universe;
        universeSize = universeProperties.getSize();
        lifeProps = universeProperties.getLifeProperties();
    }

    @Override
    protected void paintComponent(Graphics graphics) {

        paintBackground(graphics);

        Cell[][] allCells = universe.getAllCells();

        paintCells(graphics, allCells);
        paintInfo(graphics, allCells);

        graphics.dispose();
    }

    private void paintBackground(Graphics graphics) {
        graphics.setColor(Color.BLACK);
        Rectangle bounds = graphics.getClipBounds();
        graphics.fillRect(0, 0, bounds.width, bounds.height);
    }

    private void paintInfo(Graphics graphics, Cell[][] allCells) {
        List<Cell> cells = Arrays.stream(allCells)
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());
        long aliveCount = cells.stream()
                .filter(cell -> cell.value > lifeProps.getLifeEnergyThreshold())
                .count();
        graphics.setColor(Color.RED);
        graphics.drawString("Gen:  " + universe.getGeneration(), 10, 20);
        graphics.drawString("Gen/s:  " + universe.getGenPerSec(), 10, 40);
        graphics.drawString("Net E: " + Math.round(cells.stream().map(cell -> cell.value).reduce(Float::sum).orElse(-1f)), 10, 60);
        graphics.drawString("Abs E: " + Math.round(cells.stream().map(cell -> Math.abs(cell.value)).reduce(Float::sum).orElse(-1f)), 10, 80);
        graphics.drawString("Alive: " + aliveCount, 10, 100);
    }

    private void paintCells(Graphics graphics, Cell[][] cells) {
        Rectangle bounds = graphics.getClipBounds();

        int drawSize = cellSize > 3 ? cellSize - 1 : cellSize;
        for (int x = 0; x < universeSize; x++) {
            for (int y = 0; y < universeSize; y++) {
                int posX = x * cellSize + translateX;
                int posY = y * cellSize + translateY;
                if (posX > -cellSize && posX < bounds.width && posY > -cellSize && posY < bounds.height) {
                    paintCell(graphics, posX, posY, drawSize, calculateCellColor(cells[x][y].value));
                }
            }
        }
    }

    private void paintCell(Graphics graphics, int posX, int posY, int size, Color fill) {
        graphics.setColor(fill);
        graphics.fillRect(posX, posY, size, size);
    }

    private Color calculateCellColor(float cellValue) {
        if (cellValue > 1) {
            return new Color(0.75f, 0, 0);
        }
        if (cellValue > lifeProps.getMinEnergyState()) {
            float red = cellValue > lifeProps.getLifeEnergyThreshold() ?
                    0.5f * gradient(cellValue, 1 - lifeProps.getLifeEnergyThreshold(), 1) : 0;
            float green = cellValue > lifeProps.getLifeEnergyThreshold() ?
                    0.5f * gradient(cellValue, lifeProps.getHighEnergyState() - lifeProps.getLowEnergyState(), lifeProps.getHighEnergyState()) : 0;
            float blue = 0.5f * gradient(cellValue, lifeProps.getLifeEnergyThreshold() - lifeProps.getMinEnergyState(), lifeProps.getLifeEnergyThreshold());
            return new Color(red, green, blue);
        } else {
            float abs = Math.abs(cellValue);
            float value = abs > 1 ? 1 : abs;
            return new Color(value * 0.5f, 0, value * 0.5f);
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

    public Cell findCellAtPixel(int pixelX, int pixelY) {
        return universe.findCell((pixelX - translateX) / cellSize, (pixelY - translateY) / cellSize);
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

    public void addTranslateX(int translateX) {
        this.translateX += translateX;
    }

    public void addTranslateY(int translateY) {
        this.translateY += translateY;
    }
}