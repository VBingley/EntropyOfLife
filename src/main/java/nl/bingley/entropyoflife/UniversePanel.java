package nl.bingley.entropyoflife;

import nl.bingley.entropyoflife.config.properties.LifeProperties;
import nl.bingley.entropyoflife.config.properties.UniverseProperties;
import nl.bingley.entropyoflife.models.Universe;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

@Component
public class UniversePanel extends JPanel {
    private static final long serialVersionUID = 119486406615542676L;

    private final Universe universe;
    private final LifeProperties props;

    private final BufferedImage bufferedImage;

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
        bufferedImage = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        paintBackground(graphics);
        paintCells(graphics);
        paintInfo(graphics);

        graphics.dispose();
    }


    private void paintBackground(Graphics graphics) {
        graphics.setColor(Color.BLACK);
        Rectangle bounds = graphics.getClipBounds();
        graphics.fillRect(0, 0, bounds.width, bounds.height);
    }

    private void paintCells(Graphics graphics) {
        int[] imageData = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < imageData.length; i++) {
            renderCell(i, bufferedImage.getWidth(), imageData);
        }
        graphics.drawImage(bufferedImage, 0, 0, 1024, 1024, this);
    }

    private void renderCell(int pixel, int imageWidth, int[] imageData) {
        int pixelX = pixel % imageWidth - translateX;
        int pixelY = pixel / imageWidth - translateY;
        if (cellSize > 3 && (pixelX % cellSize == 0 || pixelY % cellSize == 0)) {
            imageData[pixel] = 0;
            return;
        }
        int cellX = pixelX / cellSize;
        int cellY = pixelY / cellSize;
        if (cellX < 0 || cellX >= universe.getSize() || cellY < 0 || cellY >= universe.getSize()) {
            imageData[pixel] = 0;
        } else {
            float cellValue = universe.energyMatrix[cellX][cellY];
            if (cellValue < props.getLifeEnergyThreshold()) {
                imageData[pixel] = 0x000055;
            } else {
                imageData[pixel] = 0x336633;
            }
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

    private void paintInfo(Graphics graphics) {
        graphics.setColor(Color.RED);
        graphics.drawString("Gen:  " + universe.getGeneration(), 10, 20);
        graphics.drawString("Gen/s:  " + genPerSec, 10, 40);
    }

    public float findValueAtPixel(int pixelX, int pixelY) {
        int x = convertPixelToCellCoordinate(pixelX, true);
        int y = convertPixelToCellCoordinate(pixelY, false);
        return universe.energyMatrix[x][y];
    }

    public void setValueAtPixel(int pixelX, int pixelY, float value) {
        int x = convertPixelToCellCoordinate(pixelX, true);
        int y = convertPixelToCellCoordinate(pixelY, false);
        universe.energyMatrix[x][y] = value;
    }

    private int convertPixelToCellCoordinate(int pixel, boolean isXAxis) {
        int translate = isXAxis ? translateX : translateY;
        int universeSize = universe.getSize();
        int cell = (pixel - translate) / cellSize;
        if (cell < 0) cell = 0;
        if (cell >= universeSize) cell = universeSize - 1;
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