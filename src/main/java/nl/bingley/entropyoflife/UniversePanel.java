package nl.bingley.entropyoflife;

import nl.bingley.entropyoflife.config.LifeProperties;
import nl.bingley.entropyoflife.config.UniverseProperties;
import nl.bingley.entropyoflife.models.Universe;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class UniversePanel extends JPanel {
    private static final long serialVersionUID = 119486406615542676L;

    private final Universe universe;
    private final LifeProperties props;

    private int translateX;
    private int translateY;
    private int cellSize = 8;

    private int genPerSec = 0;

    public UniversePanel(Universe universe, UniverseProperties universeProperties) {
        super();
        this.universe = universe;
        props = universeProperties.getLifeProperties();
        translateX = 1024 / 2 - universe.getSize() * cellSize / 2;
        translateY = 1024 / 2 - universe.getSize() * cellSize / 2;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        paintBackground(graphics);

         float[][] energyMatrix = universe.energyMatrix;
         paintCells(graphics, energyMatrix);
         paintInfo(graphics);

        graphics.dispose();
    }

    private void paintBackground(Graphics graphics) {
        graphics.setColor(Color.BLACK);
        Rectangle bounds = graphics.getClipBounds();
        graphics.fillRect(0, 0, bounds.width, bounds.height);
    }

    private void paintInfo(Graphics graphics) {
        graphics.setColor(Color.RED);
        graphics.drawString("Gen:  " + universe.getGeneration(), 10, 20);
        graphics.drawString("Gen/s:  " + genPerSec, 10, 40);
    }

    private void paintCells(Graphics graphics, float[][] cells) {
        Rectangle bounds = graphics.getClipBounds();

        int drawSize = cellSize > 3 ? cellSize - 1 : cellSize;
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells.length; y++) {
                int posX = x * cellSize + translateX;
                int posY = y * cellSize + translateY;
                if (posX > -cellSize && posX < bounds.width && posY > -cellSize && posY < bounds.height) {
                    paintCell(graphics, posX, posY, drawSize, calculateCellColor(cells[x][y]));
                }
            }
        }
    }

    private void paintCell(Graphics graphics, int posX, int posY, int size, Color fill) {
        graphics.setColor(fill);
        graphics.fillRect(posX, posY, size, size);
    }

    public Color calculateCellColor(float cellValue) {
        if (cellValue > 1) {
            return new Color(0.75f, 0, 0);
        }
        if (cellValue > props.getMinEnergyState()) {
            float red = cellValue > props.getLifeEnergyThreshold() ?
                    0.5f * gradient(cellValue, 1 - props.getLifeEnergyThreshold(), 1) : 0;
            float green = cellValue > props.getLifeEnergyThreshold() ?
                    0.5f * gradient(cellValue, props.getHighEnergyState() - props.getLowEnergyState(), props.getHighEnergyState()) : 0;
            float blue = 0.5f * gradient(cellValue, props.getLifeEnergyThreshold() - props.getMinEnergyState(), props.getLifeEnergyThreshold());
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

    public float findEnergyAtPixel(int pixelX, int pixelY) {
        int x = convertPixelToCellCoordinate(pixelX, true);
        int y = convertPixelToCellCoordinate(pixelY, false);
        return universe.energyMatrix[x][y];
    }

    public void setEnergyAtPixel(int pixelX, int pixelY, float value) {
        int x = convertPixelToCellCoordinate(pixelX, true);
        int y = convertPixelToCellCoordinate(pixelY, false);
        universe.energyMatrix[x][y] = value;
    }

    private int convertPixelToCellCoordinate(int pixel, boolean isXAxis) {
        int translate = isXAxis ? translateX : translateY;
        int universeSize = universe.getSize();
        int cell = (pixel - translate) / cellSize;
        if (cell < 0) cell = 0;
        if (cell >= universeSize) cell = universeSize -1;
        return cell;
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