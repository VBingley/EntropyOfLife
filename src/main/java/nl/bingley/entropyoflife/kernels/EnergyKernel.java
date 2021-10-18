package nl.bingley.entropyoflife.kernels;

import com.aparapi.Kernel;
import nl.bingley.entropyoflife.config.UniverseProperties;
import nl.bingley.entropyoflife.models.Universe;
import org.springframework.stereotype.Component;

@Component
public class EnergyKernel extends Kernel {

    private final float[][] deltaMatrix;
    private final float[][] energyMatrix;

    private final int energyNeighbourhoodRadius;
    private final int universeSize;

    public EnergyKernel(Universe universe, UniverseProperties properties) {
        super();
        deltaMatrix = universe.deltaMatrix;
        energyMatrix = universe.energyMatrix;
        universeSize = universe.getSize();
        energyNeighbourhoodRadius = properties.getLifeProperties().getEnergyNeighbourhoodRadius();

        setExplicit(true);
    }

    // Calculate the next energy value for value at x,y
    @Override
    public void run() {
        int x = getGlobalId(0);
        int y = getGlobalId(1);
        energyMatrix[x][y] += deltaMatrix[x][y] - gatherEnergyDeltaFromNeighbourhood(x, y);
    }

    private float gatherEnergyDeltaFromNeighbourhood(int deltaX, int deltaY) {
        if (energyNeighbourhoodRadius == 0) {
            return 0;
        }
        float neighbourhoodDelta = 0;
        for (int x = deltaX - energyNeighbourhoodRadius; x <= deltaX + energyNeighbourhoodRadius; x++) {
            for (int y = deltaY - energyNeighbourhoodRadius; y <= deltaY + energyNeighbourhoodRadius; y++) {
                int posX = EnergyUtil.wrapUniverse(x, universeSize);
                int posY = EnergyUtil.wrapUniverse(y, universeSize);
                if ((x != deltaX || y != deltaY)) {
                    neighbourhoodDelta += deltaMatrix[posX][posY];
                }
            }
        }
        return neighbourhoodDelta / (pow(2 * energyNeighbourhoodRadius + 1, 2) - 1);
    }
}
