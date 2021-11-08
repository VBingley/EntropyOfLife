package nl.bingley.entropyoflife.actionlisteners;

import nl.bingley.entropyoflife.application.windowapplication.UniversePanel;
import nl.bingley.entropyoflife.config.properties.LifeProperties;
import nl.bingley.entropyoflife.config.properties.UniverseProperties;
import nl.bingley.entropyoflife.kernels.UniverseKernel;
import nl.bingley.entropyoflife.models.Universe;
import org.junit.jupiter.api.Test;

import static nl.bingley.entropyoflife.UniverseTestUtil.*;
import static org.mockito.Mockito.mock;

public class UniverseUpdateActionListenerTest {

    private float[][] energyMatrix;
    private LifeProperties props;
    private UniverseUpdateActionListener universeUpdateActionListener;
    private UniverseKernel universeKernel;

    @Test
    public void testGameOfLifeDeath() {
        initUniverseUpdate(mockGameOfLifeProperties(3));

        energyMatrix[1][1] = props.getHighEnergyState();

        universeUpdateActionListener.updateUniverse();

        assertEnergyValue(props.getLowEnergyState(), energyMatrix[1][1]);
        assertEnergyValue(0, countTotalEnergy(energyMatrix));
    }

    @Test
    public void testGameOfLifeBirth() {
        initUniverseUpdate(mockGameOfLifeProperties(5));

        energyMatrix[1][2] = props.getHighEnergyState();
        energyMatrix[2][2] = props.getHighEnergyState();
        energyMatrix[3][2] = props.getHighEnergyState();

        universeUpdateActionListener.updateUniverse();

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

        universeUpdateActionListener.updateUniverse();

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

        universeUpdateActionListener.updateUniverse();

        assertEnergyValue(props.getLowEnergyState(), energyMatrix[2][2]);
        assertEnergyValue(props.getHighEnergyState(), countTotalEnergy(energyMatrix));
    }

    @Test
    public void testEntropyOfLifeBirth() {
        initUniverseUpdate(mockEntropyOfLifeProperties(7));

        energyMatrix[2][3] = props.getHighEnergyState();
        energyMatrix[3][3] = props.getHighEnergyState();
        energyMatrix[4][3] = props.getHighEnergyState();

        universeUpdateActionListener.updateUniverse();

        float distributedDyingCellEnergy = (props.getHighEnergyState() - props.getLowEnergyState()) / 24f;
        float distributedBornCellEnergy = props.getHighEnergyState() / 24f;
        assertEnergyValue(props.getLowEnergyState() - distributedBornCellEnergy * 2 + distributedDyingCellEnergy, energyMatrix[2][3]);
        assertEnergyValue(props.getHighEnergyState() - distributedBornCellEnergy * 2 + distributedDyingCellEnergy * 2, energyMatrix[3][3]);
        assertEnergyValue(props.getLowEnergyState() - distributedBornCellEnergy * 2 + distributedDyingCellEnergy, energyMatrix[4][3]);

        assertEnergyValue(props.getHighEnergyState() + distributedDyingCellEnergy * 2 - distributedBornCellEnergy, energyMatrix[3][4]);
        assertEnergyValue(props.getHighEnergyState() + distributedDyingCellEnergy * 2 - distributedBornCellEnergy, energyMatrix[3][2]);
        assertEnergyValue(props.getHighEnergyState() * 3, countTotalEnergy(energyMatrix));
    }

    @Test
    public void testEntropyOfLifeSurvive() {
        initUniverseUpdate(mockEntropyOfLifeProperties(6));

        energyMatrix[1][2] = props.getHighEnergyState();
        energyMatrix[2][2] = props.getHighEnergyState();
        energyMatrix[2][1] = props.getHighEnergyState();
        energyMatrix[1][1] = props.getHighEnergyState();

        universeUpdateActionListener.updateUniverse();

        assertEnergyValue(props.getHighEnergyState(), energyMatrix[1][2]);
        assertEnergyValue(props.getHighEnergyState(), energyMatrix[2][2]);
        assertEnergyValue(props.getHighEnergyState(), energyMatrix[2][1]);
        assertEnergyValue(props.getHighEnergyState(), energyMatrix[1][1]);
        assertEnergyValue(props.getHighEnergyState() * 4, countTotalEnergy(energyMatrix));
    }

    private void initUniverseUpdate(UniverseProperties universeProperties) {
        int size = universeProperties.getSize();
        Universe universe = new Universe(size);

        props = universeProperties.getLifeProperties();
        energyMatrix = universe.energyMatrix;
        universeKernel = new UniverseKernel(universe, universeProperties);

        universeUpdateActionListener = new UniverseUpdateActionListener(universe, mock(UniversePanel.class), universeProperties, universeKernel);
    }
}
