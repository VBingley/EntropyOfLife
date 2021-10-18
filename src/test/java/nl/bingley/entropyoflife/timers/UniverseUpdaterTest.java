package nl.bingley.entropyoflife.timers;

import nl.bingley.entropyoflife.UniversePanel;
import nl.bingley.entropyoflife.config.LifeProperties;
import nl.bingley.entropyoflife.config.UniverseProperties;
import nl.bingley.entropyoflife.kernels.DeltaEnergyKernel;
import nl.bingley.entropyoflife.kernels.EnergyKernel;
import nl.bingley.entropyoflife.models.Universe;
import org.junit.jupiter.api.Test;

import static nl.bingley.entropyoflife.UniverseTestUtil.*;
import static org.mockito.Mockito.mock;

public class UniverseUpdaterTest {

    private float[][] energyMatrix;
    private LifeProperties props;
    private UniverseUpdater universeUpdater;

    @Test
    public void testGameOfLifeDeath() {
        initUniverseUpdate(mockGameOfLifeProperties(3));

        energyMatrix[1][1] = props.getHighEnergyState();

        universeUpdater.updateUniverse();

        assertEnergyValue(props.getLowEnergyState(), energyMatrix[1][1]);
        assertEnergyValue(0, countTotalEnergy(energyMatrix));
    }

    @Test
    public void testGameOfLifeBirth() {
        initUniverseUpdate(mockGameOfLifeProperties(5));

        energyMatrix[1][2] = props.getHighEnergyState();
        energyMatrix[2][2] = props.getHighEnergyState();
        energyMatrix[3][2] = props.getHighEnergyState();

        universeUpdater.updateUniverse();

        assertEnergyValue(props.getLowEnergyState(), energyMatrix[1][2]);
        assertEnergyValue(props.getHighEnergyState(), energyMatrix[2][2]);
        assertEnergyValue(props.getLowEnergyState(), energyMatrix[3][2]);

        assertEnergyValue(props.getHighEnergyState(), energyMatrix[2][3]);
        assertEnergyValue(props.getHighEnergyState(), energyMatrix[2][1]);
        assertEnergyValue(props.getHighEnergyState() * 3, countTotalEnergy(energyMatrix));
    }

    @Test
    public void testGameOfLifeSurvive() {
        initUniverseUpdate(mockGameOfLifeProperties(4));

        energyMatrix[1][2] = props.getHighEnergyState();
        energyMatrix[2][2] = props.getHighEnergyState();
        energyMatrix[2][1] = props.getHighEnergyState();
        energyMatrix[1][1] = props.getHighEnergyState();

        universeUpdater.updateUniverse();

        assertEnergyValue(props.getHighEnergyState(), energyMatrix[1][2]);
        assertEnergyValue(props.getHighEnergyState(), energyMatrix[2][2]);
        assertEnergyValue(props.getHighEnergyState(), energyMatrix[2][1]);
        assertEnergyValue(props.getHighEnergyState(), energyMatrix[1][1]);
        assertEnergyValue(props.getHighEnergyState() * 4, countTotalEnergy(energyMatrix));
    }

    @Test
    public void testEntropyOfLifeDeath() {
        initUniverseUpdate(mockEntropyOfLifeProperties(6));

        energyMatrix[2][2] = props.getHighEnergyState();

        universeUpdater.updateUniverse();

        assertEnergyValue(props.getLowEnergyState(), energyMatrix[2][2]);
        assertEnergyValue(props.getHighEnergyState(), countTotalEnergy(energyMatrix));
    }

    @Test
    public void testEntropyOfLifeBirth() {
        initUniverseUpdate(mockEntropyOfLifeProperties(7));

        energyMatrix[2][3] = props.getHighEnergyState();
        energyMatrix[3][3] = props.getHighEnergyState();
        energyMatrix[4][3] = props.getHighEnergyState();

        universeUpdater.updateUniverse();

        float energyJump = props.getHighEnergyState() - props.getLowEnergyState();
        float distributedEnergyJump = energyJump / 24f;
        assertEnergyValue(props.getLowEnergyState() - distributedEnergyJump * 2 + distributedEnergyJump, energyMatrix[2][3]);
        assertEnergyValue(props.getHighEnergyState(), energyMatrix[3][3]);
        assertEnergyValue(props.getLowEnergyState() - distributedEnergyJump * 2 + distributedEnergyJump, energyMatrix[4][3]);

        assertEnergyValue(energyJump + distributedEnergyJump * 2 - distributedEnergyJump, energyMatrix[3][4]);
        assertEnergyValue(energyJump + distributedEnergyJump * 2 - distributedEnergyJump, energyMatrix[3][2]);
        assertEnergyValue(props.getHighEnergyState() * 3, countTotalEnergy(energyMatrix));
    }

    @Test
    public void testEntropyOfLifeSurvive() {
        initUniverseUpdate(mockEntropyOfLifeProperties(6));

        energyMatrix[1][2] = props.getHighEnergyState();
        energyMatrix[2][2] = props.getHighEnergyState();
        energyMatrix[2][1] = props.getHighEnergyState();
        energyMatrix[1][1] = props.getHighEnergyState();

        universeUpdater.updateUniverse();

        assertEnergyValue(props.getHighEnergyState(), energyMatrix[1][2]);
        assertEnergyValue(props.getHighEnergyState(), energyMatrix[2][2]);
        assertEnergyValue(props.getHighEnergyState(), energyMatrix[2][1]);
        assertEnergyValue(props.getHighEnergyState(), energyMatrix[1][1]);
        assertEnergyValue(props.getHighEnergyState() * 4, countTotalEnergy(energyMatrix));
    }

    private void initUniverseUpdate(UniverseProperties universeProperties) {
        Universe universe = new Universe(universeProperties.getSize());

        props = universeProperties.getLifeProperties();
        energyMatrix = universe.energyMatrix;
        DeltaEnergyKernel deltaEnergyKernel = new DeltaEnergyKernel(universe, universeProperties);
        EnergyKernel energyKernel = new EnergyKernel(universe, universeProperties);
        universeUpdater = new UniverseUpdater(universe, mock(UniversePanel.class), universeProperties, deltaEnergyKernel, energyKernel);
    }
}
