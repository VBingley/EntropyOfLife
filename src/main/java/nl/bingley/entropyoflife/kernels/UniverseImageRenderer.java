package nl.bingley.entropyoflife.kernels;

import nl.bingley.entropyoflife.config.properties.LifeProperties;
import nl.bingley.entropyoflife.config.properties.UniverseProperties;
import nl.bingley.entropyoflife.models.Universe;
import org.springframework.stereotype.Component;

/**
 * I'd really like to do this in a GPU kernel,
 * but for some reason I can't get that to work for larger images without getting an FP64 error
 **/
@Component
public class UniverseImageRenderer {

    private final Universe universe;
    private final LifeProperties props;

    public UniverseImageRenderer(Universe universe, UniverseProperties properties) {
        this.universe = universe;
        props = properties.getLifeProperties();
    }

    public void render(int[] imageData, int width, int cellSize, int translateX, int translateY) {
        for (int i = 0; i < imageData.length; i++) {
            imageData[i] = calculatePixelValue(i, width, cellSize, translateX, translateY);
        }
    }

    private int calculatePixelValue(int pixel, int imageWidth, int cellSize, int translateX, int translateY) {
        int pixelX = pixel % imageWidth;
        int pixelY = pixel / imageWidth;
        if (cellSize > 3 && ((pixelX - translateX) % cellSize == 0 || (pixelY - translateY) % cellSize == 0)) {
            return 0;
        }
        int cellX = (int) ((float) pixelX / cellSize - (float) translateX / cellSize);
        int cellY = (int) ((float) pixelY / cellSize - (float) translateY / cellSize);
        if (cellX < 0 || cellX >= universe.getSize() || cellY < 0 || cellY >= universe.getSize()) {
            return 0;
        } else {
            float range = props.getEnergyJump();
            float cellValue = universe.energyMatrix[cellX][cellY];
            if (cellValue < 0) {
                int color = colorGradient(Math.min(range, Math.abs(cellValue)) / range, 120);
                return color + (color << 16);
            } else if (cellValue < props.getLifeEnergyThreshold()) {
                return colorGradient(cellValue / props.getLifeEnergyThreshold(), 122);
            } else if (cellValue < props.getHighEnergyState()) {
                return (71 << 16) + (97 << 8) + 60;
            } else {
                float modifier = Math.min(range, cellValue - props.getLifeEnergyThreshold());
                int red = colorGradient(modifier / range, 130);
                int green = colorGradient(1 - (modifier / range), 130);
                return (int) ((red << 16) + (green << 8) + ((1 - modifier / range)));
            }
        }
    }

    private int colorGradient(float num, int max) {
        int result = (int) (Math.sin(num * 0.5 * Math.PI) * max);
        return Math.max(result, 0);
    }
}
