package nl.bingley.entropyoflife.kernels;

import com.aparapi.Kernel;
import nl.bingley.entropyoflife.config.LifeProperties;
import nl.bingley.entropyoflife.config.UniverseProperties;
import nl.bingley.entropyoflife.models.Universe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnergyKernel extends Kernel {

    private final float[][] deltaMatrix;
    private final float[][] energyMatrix;

    private final int universeSize;
    private final int energyNeighbourhoodRadius;
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

    private int runCalc;
    private int imageWidth;
    private int imageHeight;

    public EnergyKernel(Universe universe, UniverseProperties universeProperties,
                        @Value("0") int runCalc, @Value("1024") int imageWidth, @Value("1024") int imageHeight) {
        super();
        deltaMatrix = universe.deltaMatrix;
        energyMatrix = universe.energyMatrix;
        universeSize = universe.getSize();
        LifeProperties properties = universeProperties.getLifeProperties();
        energyNeighbourhoodRadius = properties.getEnergyNeighbourhoodRadius();
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
        this.runCalc = runCalc;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public void prepareDeltaCalculation() {
        runCalc = 0;
    }

    public void prepareEnergyCalculation(int imageWidth, int imageHeight) {
        get(deltaMatrix);
        runCalc = 1;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    // Calculate the next energy value for value at x,y
    @Override
    public void run() {
        int x = getGlobalId(0);
        int y = getGlobalId(1);
        if (runCalc == 0) {
            deltaMatrix[x][y] = calculateOwnEnergyDelta(x, y);
        } else {
            energyMatrix[x][y] += deltaMatrix[x][y] - gatherEnergyDeltaFromNeighbourhood(x, y);
        }
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
