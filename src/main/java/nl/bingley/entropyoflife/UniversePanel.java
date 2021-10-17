package nl.bingley.entropyoflife;

import nl.bingley.entropyoflife.config.LifeProperties;
import nl.bingley.entropyoflife.config.UniverseProperties;
import nl.bingley.entropyoflife.models.Cell;
import nl.bingley.entropyoflife.models.Universe;
import nl.bingley.entropyoflife.services.CellStateCalculator;
import nl.bingley.entropyoflife.services.UniverseStateCalculator;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

@Component
public class UniversePanel extends JPanel {
    private static final long serialVersionUID = 119486406615542676L;

    private final UniverseStateCalculator universeStateCalculator;
    private final CellStateCalculator cellStateCalculator;

    private final Universe universe;

    private int translateX = 0;
    private int translateY = 0;
    private int cellSize = 5;

    private int genPerSec = 0;

    public UniversePanel(UniverseStateCalculator universeStateCalculator, CellStateCalculator cellStateCalculator) {
        super();
        this.universeStateCalculator = universeStateCalculator;
        this.cellStateCalculator = cellStateCalculator;
        universe = universeStateCalculator.getUniverse();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        paintBackground(graphics);

        Cell[][] energyMap = universe.getEnergyMap();
        paintCells(graphics, energyMap);
        paintInfo(graphics, energyMap);

        graphics.dispose();
    }

    private void paintBackground(Graphics graphics) {
        graphics.setColor(Color.BLACK);
        Rectangle bounds = graphics.getClipBounds();
        graphics.fillRect(0, 0, bounds.width, bounds.height);
    }

    private void paintInfo(Graphics graphics, Cell[][] cells) {
        long aliveCount = Arrays.stream(cells).flatMap(Arrays::stream)
                .filter(cellStateCalculator::isAlive)
                .count();
        graphics.setColor(Color.RED);
        graphics.drawString("Gen:  " + universeStateCalculator.getGeneration(), 10, 20);
        graphics.drawString("Gen/s:  " + genPerSec, 10, 40);
        graphics.drawString("Net E: " + Math.round(Arrays.stream(cells).flatMap(Arrays::stream).map(cell -> cell.value).reduce(Float::sum).orElse(-1f)), 10, 60);
        graphics.drawString("Abs E: " + Math.round(Arrays.stream(cells).flatMap(Arrays::stream).map(cell -> Math.abs(cell.value)).reduce(Float::sum).orElse(-1f)), 10, 80);
        graphics.drawString("Alive: " + aliveCount, 10, 100);
    }

    private void paintCells(Graphics graphics, Cell[][] cells) {
        Rectangle bounds = graphics.getClipBounds();

        int drawSize = cellSize > 3 ? cellSize - 1 : cellSize;
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells.length; y++) {
                int posX = x * cellSize + translateX;
                int posY = y * cellSize + translateY;
                if (posX > -cellSize && posX < bounds.width && posY > -cellSize && posY < bounds.height) {
                    paintCell(graphics, posX, posY, drawSize, cellStateCalculator.calculateCellColor(cells[x][y].value));
                }
            }
        }
    }

    private synchronized void paintCell(Graphics graphics, int posX, int posY, int size, Color fill) {
        graphics.setColor(fill);
        graphics.fillRect(posX, posY, size, size);
    }

    public Cell findCellAtPixel(int pixelX, int pixelY) {
        int universeSize = universe.getSize();
        int x = (pixelX - translateX) / cellSize;
        int y = (pixelY - translateY) / cellSize;
        if (x < 0) x = 0;
        if (x >= universeSize) x = universeSize -1;
        if (y < 0) y = 0;
        if (y >= universeSize) y = universeSize -1;
        return universe.getEnergyCell(x, y);
    }

    public void zoomIn(int focusX, int focusY) {
        if (cellSize < 64) {
            translateX = (translateX * ((cellSize * 2) / cellSize));
            translateY = (translateY * ((cellSize * 2) / cellSize));
            translateX = translateX - focusX;
            translateY = translateY - focusY;
            cellSize = cellSize * 2;
        }
    }

    public void zoomOut(int focusX, int focusY) {
        if (cellSize > 1) {
            translateX = (int) (translateX * ((cellSize / 2d) / cellSize));
            translateY = (int) (translateY * ((cellSize / 2d) / cellSize));
            translateX = translateX + focusX / 2;
            translateY = translateY + focusY / 2;
            cellSize = cellSize / 2;
        }
    }

    public void addTranslateX(int translateX) {
        this.translateX += translateX;
    }

    public void addTranslateY(int translateY) {
        this.translateY += translateY;
    }

    public void setGenPerSec(int genPerSec) {
        this.genPerSec = genPerSec;
    }
}