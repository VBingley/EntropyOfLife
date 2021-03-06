package nl.bingley.entropyoflife.kernels;

import com.aparapi.Range;
import nl.bingley.entropyoflife.UniverseTestUtil;
import nl.bingley.entropyoflife.config.properties.UniverseProperties;
import nl.bingley.entropyoflife.models.Universe;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RendererKernelTest {

    private RendererKernel rendererKernel;
    private int[] imageData;
    private float[][] energyMatrix;
    private Range kernelRange;

    @Test
    public void testBlock() {
        int size = 4;
        int cellSize = 3;
        initializeRendererKernel(size, cellSize);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                energyMatrix[x][y] = 1;
            }
        }

        rendererKernel.put(energyMatrix);
        rendererKernel.execute(kernelRange);
        rendererKernel.get(imageData);

        assertEquals(size * cellSize * size * cellSize, imageData.length);
        assertEquals(0, Arrays.stream(imageData).filter(value -> value == 0).count());
    }

    @Test
    public void testMinX() {
        int size = 4;
        int cellSize = 3;
        initializeRendererKernel(size, cellSize);
        for (int y = 0; y < size; y++) {
            energyMatrix[0][y] = 1;
        }

        rendererKernel.put(energyMatrix);
        rendererKernel.execute(kernelRange);
        rendererKernel.get(imageData);

        assertEquals(size * cellSize * size * cellSize, imageData.length);
        assertEquals(size * 3 * cellSize * cellSize, Arrays.stream(imageData).filter(value -> value == 0).count());
    }

    @Test
    public void testMaxX() {
        int size = 4;
        int cellSize = 3;
        initializeRendererKernel(size, cellSize);
        for (int y = 0; y < size; y++) {
            energyMatrix[size - 1][y] = 1;
        }

        rendererKernel.put(energyMatrix);
        rendererKernel.execute(kernelRange);
        rendererKernel.get(imageData);

        assertEquals(size * cellSize * size * cellSize, imageData.length);
        assertEquals(size * 3 * cellSize * cellSize, Arrays.stream(imageData).filter(value -> value == 0).count());
    }

    @Test
    public void testMinY() {
        int size = 4;
        int cellSize = 3;
        initializeRendererKernel(size, cellSize);
        for (int x = 0; x < size; x++) {
            energyMatrix[x][0] = 1;
        }

        rendererKernel.put(energyMatrix);
        rendererKernel.execute(kernelRange);
        rendererKernel.get(imageData);

        assertEquals(size * cellSize * size * cellSize, imageData.length);
        assertEquals(size * 3 * cellSize * cellSize, Arrays.stream(imageData).filter(value -> value == 0).count());
    }

    @Test
    public void testMaxY() {
        int size = 4;
        int cellSize = 3;
        initializeRendererKernel(size, cellSize);
        for (int x = 0; x < size; x++) {
            energyMatrix[x][size - 1] = 1;
        }

        rendererKernel.put(energyMatrix);
        rendererKernel.execute(kernelRange);
        rendererKernel.get(imageData);

        assertEquals(size * cellSize * size * cellSize, imageData.length);
        assertEquals(size * 3 * cellSize * cellSize, Arrays.stream(imageData).filter(value -> value == 0).count());
    }

    @Test
    public void testRing() {
        int size = 4;
        int cellSize = 3;
        initializeRendererKernel(size, cellSize);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (x == 0 || y == 0 || x == size - 1 || y == size - 1) {
                    energyMatrix[x][y] = 1;
                }
            }
        }

        rendererKernel.put(energyMatrix);
        rendererKernel.execute(kernelRange);
        rendererKernel.get(imageData);

        assertEquals(size * cellSize * size * cellSize, imageData.length);
        assertEquals(size * cellSize * cellSize, Arrays.stream(imageData).filter(value -> value == 0).count());
    }

    private void initializeRendererKernel(int size, int cellSize) {
        UniverseProperties properties = UniverseTestUtil.mockGameOfLifeProperties(size);

        Universe universe = new Universe(size);
        energyMatrix = universe.energyMatrix;

        rendererKernel = new RendererKernel(universe, properties);
        BufferedImage bufferedImage = new BufferedImage(size * cellSize, size * cellSize, BufferedImage.TYPE_INT_RGB);
        imageData = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
        kernelRange = Range.create(imageData.length);

        rendererKernel.setImageDataSize(imageData, size * cellSize);
        rendererKernel.setCellSize(cellSize);
        rendererKernel.setTranslateX(0);
        rendererKernel.setTranslateY(0);
    }
}