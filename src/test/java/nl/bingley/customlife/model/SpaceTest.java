package nl.bingley.customlife.model;

import nl.bingley.customlife.config.LifeProperties;
import nl.bingley.customlife.config.UniverseProperties;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SpaceTest {

    private Space space;

    @Test
    public void testGameOfLifeCellDeath() {
        UniverseProperties universeProperties = mockGameOfLifeProperties(3);
        LifeProperties props = universeProperties.getLifeProperties();
        space = new Space(universeProperties, false);

        Cell cell = space.findCell(1, 1);
        cell.value = props.getHighEnergyState();

        space.update();

        assertEquals(props.getLowEnergyState(), cell.value, createCellAssertMessage(cell));
        Arrays.stream(space.getAllCells())
                .flatMap(Arrays::stream)
                .filter(neighbour -> neighbour.x != cell.x || neighbour.y != cell.y)
                .forEach(neighbour -> assertEquals(0, neighbour.value, createCellAssertMessage(neighbour)));
    }

    @Test
    public void testGameOfLifeCellBirth() {
        UniverseProperties universeProperties = mockGameOfLifeProperties(5);
        LifeProperties props = universeProperties.getLifeProperties();
        space = new Space(universeProperties, false);

        Cell cell1 = space.findCell(1, 2);
        Cell cell2 = space.findCell(2, 2);
        Cell cell3 = space.findCell(3, 2);
        cell1.value = props.getHighEnergyState();
        cell2.value = props.getHighEnergyState();
        cell3.value = props.getHighEnergyState();

        space.update();

        assertEquals(props.getLowEnergyState(), cell1.value, createCellAssertMessage(cell1));
        assertEquals(props.getHighEnergyState(), cell2.value, createCellAssertMessage(cell2));
        assertEquals(props.getLowEnergyState(), cell3.value, createCellAssertMessage(cell3));

        Cell newCell1 = space.findCell(2, 3);
        Cell newCell2 = space.findCell(2, 1);
        assertEquals(props.getHighEnergyState(), newCell1.value, createCellAssertMessage(newCell1));
        assertEquals(props.getHighEnergyState(), newCell2.value, createCellAssertMessage(newCell2));
        assertEquals(props.getHighEnergyState() * 3, countTotalEnergy());
    }

    @Test
    public void testGameOfLifeCellLive() {
        UniverseProperties universeProperties = mockGameOfLifeProperties(4);
        LifeProperties props = universeProperties.getLifeProperties();
        space = new Space(universeProperties, false);

        Cell cell1 = space.findCell(1, 2);
        Cell cell2 = space.findCell(2, 2);
        Cell cell3 = space.findCell(2, 1);
        Cell cell4 = space.findCell(1, 1);
        cell1.value = props.getHighEnergyState();
        cell2.value = props.getHighEnergyState();
        cell3.value = props.getHighEnergyState();
        cell4.value = props.getHighEnergyState();

        space.update();

        assertEquals(props.getHighEnergyState(), cell1.value, createCellAssertMessage(cell1));
        assertEquals(props.getHighEnergyState(), cell2.value, createCellAssertMessage(cell2));
        assertEquals(props.getHighEnergyState(), cell3.value, createCellAssertMessage(cell3));
        assertEquals(props.getHighEnergyState(), cell4.value, createCellAssertMessage(cell4));

        assertEquals(props.getHighEnergyState() * 4, countTotalEnergy());
    }

    private float countTotalEnergy() {
        return Arrays.stream(space.getAllCells())
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
        return universeProps;
    }

    private String createCellAssertMessage(Cell cell) {
        return String.format("Cell at %s/%s had value %f", cell.x, cell.y, cell.value);
    }
}
