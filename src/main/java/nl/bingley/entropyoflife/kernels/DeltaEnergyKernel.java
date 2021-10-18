package nl.bingley.entropyoflife.kernels;

import com.aparapi.Kernel;
import nl.bingley.entropyoflife.config.LifeProperties;
import nl.bingley.entropyoflife.config.UniverseProperties;
import nl.bingley.entropyoflife.models.Universe;
import org.springframework.stereotype.Component;

@Component
public class DeltaEnergyKernel extends Kernel {

    private final float[][] deltaMatrix;
    private final float[][] energyMatrix;
    private final int universeSize;

    private final int lifeNeighbourhoodRadius;

    private final int birthMin;
    private final int birthMax;
    private final int surviveMin;
    private final int surviveMax;
    private final float lifeThreshold;
    private final float minEnergyState;
    private final float lowEnergyState;
    private final float highEnergyState;

    private final float energyJump;
    private final float energyStep;

    public DeltaEnergyKernel(Universe universe, UniverseProperties universeProperties) {
        super();
        deltaMatrix = universe.deltaMatrix;
        energyMatrix = universe.energyMatrix;
        universeSize = universe.getSize();
        LifeProperties properties = universeProperties.getLifeProperties();
        this.lifeNeighbourhoodRadius = properties.getLifeNeighbourhoodRadius();

        lifeThreshold = properties.getLifeEnergyThreshold();
        surviveMin = properties.getSurviveMin();
        surviveMax = properties.getSurviveMax();
        birthMin = properties.getBirthMin();
        birthMax = properties.getBirthMax();
        minEnergyState = properties.getMinEnergyState();
        lowEnergyState = properties.getLowEnergyState();
        highEnergyState = properties.getHighEnergyState();
        energyJump = properties.getEnergyJump();
        energyStep = properties.getEnergyStep();

        setExplicit(true);
    }

    // calculate the next delta energy value for value at x,y
    @Override
    public void run() {
        int x = getGlobalId(0);
        int y = getGlobalId(1);
        deltaMatrix[x][y] = calculateOwnEnergyDelta(x, y);
    }

    private float calculateOwnEnergyDelta(int energyX, int energyY) {
        int livingNeighbours = 0;
        for (int x = energyX - lifeNeighbourhoodRadius; x <= energyX + lifeNeighbourhoodRadius; x++) {
            for (int y = energyY - lifeNeighbourhoodRadius; y <= energyY + lifeNeighbourhoodRadius; y++) {
                int posX = EnergyUtil.wrapUniverse(x, universeSize);
                int posY = EnergyUtil.wrapUniverse(y, universeSize);
                if ((x != energyX || y != energyY) && energyMatrix[posX][posY] > lifeThreshold) {
                    livingNeighbours += 1;
                }
            }
        }
        return calculateEnergyDelta(energyMatrix[energyX][energyY], livingNeighbours);
    }

    private float calculateEnergyDelta(float energyValue, int livingNeighbours) {
        boolean alive = energyValue > lifeThreshold;
        if (alive && (livingNeighbours < surviveMin || livingNeighbours > surviveMax)) {
            // Cell dies, lower energy state
            return -energyJump;
        } else if (alive && energyValue < highEnergyState - energyStep * 0.5) {
            // Cell stays alive, restore to high energy state
            return energyStep;
        } else if (alive && energyValue > highEnergyState + energyStep * 0.5) {
            // Cell stays alive, restore to high energy state
            return -energyStep;
        } else if (alive) {
            // Cell stays alive, maintain high energy state
            return 0;
        } else if (livingNeighbours >= birthMin && livingNeighbours <= birthMax) {
            // Cell is born, raise energy state
            return energyJump;
        } else if (energyValue > lowEnergyState) {
            // Cell stays dead, restore to low energy state
            return lowEnergyState - energyValue;
        } else if (energyValue < minEnergyState) {
            // Cell stays dead, restore low energy state
            return minEnergyState - energyValue + energyStep;
        } else {
            // Cell stays dead, maintain low energy state
            return 0;
        }
    }
}
