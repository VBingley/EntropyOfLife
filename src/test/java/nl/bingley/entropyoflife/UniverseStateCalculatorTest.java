package nl.bingley.entropyoflife;

import nl.bingley.entropyoflife.config.LifeProperties;
import nl.bingley.entropyoflife.config.UniverseProperties;
import nl.bingley.entropyoflife.models.Cell;
import nl.bingley.entropyoflife.models.Universe;
import nl.bingley.entropyoflife.services.CellStateCalculator;
import nl.bingley.entropyoflife.services.UniverseStateCalculator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UniverseStateCalculatorTest {

    private static final float floatRoundingErrorMargin = 0.000001f;

    private UniverseStateCalculator universeStateCalculator;
    private Universe universe;

    @Test
    public void testGameOfLifeDeath() {
        UniverseProperties universeProperties = mockGameOfLifeProperties(3);
        LifeProperties props = universeProperties.getLifeProperties();
        CellStateCalculator cellStateCalculator = new CellStateCalculator(universeProperties);
        universeStateCalculator = new UniverseStateCalculator(cellStateCalculator, universeProperties);
        universe = universeStateCalculator.getUniverse();

        universe.getEnergyCell(1, 1).value = props.getHighEnergyState();

        universeStateCalculator.nextGeneration();

        Cell cell = universe.getEnergyCell(1, 1);
        assertCellEnergy(props.getLowEnergyState(), cell);
        assertTotalEnergy(0, countTotalEnergy());
    }

    @Test
    public void testGameOfLifeBirth() {
        UniverseProperties universeProperties = mockGameOfLifeProperties(5);
        LifeProperties props = universeProperties.getLifeProperties();
        CellStateCalculator cellStateCalculator = new CellStateCalculator(universeProperties);
        universeStateCalculator = new UniverseStateCalculator(cellStateCalculator, universeProperties);
        universe = universeStateCalculator.getUniverse();

        Cell cell1 = universe.getEnergyCell(1, 2);
        Cell cell2 = universe.getEnergyCell(2, 2);
        Cell cell3 = universe.getEnergyCell(3, 2);
        cell1.value = props.getHighEnergyState();
        cell2.value = props.getHighEnergyState();
        cell3.value = props.getHighEnergyState();

        universeStateCalculator.nextGeneration();

        assertCellEnergy(props.getLowEnergyState(), cell1);
        assertCellEnergy(props.getHighEnergyState(), cell2);
        assertCellEnergy(props.getLowEnergyState(), cell3);

        Cell newCell1 = universe.getEnergyCell(2, 3);
        Cell newCell2 = universe.getEnergyCell(2, 1);
        assertCellEnergy(props.getHighEnergyState(), newCell1);
        assertCellEnergy(props.getHighEnergyState(), newCell2);
        assertTotalEnergy(props.getHighEnergyState() * 3, countTotalEnergy());
    }

    @Test
    public void testGameOfLifeSurvive() {
        UniverseProperties universeProperties = mockGameOfLifeProperties(4);
        LifeProperties props = universeProperties.getLifeProperties();
        CellStateCalculator cellStateCalculator = new CellStateCalculator(universeProperties);
        universeStateCalculator = new UniverseStateCalculator(cellStateCalculator, universeProperties);
        universe = universeStateCalculator.getUniverse();

        Cell cell1 = universe.getEnergyCell(1, 2);
        Cell cell2 = universe.getEnergyCell(2, 2);
        Cell cell3 = universe.getEnergyCell(2, 1);
        Cell cell4 = universe.getEnergyCell(1, 1);
        cell1.value = props.getHighEnergyState();
        cell2.value = props.getHighEnergyState();
        cell3.value = props.getHighEnergyState();
        cell4.value = props.getHighEnergyState();

        universeStateCalculator.nextGeneration();

        assertCellEnergy(props.getHighEnergyState(), cell1);
        assertCellEnergy(props.getHighEnergyState(), cell2);
        assertCellEnergy(props.getHighEnergyState(), cell3);
        assertCellEnergy(props.getHighEnergyState(), cell3);
        assertTotalEnergy(props.getHighEnergyState() * 4, countTotalEnergy());
    }

    @Test
    public void testEntropyOfLifeDeath() {
        UniverseProperties universeProperties = mockEntropyOfLifeProperties(5);
        LifeProperties props = universeProperties.getLifeProperties();
        CellStateCalculator cellStateCalculator = new CellStateCalculator(universeProperties);
        universeStateCalculator = new UniverseStateCalculator(cellStateCalculator, universeProperties);
        universe = universeStateCalculator.getUniverse();

        universe.getEnergyCell(2, 2).value = props.getHighEnergyState();

        universeStateCalculator.nextGeneration();

        Cell cell = universe.getEnergyCell(2, 2);
        assertCellEnergy(props.getLowEnergyState(), cell);
        assertTotalEnergy(props.getHighEnergyState(), countTotalEnergy());
    }

    @Test
    public void testEntropyOfLifeBirth() {
        UniverseProperties universeProperties = mockEntropyOfLifeProperties(7);
        LifeProperties props = universeProperties.getLifeProperties();
        CellStateCalculator cellStateCalculator = new CellStateCalculator(universeProperties);
        universeStateCalculator = new UniverseStateCalculator(cellStateCalculator, universeProperties);
        universe = universeStateCalculator.getUniverse();

        Cell cell1 = universe.getEnergyCell(2, 3);
        Cell cell2 = universe.getEnergyCell(3, 3);
        Cell cell3 = universe.getEnergyCell(4, 3);
        cell1.value = props.getHighEnergyState();
        cell2.value = props.getHighEnergyState();
        cell3.value = props.getHighEnergyState();

        universeStateCalculator.nextGeneration();

        float energyJump = props.getHighEnergyState() - props.getLowEnergyState();
        float distributedEnergyJump = energyJump / 24f;
        assertCellEnergy(props.getLowEnergyState() - distributedEnergyJump * 2 + distributedEnergyJump, cell1);
        assertCellEnergy(props.getHighEnergyState(), cell2);
        assertCellEnergy(props.getLowEnergyState() - distributedEnergyJump * 2 + distributedEnergyJump, cell3);

        Cell newCell1 = universe.getEnergyCell(3, 4);
        Cell newCell2 = universe.getEnergyCell(3, 2);
        assertCellEnergy(energyJump + distributedEnergyJump * 2 - distributedEnergyJump, newCell1);
        assertCellEnergy(energyJump + distributedEnergyJump * 2 - distributedEnergyJump, newCell2);
        assertTotalEnergy(props.getHighEnergyState() * 3, countTotalEnergy());
    }

    @Test
    public void testEntropyOfLifeSurvive() {
        UniverseProperties universeProperties = mockEntropyOfLifeProperties(6);
        LifeProperties props = universeProperties.getLifeProperties();
        CellStateCalculator cellStateCalculator = new CellStateCalculator(universeProperties);
        universeStateCalculator = new UniverseStateCalculator(cellStateCalculator, universeProperties);
        universe = universeStateCalculator.getUniverse();

        Cell cell1 = universe.getEnergyCell(1, 2);
        Cell cell2 = universe.getEnergyCell(2, 2);
        Cell cell3 = universe.getEnergyCell(2, 1);
        Cell cell4 = universe.getEnergyCell(1, 1);
        cell1.value = props.getHighEnergyState();
        cell2.value = props.getHighEnergyState();
        cell3.value = props.getHighEnergyState();
        cell4.value = props.getHighEnergyState();

        universeStateCalculator.nextGeneration();

        assertCellEnergy(props.getHighEnergyState(), cell1);
        assertCellEnergy(props.getHighEnergyState(), cell2);
        assertCellEnergy(props.getHighEnergyState(), cell3);
        assertCellEnergy(props.getHighEnergyState(), cell3);
        assertTotalEnergy(props.getHighEnergyState() * 4, countTotalEnergy());
    }

    private float countTotalEnergy() {
        return Arrays.stream(universe.getEnergyMap())
                .flatMap(Arrays::stream)
                .map(fin -> fin.value).reduce(Float::sum).get();
    }

    private void assertTotalEnergy(float expected, float actual) {
        assertTrue(Math.abs(expected - actual) < floatRoundingErrorMargin,
                String.format("Expected value %f, actual value %f", expected, actual));
    }

    private void assertCellEnergy(float expected, Cell cell) {
        assertTrue(Math.abs(expected - cell.value) < floatRoundingErrorMargin,
                String.format("Expected cell at %s,%s to have %f, actual value %f", cell.x, cell.y, expected, cell.value));
    }

    private UniverseProperties mockGameOfLifeProperties(int size) {
        LifeProperties lifeProps = mock(LifeProperties.class);
        when(lifeProps.getLifeNeighbourhoodRadius()).thenReturn(1);
        when(lifeProps.getBirthMax()).thenReturn(3);
        when(lifeProps.getBirthMin()).thenReturn(3);
        when(lifeProps.getSurviveMax()).thenReturn(3);
        when(lifeProps.getSurviveMin()).thenReturn(2);
        when(lifeProps.getLifeEnergyThreshold()).thenReturn(0.5f);
        when(lifeProps.getHighEnergyState()).thenReturn(1f);
        when(lifeProps.getLowEnergyState()).thenReturn(0f);
        when(lifeProps.getMinEnergyState()).thenReturn(0f);
        when(lifeProps.getEnergyJump()).thenReturn(1f);
        when(lifeProps.getEnergyNeighbourhoodRadius()).thenReturn(0);

        UniverseProperties universeProps = mock(UniverseProperties.class);
        when(universeProps.getSize()).thenReturn(size);
        when(universeProps.getLifeProperties()).thenReturn(lifeProps);
        when(universeProps.isRandomized()).thenReturn(false);
        return universeProps;
    }

    private UniverseProperties mockEntropyOfLifeProperties(int size) {
        LifeProperties lifeProps = mock(LifeProperties.class);
        when(lifeProps.getLifeNeighbourhoodRadius()).thenReturn(1);
        when(lifeProps.getBirthMax()).thenReturn(3);
        when(lifeProps.getBirthMin()).thenReturn(3);
        when(lifeProps.getSurviveMax()).thenReturn(3);
        when(lifeProps.getSurviveMin()).thenReturn(2);
        when(lifeProps.getLifeEnergyThreshold()).thenReturn(0.5f);
        when(lifeProps.getHighEnergyState()).thenReturn(0.75f);
        when(lifeProps.getLowEnergyState()).thenReturn(0.125f);
        when(lifeProps.getMinEnergyState()).thenReturn(0f);
        when(lifeProps.getEnergyJump()).thenReturn(0.625f);
        when(lifeProps.getEnergyStep()).thenReturn(0.125f);
        when(lifeProps.getEnergyNeighbourhoodRadius()).thenReturn(2);

        UniverseProperties universeProps = mock(UniverseProperties.class);
        when(universeProps.getSize()).thenReturn(size);
        when(universeProps.getLifeProperties()).thenReturn(lifeProps);
        when(universeProps.isRandomized()).thenReturn(false);
        return universeProps;
    }
}
