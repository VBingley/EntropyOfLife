package nl.bingley.entropyoflife.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class Universe {

    private final Cell[][] deltaMap;
    private final Cell[][] energyMap;

    public Universe(int size) {
        deltaMap = new Cell[size][size];
        energyMap = new Cell[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                deltaMap[x][y] = new Cell(x, y, 0);
                energyMap[x][y] = new Cell(x, y, 0);
            }
        }
    }

    public Universe(int size, int spawnSize, long seed, float highEnergy, float lowEnergy) {
        this(size);
        initializeRandom(spawnSize, seed, highEnergy, lowEnergy);
    }

    public void forEachCellInEnergyMap(Consumer<Cell> action) {
        Arrays.stream(energyMap)
                .flatMap(Arrays::stream).parallel()
                .forEach(action);
    }

    public List<Cell> findEnergyCellsInNeighbourhood(Cell cell, int radius) {
        return findCellsInNeighbourhood(energyMap, cell, radius);
    }

    public List<Cell> findDeltaCellsInNeighbourhood(Cell cell, int radius) {
        return findCellsInNeighbourhood(deltaMap, cell, radius);
    }

    public void initializeRandom(int spawnSize, long seed, float highEnergy, float lowEnergy) {
        Random random = new Random(seed);
        System.out.println("Random seed: " + seed);

        forEachCellInEnergyMap(cell -> cell.value = 0.5f * lowEnergy + random.nextFloat() * 0.5f * lowEnergy);
        int center = energyMap.length / 2;
        findEnergyCellsInNeighbourhood(energyMap[center][center], spawnSize / 2)
                .forEach(cell -> cell.value = random.nextBoolean() ? highEnergy : lowEnergy);
    }

    private List<Cell> findCellsInNeighbourhood(Cell[][] cells, Cell cell, int radius) {
        List<Cell> neighbours = new ArrayList<>();
        for (int x = cell.x - radius; x <= cell.x + radius; x++) {
            for (int y = cell.y - radius; y <= cell.y + radius; y++) {
                if (x != cell.x || y != cell.y) {
                    neighbours.add(findCell(cells, x, y));
                }
            }
        }
        return neighbours;
    }

    private Cell findCell(Cell[][] values, int x, int y) {
        return values[coordinateToCell(x)][coordinateToCell(y)];
    }

    private int coordinateToCell(int coordinate) {
        if (coordinate < 0) coordinate = energyMap.length + coordinate;
        if (coordinate >= energyMap.length) coordinate = coordinate - energyMap.length;
        return coordinate;
    }

    public Cell[][] getEnergyMap() {
        return energyMap;
    }

    public Cell getDeltaCell(Cell energyCell) {
        return deltaMap[energyCell.x][energyCell.y];
    }

    public Cell getEnergyCell(int x, int y) {
        return energyMap[x][y];
    }

    public void setDeltaValue(Cell energyCell, float deltaValue) {
        deltaMap[energyCell.x][energyCell.y].value = deltaValue;
    }

    public int getSize() {
        return energyMap.length;
    }
}
