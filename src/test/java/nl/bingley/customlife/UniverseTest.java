package nl.bingley.customlife;

import nl.bingley.customlife.config.LifeProperties;
import nl.bingley.customlife.config.UniverseProperties;
import nl.bingley.customlife.model.Cell;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UniverseTest {

    private Universe universe;

    @Test
    public void testGameOfLifeCellDeath() {
        UniverseProperties universeProperties = mockGameOfLifeProperties(3);
        LifeProperties props = universeProperties.getLifeProperties();
        universe = new Universe(universeProperties);

        Cell cell = universe.findCell(1, 1);
        cell.value = props.getHighEnergyState();

        universe.nextGeneration();

        assertEquals(props.getLowEnergyState(), cell.value, createCellAssertMessage(cell));
        assertEquals(0, countTotalEnergy());
    }

    @Test
    public void testGameOfLifeCellBirth() {
        UniverseProperties universeProperties = mockGameOfLifeProperties(5);
        LifeProperties props = universeProperties.getLifeProperties();
        universe = new Universe(universeProperties);

        Cell cell1 = universe.findCell(1, 2);
        Cell cell2 = universe.findCell(2, 2);
        Cell cell3 = universe.findCell(3, 2);
        cell1.value = props.getHighEnergyState();
        cell2.value = props.getHighEnergyState();
        cell3.value = props.getHighEnergyState();

        universe.nextGeneration();

        assertEquals(props.getLowEnergyState(), cell1.value, createCellAssertMessage(cell1));
        assertEquals(props.getHighEnergyState(), cell2.value, createCellAssertMessage(cell2));
        assertEquals(props.getLowEnergyState(), cell3.value, createCellAssertMessage(cell3));

        Cell newCell1 = universe.findCell(2, 3);
        Cell newCell2 = universe.findCell(2, 1);
        assertEquals(props.getHighEnergyState(), newCell1.value, createCellAssertMessage(newCell1));
        assertEquals(props.getHighEnergyState(), newCell2.value, createCellAssertMessage(newCell2));
        assertEquals(props.getHighEnergyState() * 3, countTotalEnergy());
    }

    @Test
    public void testGameOfLifeCellLive() {
        UniverseProperties universeProperties = mockGameOfLifeProperties(4);
        LifeProperties props = universeProperties.getLifeProperties();
        universe = new Universe(universeProperties);

        Cell cell1 = universe.findCell(1, 2);
        Cell cell2 = universe.findCell(2, 2);
        Cell cell3 = universe.findCell(2, 1);
        Cell cell4 = universe.findCell(1, 1);
        cell1.value = props.getHighEnergyState();
        cell2.value = props.getHighEnergyState();
        cell3.value = props.getHighEnergyState();
        cell4.value = props.getHighEnergyState();

        universe.nextGeneration();

        assertEquals(props.getHighEnergyState(), cell1.value, createCellAssertMessage(cell1));
        assertEquals(props.getHighEnergyState(), cell2.value, createCellAssertMessage(cell2));
        assertEquals(props.getHighEnergyState(), cell3.value, createCellAssertMessage(cell3));
        assertEquals(props.getHighEnergyState(), cell4.value, createCellAssertMessage(cell4));

        assertEquals(props.getHighEnergyState() * 4, countTotalEnergy());
    }

    private float countTotalEnergy() {
        return Arrays.stream(universe.getAllCells())
                .flatMap(Arrays::stream)
                .map(fin -> fin.value).reduce(Float::sum).get();
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
        when(lifeProps.getEnergyNeighbourhoodRadius()).thenReturn(0);

        UniverseProperties universeProps = mock(UniverseProperties.class);
        when(universeProps.getSize()).thenReturn(size);
        when(universeProps.getLifeProperties()).thenReturn(lifeProps);
        when(universeProps.isRandomized()).thenReturn(false);
        return universeProps;
    }

    private String createCellAssertMessage(Cell cell) {
        return String.format("Cell at %s/%s had value %f", cell.x, cell.y, cell.value);
    }
}
