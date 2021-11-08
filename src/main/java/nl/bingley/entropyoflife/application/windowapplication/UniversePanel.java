package nl.bingley.entropyoflife.application.windowapplication;

import com.aparapi.Range;
import nl.bingley.entropyoflife.kernels.RendererKernel;
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

    private final RendererKernel rendererKernel;
    private Range kernelRange;
    private int[] imageData;
    private BufferedImage bufferedImage;

    private int translateX;
    private int translateY;
    private int cellSize = 8;

    private int genPerSec = 0;

    public UniversePanel(Universe universe, RendererKernel rendererKernel) {
        super();
        this.universe = universe;
        translateX = 1024 / 2 - universe.getSize() * cellSize / 2;
        translateY = 1024 / 2 - universe.getSize() * cellSize / 2;

        this.rendererKernel = rendererKernel;
        updateBufferedImage(1024, 1024);
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
            updateBufferedImage(getWidth(), getHeight());
        }

        rendererKernel.setCellSize(cellSize);
        rendererKernel.setTranslateX(translateX);
        rendererKernel.setTranslateY(translateY);

        rendererKernel.put(universe.energyMatrix);
        rendererKernel.execute(kernelRange);
        rendererKernel.get(imageData);

        graphics.drawImage(bufferedImage, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), this);
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

    private void updateBufferedImage(int width, int height) {
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.imageData = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
        kernelRange = Range.create(imageData.length);
        rendererKernel.setImageDataSize(imageData, width);
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