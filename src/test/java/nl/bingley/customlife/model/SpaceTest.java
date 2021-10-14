package nl.bingley.customlife.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpaceTest {

    private Space space;

    @Test
    public void testDyingCell() {
        space = new Space(3);
        Cell cell = getCellAtPosition(1, 1);
        cell.value = Space.highEnergyState;

        space.update();

        assertEquals(Space.lowEnergyState, cell.value);
        space.getAllCells().stream()
                .filter(neighbour -> neighbour.x != cell.x || neighbour.y != cell.y)
                .forEach(neighbour ->
                        assertEquals((Space.highEnergyState - Space.lowEnergyState) * 0.125f, neighbour.value));
        assertEquals(Space.highEnergyState, space.getAllCells().stream().map(fin -> fin.value).reduce(Float::sum).get());
    }

    @Test
    public void testAntiCell() {
        space = new Space(3);
        Cell cell1 = getCellAtPosition(1, 1);
        Cell cell2 = getCellAtPosition(1, 2);
        cell1.value = 1;
        cell2.value = -1;

        space.update();

        assertEquals(0, space.getAllCells().stream().map(fin -> fin.value).reduce(Float::sum).get());
    }

    @Test
    public void testGrowCell() {
        space = new Space(8);
        Cell cell1 = getCellAtPosition(1, 2);
        Cell cell2 = getCellAtPosition(2, 2);
        Cell cell3 = getCellAtPosition(3, 2);
        cell1.value = Space.highEnergyState;
        cell2.value = Space.highEnergyState;
        cell3.value = Space.highEnergyState;

        space.update();

        assertEquals(Space.lowEnergyState * (1 / 4f), cell1.value);
        assertEquals(Space.highEnergyState - 1 / 16f, cell2.value);
        assertEquals(Space.lowEnergyState * (1 / 4f), cell3.value);

        Cell newCell1 = getCellAtPosition(2, 3);
        Cell newCell2 = getCellAtPosition(2, 1);
        assertEquals(Space.highEnergyState + 1 / 8f, newCell1.value);
        assertEquals(Space.highEnergyState + 1 / 8f, newCell2.value);
        assertEquals(Space.highEnergyState * 3, space.getAllCells().stream().map(fin -> fin.value).reduce(Float::sum).get());
    }

    private Cell getCellAtPosition(int x, int y) {
        return space.getAllCells().stream()
                .filter(cell -> cell.x == x && cell.y == y)
                .findFirst().get();
    }
}
