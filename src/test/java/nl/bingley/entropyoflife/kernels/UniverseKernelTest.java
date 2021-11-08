package nl.bingley.entropyoflife.kernels;

import com.aparapi.Range;
import nl.bingley.entropyoflife.config.properties.LifeProperties;
import nl.bingley.entropyoflife.config.properties.UniverseProperties;
import nl.bingley.entropyoflife.models.Universe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static nl.bingley.entropyoflife.UniverseTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UniverseKernelTest {

    private Universe universe;

    private Range range;
    private UniverseKernel universeKernel;
    private LifeProperties lifeProperties;

    @BeforeEach
    void setUp() {
        UniverseProperties universeProperties = mockGameOfLifeProperties(64);
        lifeProperties = universeProperties.getLifeProperties();
        universe = new Universe(universeProperties.getSize());
        range = Range.create2D(universeProperties.getSize(), universeProperties.getSize());
        universeKernel = new UniverseKernel(universe, universeProperties);
    }

    @Test
    public void testFirstPass() {
        float[][] energyMatrix = universe.energyMatrix;
        energyMatrix[1][2] = lifeProperties.getHighEnergyState();
        energyMatrix[2][2] = lifeProperties.getHighEnergyState();
        energyMatrix[3][2] = lifeProperties.getHighEnergyState();

        universeKernel.execute(range, 1);
        universeKernel.get(universe.deltaMatrix);

        float[][] deltaMatrix = universe.deltaMatrix;
        float energyJump = lifeProperties.getHighEnergyState() - lifeProperties.getLowEnergyState();
        assertEnergyValue(-energyJump, deltaMatrix[1][2]);
        assertEnergyValue(0, deltaMatrix[2][2]);
        assertEnergyValue(-energyJump, deltaMatrix[3][2]);
        assertEnergyValue(energyJump, deltaMatrix[2][1]);
        assertEnergyValue(energyJump, deltaMatrix[2][3]);
        assertEquals(0f, countTotalEnergy(deltaMatrix));
    }

    @Test
    public void testBothPasses() {
        float[][] energyMatrix = universe.energyMatrix;
        energyMatrix[1][2] = lifeProperties.getHighEnergyState();
        energyMatrix[2][2] = lifeProperties.getHighEnergyState();
        energyMatrix[3][2] = lifeProperties.getHighEnergyState();

        universeKernel.execute(range, 2);
        universeKernel.get(universe.energyMatrix);

        assertEnergyValue(0, energyMatrix[1][2]);
        assertEnergyValue(1, energyMatrix[2][2]);
        assertEnergyValue(0, energyMatrix[3][2]);
        assertEnergyValue(1, energyMatrix[2][1]);
        assertEnergyValue(1, energyMatrix[2][3]);
        assertEquals(3, countTotalEnergy(energyMatrix));
    }
}
