package nl.bingley.entropyoflife.kernels;

import com.aparapi.Range;
import nl.bingley.entropyoflife.config.LifeProperties;
import nl.bingley.entropyoflife.config.UniverseProperties;
import nl.bingley.entropyoflife.models.Universe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static nl.bingley.entropyoflife.UniverseTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnergyKernelTest {

    private Universe universe;

    private Range range;
    private EnergyKernel energyKernel;
    private LifeProperties lifeProperties;

    @BeforeEach
    void setUp() {
        UniverseProperties universeProperties = mockGameOfLifeProperties(64);
        lifeProperties = universeProperties.getLifeProperties();
        universe = new Universe(universeProperties.getSize());
        range = Range.create2D(universeProperties.getSize(), universeProperties.getSize());
        energyKernel = new EnergyKernel(universe, universeProperties, 0, 1024, 1024);
    }

    @Test
    public void testBasicDeltaTransformation() {
        float[][] energyMatrix = universe.energyMatrix;
        energyMatrix[1][2] = lifeProperties.getHighEnergyState();
        energyMatrix[2][2] = lifeProperties.getHighEnergyState();
        energyMatrix[3][2] = lifeProperties.getHighEnergyState();

        energyKernel.execute(range);
        energyKernel.get(universe.deltaMatrix);

        float[][] deltaMatrix = universe.deltaMatrix;
        assertEnergyValue(-lifeProperties.getEnergyJump(), deltaMatrix[1][2]);
        assertEnergyValue(0, deltaMatrix[2][2]);
        assertEnergyValue(-lifeProperties.getEnergyJump(), deltaMatrix[3][2]);
        assertEnergyValue(lifeProperties.getEnergyJump(), deltaMatrix[2][1]);
        assertEnergyValue(lifeProperties.getEnergyJump(), deltaMatrix[2][3]);
        assertEquals(0f, countTotalEnergy(deltaMatrix));
    }

    @Test
    public void testBasicTransformation() {
        float[][] deltaMatrix = universe.deltaMatrix;
        deltaMatrix[1][2] = 1;
        deltaMatrix[2][2] = 0.75f;
        deltaMatrix[3][2] = 0.25f;

        energyKernel.prepareEnergyCalculation( 1024, 1024);
        energyKernel.execute(range);
        energyKernel.get(universe.energyMatrix);

        float[][] energyMatrix = universe.energyMatrix;
        assertEnergyValue(1, energyMatrix[1][2]);
        assertEnergyValue(0.75f, energyMatrix[2][2]);
        assertEnergyValue(0.25f, energyMatrix[3][2]);
        assertEquals(2f, countTotalEnergy(energyMatrix));
    }
}
