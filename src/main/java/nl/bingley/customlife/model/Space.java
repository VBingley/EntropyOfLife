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

    public static final float lifeThreshold = 1 / 2f;
    public static final float highEnergyState = 6 / 8f;
    public static final float lowEnergyState = 1 / 8f;
    public static final float minEnergyState = 0f;

    public static final int birthRadius = 5;
    public static final int energyRadius = 0;

    public static final int birthMax = 45;
    public static final int birthMin = 34;
    public static final int liveMax = 57;
    public static final int liveMin = 33;

    public static final float energyJump = highEnergyState - lowEnergyState;

    public Cell[][] cells;

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
                .parallel()
                .forEach(this::updateCell);
    }

    private void updateCell(Cell cell) {
        int livingNeighbours = countLivingNeighbours(cell);

        List<Cell> energyNeighbours = getAllNeighbouringCells(cell, energyRadius);
        if (isAlive(cell) && (livingNeighbours < liveMin || livingNeighbours > liveMax)) {
            // Cell dies, lower energy state
            gainEnergy(cell, -energyJump, energyNeighbours);
        } else if (isAlive(cell) && cell.oldValue != highEnergyState) {
            // Cell stays alive, restore to high energy state
            gainEnergy(cell, highEnergyState - cell.oldValue, energyNeighbours);
        } else if (isAlive(cell)) {
            // Cell stays alive, maintain high energy state
            cell.value += cell.oldValue;
        } else if (livingNeighbours >= birthMin && livingNeighbours <= birthMax) {
            // Cell is born, raise energy state
            gainEnergy(cell, energyJump, energyNeighbours);
        } else if (cell.oldValue > lowEnergyState) {
            // Cell stays dead, drain to low energy state
            gainEnergy(cell, lowEnergyState - cell.oldValue, energyNeighbours);
        } else if (cell.oldValue < minEnergyState) {
            // Cell stays dead, maintain low energy state
            gainEnergy(cell, minEnergyState - cell.oldValue, energyNeighbours);
        } else {
            // Cell stays dead, maintain low energy state
            cell.value += cell.oldValue;
        }
    }

    private List<Cell> getAllNeighbouringCells(Cell centerCell, int radius) {
        List<Cell> neighbours = new ArrayList<>();
        for (int x = centerCell.x - radius; x <= centerCell.x + radius; x++) {
            for (int y = centerCell.y - radius; y <= centerCell.y + radius; y++) {
                if (x != centerCell.x || y != centerCell.y) {
                    neighbours.add(getCell(x, y));
                }
            }
        }
        return neighbours;
    }

    private int countLivingNeighbours(Cell centerCell) {
        return (int)getAllNeighbouringCells(centerCell, birthRadius).stream().filter(this::isAlive).count();
    }

    private boolean isAlive(Cell cell) {
        return cell.oldValue > lifeThreshold;
    }

    private void gainEnergy(Cell cell, float energy, List<Cell> neighbours) {
        cell.value += cell.oldValue + energy;
        neighbours.forEach(neighbour -> neighbour.value -= energy * (1f / neighbours.size()));
    }

    private Cell getCell(int x, int y) {
        if (x < 0) x = cells.length + x;
        if (x >= cells.length) x = x - cells.length;
        if (y < 0) y = cells.length + y;
        if (y >= cells.length) y = y - cells.length;

        return cells[x][y];
    }

    private void initializeRandom(int size, int spawnSize) {
        Random random = new Random();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int margin = (int) (size * 0.5 - spawnSize * 0.5);
                if (x >= margin && x <= size - margin && y >= margin && y <= size - margin) {
                    cells[x][y] = new Cell(x, y, random.nextFloat());
                } else {
                    cells[x][y] = new Cell(x, y, random.nextFloat() * lowEnergyState);
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
