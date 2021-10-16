package nl.bingley.entropyoflife;

import nl.bingley.entropyoflife.config.LifeProperties;
import nl.bingley.entropyoflife.config.UniverseProperties;
import nl.bingley.entropyoflife.model.Cell;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class Universe {

    private final EnergyStateCalculator calculator;

    private final int energyNeighbourhoodRadius;
    private final int lifeNeighbourhoodRadius;
    private final int size;
    private final int spawnSize;

    private int generation;

    private final float[][] delta;
    private final Cell[][] cells;

    public Universe(EnergyStateCalculator calculator, UniverseProperties uniProps) {
        this.calculator = calculator;

        LifeProperties lifeProps = uniProps.getLifeProperties();
        energyNeighbourhoodRadius = lifeProps.getEnergyNeighbourhoodRadius();
        lifeNeighbourhoodRadius = lifeProps.getLifeNeighbourhoodRadius();

        size = uniProps.getSize();
        spawnSize = uniProps.getSpawnSize();
        cells = new Cell[size][size];
        delta = new float[size][size];
        if (uniProps.isRandomized()) {
            initializeRandom(size, spawnSize);
        } else {
            initializeEmpty(size);
        }
    }

    private void update() {
        Arrays.stream(cells)
                .flatMap(Arrays::stream)
                .parallel()
                .forEach(this::calculateEnergyDelta);
        Arrays.stream(cells)
                .flatMap(Arrays::stream)
                .parallel()
                .forEach(cell -> cell.value += calculateTotalEnergyDelta(cell));
    }

    private float calculateTotalEnergyDelta(Cell cell) {
        float neighbourhoodDelta = calculateNeighbourhoodDelta(cell, energyNeighbourhoodRadius);
        return delta[cell.x][cell.y] - neighbourhoodDelta;
    }

    private void calculateEnergyDelta(Cell cell) {
        int livingNeighbours = calculator.countLivingCells(findAllNeighbours(cell, lifeNeighbourhoodRadius));
        delta[cell.x][cell.y] = calculator.calculateEnergyDelta(cell, livingNeighbours);
    }

    private float calculateNeighbourhoodDelta(Cell centerCell, int radius) {
        if (radius == 0) return 0;
        float energy = 0;
        for (int x = centerCell.x - radius; x <= centerCell.x + radius; x++) {
            for (int y = centerCell.y - radius; y <= centerCell.y + radius; y++) {
                if (x != centerCell.x || y != centerCell.y) {
                    energy += delta[coordinateToCell(x)][coordinateToCell(y)];
                }
            }
        }
        return energy / ((int) Math.pow(radius * 2 + 1, 2) - 1);
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

    private int coordinateToCell(int coordinate) {
        if (coordinate < 0) coordinate = cells.length + coordinate;
        if (coordinate >= cells.length) coordinate = coordinate - cells.length;
        return coordinate;
    }

    public Cell findCell(int x, int y) {
        return cells[coordinateToCell(x)][coordinateToCell(y)];
    }

    private void initializeRandom(int size, int spawnSize) {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int margin = (int) (size * 0.5 - spawnSize * 0.5);
                if (x >= margin && x <= size - margin && y >= margin && y <= size - margin) {
                    cells[x][y] = new Cell(x, y, calculator.randomHighEnergyState());
                } else {
                    cells[x][y] = new Cell(x, y, calculator.randomLowEnergyState());
                }
            }
        }
    }

    private void initializeEmpty(int size) {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                cells[x][y] = new Cell(x, y, 0);
            }
        }
    }

    public void reset() {
        initializeRandom(size, spawnSize);
        generation = 0;
    }

    public void nextGeneration() {
        generation++;
        update();
    }

    public float[][] getDelta() {
        return delta.clone();
    }

    public Cell[][] getAllCells() {
        return cells.clone();
    }

    public int getGeneration() {
        return generation;
    }
}
