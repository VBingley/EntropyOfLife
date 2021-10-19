package nl.bingley.entropyoflife.kernels;

import com.aparapi.Kernel;
import nl.bingley.entropyoflife.config.properties.LifeProperties;
import nl.bingley.entropyoflife.config.properties.UniverseProperties;
import nl.bingley.entropyoflife.models.Universe;
import org.springframework.stereotype.Component;

@Component
public class UniverseKernel extends Kernel {

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

    public UniverseKernel(Universe universe, UniverseProperties universeProperties) {
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
    }

    @Override
    public void run() {
        int cellX = getGlobalId(0);
        int cellY = getGlobalId(1);
        int pass = getPassId();
        if (pass % 2 == 0) {
            int livingNeighbours = countLivingNeighbours(cellX, cellY);
            deltaMatrix[cellX][cellY] = calculateDelta(energyMatrix[cellX][cellY], livingNeighbours);
        } else if (pass % 2 == 1) {
            energyMatrix[cellX][cellY] += deltaMatrix[cellX][cellY] - sumNeighbourhoodDelta(cellX, cellY);
        }
    }

    private int countLivingNeighbours(int cellX, int cellY) {
        int livingNeighbours = 0;
        for (int x = cellX - lifeNeighbourhoodRadius; x <= cellX + lifeNeighbourhoodRadius; x++) {
            for (int y = cellY - lifeNeighbourhoodRadius; y <= cellY + lifeNeighbourhoodRadius; y++) {
                int posX = wrapUniverse(x, universeSize);
                int posY = wrapUniverse(y, universeSize);
                if ((x != cellX || y != cellY) && energyMatrix[posX][posY] > lifeThreshold) {
                    livingNeighbours += 1;
                }
            }
        }
        return livingNeighbours;
    }

    private float calculateDelta(float energyValue, int livingNeighbours) {
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

    private float sumNeighbourhoodDelta(int cellX, int cellY) {
        if (energyNeighbourhoodRadius == 0) {
            return 0;
        }
        float neighbourhoodDelta = 0;
        for (int x = cellX - energyNeighbourhoodRadius; x <= cellX + energyNeighbourhoodRadius; x++) {
            for (int y = cellY - energyNeighbourhoodRadius; y <= cellY + energyNeighbourhoodRadius; y++) {
                int posX = wrapUniverse(x, universeSize);
                int posY = wrapUniverse(y, universeSize);
                if ((x != cellX || y != cellY)) {
                    neighbourhoodDelta += deltaMatrix[posX][posY];
                }
            }
        }
        return neighbourhoodDelta / (pow(2 * energyNeighbourhoodRadius + 1, 2) - 1);
    }

    private int wrapUniverse(int coordinate, int universeSize) {
        int trueCoordinate = coordinate < 0 ? universeSize + coordinate : coordinate;
        trueCoordinate = coordinate >= universeSize ? coordinate - universeSize : trueCoordinate;
        return trueCoordinate;
    }
}
