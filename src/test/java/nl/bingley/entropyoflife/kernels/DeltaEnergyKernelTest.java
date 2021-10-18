package nl.bingley.entropyoflife.kernels;

import com.aparapi.Range;
import nl.bingley.entropyoflife.config.LifeProperties;
import nl.bingley.entropyoflife.config.UniverseProperties;
import nl.bingley.entropyoflife.models.Universe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static nl.bingley.entropyoflife.UniverseTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

class DeltaEnergyKernelTest {

    private Universe universe;

    private Range range;
    private DeltaEnergyKernel deltaEnergyKernel;
    private LifeProperties lifeProperties;

    @BeforeEach
    void setUp() {
        UniverseProperties universeProperties = mockGameOfLifeProperties(64);
        lifeProperties = universeProperties.getLifeProperties();
        universe = new Universe(universeProperties.getSize());
        range = Range.create2D(universeProperties.getSize(), universeProperties.getSize());
        deltaEnergyKernel = new DeltaEnergyKernel(universe, universeProperties);
    }

    @Test
    public void testRunningOnOpenCL() {
        assertTrue(deltaEnergyKernel.isRunningCL(), "You really should be running on OpenCL...");
    }

    @Test
    public void testBasicTransformation() {
        float[][] energyMatrix = universe.energyMatrix;
        energyMatrix[1][2] = lifeProperties.getHighEnergyState();
        energyMatrix[2][2] = lifeProperties.getHighEnergyState();
        energyMatrix[3][2] = lifeProperties.getHighEnergyState();

        deltaEnergyKernel.execute(range);
        deltaEnergyKernel.get(universe.deltaMatrix);

        float[][] deltaMatrix = universe.deltaMatrix;
        assertEnergyValue(-lifeProperties.getEnergyJump(), deltaMatrix[1][2]);
        assertEnergyValue(0, deltaMatrix[2][2]);
        assertEnergyValue(-lifeProperties.getEnergyJump(), deltaMatrix[3][2]);
        assertEnergyValue(lifeProperties.getEnergyJump(), deltaMatrix[2][1]);
        assertEnergyValue(lifeProperties.getEnergyJump(), deltaMatrix[2][3]);
        assertEquals(0f, countTotalEnergy(deltaMatrix));
    }
}