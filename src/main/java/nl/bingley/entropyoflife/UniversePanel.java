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

    private BufferedImage bufferedImage;
    private int lastPixelValue = 0;
    private int lastCellX = -1;
    private int lastCellY = -1;

    private int translateX;
    private int translateY;
    private int cellSize = 16;

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
        if (bufferedImage.getWidth() != getWidth() || bufferedImage.getHeight() != getHeight()) {
            bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        }
        // I'd really like to do this in a GPU kernel,
        // but for some reason I can't get that to work for larger images without getting an FP64 error
        int[] imageData = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < imageData.length; i++) {
            setImagePixelValue(i, bufferedImage.getWidth(), imageData);
        }
        graphics.drawImage(bufferedImage, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), this);
    }

    private void setImagePixelValue(int pixel, int imageWidth, int[] imageData) {
        int pixelX = pixel % imageWidth - translateX;
        int pixelY = pixel / imageWidth - translateY;
        if (cellSize > 3 && (pixelX % cellSize == 0 || pixelY % cellSize == 0)) {
            imageData[pixel] = 0;
            return;
        }
        int cellX = pixelX / cellSize;
        int cellY = pixelY / cellSize;
        if (cellX == lastCellX && cellY == lastCellY) {
            imageData[pixel] = lastPixelValue;
            return;
        }
        if (cellX < 0 || cellX >= universe.getSize() || cellY < 0 || cellY >= universe.getSize()) {
            imageData[pixel] = 0;
        } else {
            float range = props.getEnergyJump();
            float cellValue = universe.energyMatrix[cellX][cellY];
            if (cellValue < 0) {
                int color = colorGradient(Math.min(range, Math.abs(cellValue)) / range, 120);
                imageData[pixel] = color + (color << 16);
            } else if (cellValue < props.getLifeEnergyThreshold()) {
                imageData[pixel] = colorGradient(cellValue / props.getLifeEnergyThreshold(), 122);
            } else if (cellValue < props.getHighEnergyState()) {
                imageData[pixel] = (71 << 16) + (97 << 8) + 60;
            } else {
                float modifier = Math.min(range, cellValue - props.getLifeEnergyThreshold());
                int red = colorGradient(modifier / range, 130);
                int green = colorGradient(1 - (modifier / range), 130);
                imageData[pixel] = (int) ((red << 16) + (green << 8) + ((1 - modifier / range)));
            }
        }
        lastCellX = cellX;
        lastCellY = cellY;
        lastPixelValue = imageData[pixel];
    }

    private int colorGradient(float num, int max) {
        int result = (int) (Math.sin(num * 0.5 * Math.PI) * max);
        return Math.max(result, 0);
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