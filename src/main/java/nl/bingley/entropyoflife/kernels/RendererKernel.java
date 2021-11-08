package nl.bingley.entropyoflife.kernels;

import com.aparapi.Kernel;
import nl.bingley.entropyoflife.config.properties.LifeProperties;
import nl.bingley.entropyoflife.config.properties.UniverseProperties;
import nl.bingley.entropyoflife.models.Universe;
import org.springframework.stereotype.Component;

@Component
public class RendererKernel extends Kernel {

    private final float[][] energyMatrix;
    private final int universeSize;
    private final float energyJump;
    private final float lifeEnergyThreshold;
    private final float highEnergyState;
    private final float maxEnergyState;
    private final float minEnergyState;

    private int[] imageData;
    private int imageWidth;
    private int cellSize;
    private int translateX;
    private int translateY;

    public RendererKernel(Universe universe, UniverseProperties properties) {
        energyMatrix = universe.energyMatrix;
        universeSize = universe.getSize();
        LifeProperties props = properties.getLifeProperties();
        lifeEnergyThreshold = props.getLifeEnergyThreshold();
        highEnergyState = props.getHighEnergyState();
        maxEnergyState = props.getMaxEnergyState();
        minEnergyState = props.getMinEnergyState();
        energyJump = highEnergyState - props.getLowEnergyState();

        setExplicit(true);
    }

    @Override
    public void run() {
        int pixel = getGlobalId(0);
        imageData[pixel] = calculatePixelValue(pixel);
    }

    private int calculatePixelValue(int pixel) {
        int pixelX = pixel % imageWidth;
        int pixelY = pixel / imageWidth;
        if (cellSize > 3 && ((pixelX - translateX) % cellSize == 0 || (pixelY - translateY) % cellSize == 0)) {
            return 0;
        }
        int cellX = (int) ((float) pixelX / cellSize - (float) translateX / cellSize);
        int cellY = (int) ((float) pixelY / cellSize - (float) translateY / cellSize);
        if (cellX < 0 || cellX >= universeSize || cellY < 0 || cellY >= universeSize) {
            return 0;
        } else {
            float range = energyJump;
            float cellValue = energyMatrix[cellX][cellY];
            if (cellValue < minEnergyState) {
                int color = colorGradient(min(range, abs(cellValue)) / range, 122);
                return color + (color << 16);
            } else if (cellValue < lifeEnergyThreshold) {
                return colorGradient(cellValue / lifeEnergyThreshold, 170);
            } else if (cellValue < highEnergyState) {
                return (71 << 16) + (97 << 8) + 60;
            } else {
                float modifier = min(range, cellValue - lifeEnergyThreshold);
                int red = colorGradient(modifier / range, 130);
                int green = colorGradient(maxEnergyState - (modifier / range), 130);
                return (int) ((red << 16) + (green << 8) + ((1 - modifier / range)));
            }
        }
    }

    private int colorGradient(float num, int limit) {
        int result = (int) (sinpi(num * 0.5) * limit);
        return max(result, 0);
    }

    public void setImageDataSize(int[] imageData, int imageWidth) {
        this.imageData = imageData;
        this.imageWidth = imageWidth;
    }

    public void setCellSize(int size) {
        cellSize = size;
    }

    public void setTranslateX(int translateX) {
        this.translateX = translateX;
    }

    public void setTranslateY(int translateY) {
        this.translateY = translateY;
    }
}
