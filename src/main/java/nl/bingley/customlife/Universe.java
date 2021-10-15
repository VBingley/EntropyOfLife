package nl.bingley.customlife;

import nl.bingley.customlife.config.LifeProperties;
import nl.bingley.customlife.config.UniverseProperties;
import nl.bingley.customlife.model.Cell;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class Universe {

    private final UniverseProperties universeProperties;
    private final LifeProperties props;
    private final CellStateUtil cellStateUtil;
    private boolean paused = false;

    private int genPerSec = 0;
    private int generation;

    public Cell[][] cells;

    public Universe(UniverseProperties properties) {
        this.universeProperties = properties;
        props = universeProperties.getLifeProperties();
        cellStateUtil = new CellStateUtil(props);

        int size = universeProperties.getSize();
        cells = new Cell[size][size];
        if (universeProperties.isRandomized()) {
            initializeRandom(size, universeProperties.getSpawnSize());
        } else {
            initializeEmpty(size);
        }
    }

    private void update() {
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

    protected Cell findCell(int posX, int posY) {
        int x = posX;
        int y = posY;
        if (posX < 0) x = cells.length + posX;
        if (posX >= cells.length) x = posX - cells.length;
        if (posY < 0) y = cells.length + posY;
        if (posY >= cells.length) y = posY - cells.length;
        return cells[x][y];
    }

    private void initializeRandom(int size, int spawnSize) {
        Random random = new Random();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int margin = (int) (size * 0.5 - spawnSize * 0.5);
                if (x >= margin && x <= size - margin && y >= margin && y <= size - margin) {
                    cells[x][y] = new Cell(x, y, random.nextFloat() * props.getHighEnergyState());
                } else {
                    cells[x][y] = new Cell(x, y, random.nextFloat() * props.getLowEnergyState() * 2);
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

    public void reset() {
        initializeRandom(universeProperties.getSize(), universeProperties.getSpawnSize());
        generation = 0;
    }

    public void nextGeneration() {
        generation++;
        update();
    }

    public Cell[][] getAllCells() {
        return cells.clone();
    }

    public int getGenPerSec() {
        return genPerSec;
    }

    public void setGenPerSec(int count) {
        genPerSec = count;
    }

    public int getGeneration() {
        return generation;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
