package nl.bingley.customlife.model;

import nl.bingley.customlife.CellStateUtil;
import nl.bingley.customlife.config.LifeProperties;
import nl.bingley.customlife.config.UniverseProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Space {

    private final LifeProperties props;

    private final CellStateUtil cellStateUtil;

    public Cell[][] cells;

    public Space(UniverseProperties universeProperties, boolean fillRandom) {
        props = universeProperties.getLifeProperties();
        cellStateUtil = new CellStateUtil(props);

        int size = universeProperties.getSize();
        cells = new Cell[size][size];
        if (fillRandom) {
            initializeRandom(size, universeProperties.getSpawnSize());
        } else {
            initializeEmpty(size);
        }
    }

    public Cell[][] getAllCells() {
        return cells.clone();
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
        int livingNeighbours = cellStateUtil.countLivingCells(findAllNeighbours(cell, props.getLifeNeighbourhoodRadius()));
        float energyDelta = cellStateUtil.calculateEnergyDelta(cell, livingNeighbours);
        if (energyDelta != 0) {
            cellStateUtil.gainEnergy(energyDelta, cell, findAllNeighbours(cell, props.getEnergyNeighbourhoodRadius()));
        } else {
            cell.value += cell.oldValue;
        }
    }

    private List<Cell> findAllNeighbours(Cell centerCell, int radius) {
        List<Cell> neighbours = new ArrayList<>();
        for (int x = centerCell.x - radius; x <= centerCell.x + radius; x++) {
            for (int y = centerCell.y - radius; y <= centerCell.y + radius; y++) {
                if (x != centerCell.x || y != centerCell.y) {
                    neighbours.add(findCell(x, y));
                }
            }
        }
        return neighbours;
    }

    protected Cell findCell(int x, int y) {
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
                    cells[x][y] = new Cell(x, y, random.nextFloat() * props.getLowEnergyState());
                }
            }
        }
    }

    private void initializeEmpty(int size) {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                cells[x][y] = new Cell(x, y, props.getMinEnergyState());
            }
        }
    }
}
