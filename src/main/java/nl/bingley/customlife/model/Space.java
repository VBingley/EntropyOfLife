package nl.bingley.customlife.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Any live cell with two or three live neighbours maintain high energy state
 * Any live cell over 1.0 drop to 1, distributing the excess among neighbours
 * All other live cells die, distributing 1/8 to each neighbour
 * Any dead cell with three live neighbours makes an energy jump up, taking energy from neighbours
 */
public class Space {

    public static final float aliveThreshold = 1 / 2f;
    public static final float highEnergyState = 2 / 3f;
    public static final float lowEnergyState = 1 / 3f;
    public static final float minEnergyState = 0f;

    public static final int radius = 3;

    public static final float energyJump = highEnergyState - lowEnergyState;

    public Cell[][] cells;

    private final Random random = new Random();

    public Space(int size) {
        cells = new Cell[size][size];
        initializeEmpty(size);
    }

    public Space(int size, int spawnSize) {
        cells = new Cell[size][size];
        initializeRandom(size, spawnSize);
    }

    public List<Cell> getAllCells() {
        return Arrays.stream(cells).flatMap(Arrays::stream).collect(Collectors.toList());
    }

    public void update() {
        // Prepare
        Arrays.stream(cells).flatMap(Arrays::stream)
                .forEach(cell -> {
                    cell.oldValue = cell.value;
                    cell.value = 0;
                });
        // Update
        Arrays.stream(cells).flatMap(Arrays::stream)
                //.parallel()
                .forEach(this::updateCell);
    }

    private void updateCell(Cell cell) {
        List<Cell> neighbours = getAllNeighbouringCells(cell);
        int aliveNeighbours = countAliveNeighbours(neighbours);
        if (isAlive(cell) && (aliveNeighbours > 3 || aliveNeighbours < 2)) {
            // Cell dies, lower energy state
            gainEnergy(cell, -energyJump, neighbours);
        } else if (isAlive(cell) && cell.oldValue != highEnergyState) {
            // Cell stays alive, restore to high energy state
            gainEnergy(cell, random.nextFloat() * (highEnergyState - cell.oldValue), neighbours);
        } else if (isAlive(cell)) {
            // Cell stays alive, maintain high energy state
            cell.value += cell.oldValue;
        } else if (aliveNeighbours == 3) {
            // Cell is born, raise energy state
            gainEnergy(cell, energyJump, neighbours);
        } else if (cell.oldValue > lowEnergyState) {
            // Cell stays dead, drain to low energy state
            gainEnergy(cell, lowEnergyState - cell.oldValue, neighbours);
        } else if (cell.oldValue < minEnergyState) {
            // Cell stays dead, maintain low energy state
            gainEnergy(cell, random.nextFloat() * minEnergyState - cell.oldValue, neighbours);
        } else {
            // Cell stays dead, maintain low energy state
            cell.value += cell.oldValue;
        }
    }

    private List<Cell> getAllNeighbouringCells(Cell centerCell) {
        List<Cell> neighbours = new ArrayList<>();
        for (int x = centerCell.x - 1; x <= centerCell.x + 1; x++) {
            for (int y = centerCell.y - 1; y <= centerCell.y + 1; y++) {
                if (x != centerCell.x || y != centerCell.y) {
                    neighbours.add(getCell(x, y));
                }
            }
        }
        return neighbours;
    }

    private int countAliveNeighbours(List<Cell> neighbours) {
        return (int) neighbours.stream()
                .filter(this::isAlive)
                .count();
    }

    private boolean isAlive(Cell cell) {
        return cell.oldValue > aliveThreshold;
    }

    private void gainEnergy(Cell cell, float energy, List<Cell> neighbours) {
        cell.value += cell.oldValue + energy;
        neighbours.forEach(neighbour -> neighbour.value -= energy * 0.125f);
    }

    private Cell getCell(int x, int y) {
        if (x < 0) x = cells.length - 1;
        if (x == cells.length) x = 0;
        if (y < 0) y = cells.length - 1;
        if (y == cells.length) y = 0;

        return cells[x][y];
    }

    private void initializeRandom(int size, int spawnSize) {
        Random random = new Random();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int margin = (int) (size * 0.5 - spawnSize * 0.5);
                if (x >= margin && x <= size - margin && y >= margin && y <= size - margin) {
                    cells[x][y] = new Cell(x, y, lowEnergyState + random.nextFloat() * (highEnergyState - lowEnergyState));
                } else {
                    cells[x][y] = new Cell(x, y, lowEnergyState);
                }
            }
        }
    }

    private void initializeEmpty(int size) {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                cells[x][y] = new Cell(x, y, minEnergyState);
            }
        }
    }
}
