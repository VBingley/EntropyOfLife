package nl.bingley.entropyoflife.services;

import nl.bingley.entropyoflife.config.LifeProperties;
import nl.bingley.entropyoflife.config.UniverseProperties;
import nl.bingley.entropyoflife.models.Cell;
import nl.bingley.entropyoflife.models.Universe;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UniverseStateCalculator {

    private final CellStateCalculator cellStateCalculator;
    private final UniverseProperties uniProps;
    private final LifeProperties lifeProps;

    private final Universe universe;

    private int generation;

    public UniverseStateCalculator(CellStateCalculator cellStateCalculator, UniverseProperties universeProperties) {
        this.cellStateCalculator = cellStateCalculator;
        this.uniProps = universeProperties;
        lifeProps = uniProps.getLifeProperties();

        if (uniProps.isRandomized()) {
            long seed = uniProps.getRandomSeed() != 0 ? uniProps.getRandomSeed() : System.nanoTime();
            universe = new Universe(uniProps.getSize(), uniProps.getSpawnSize(), seed, lifeProps.getHighEnergyState(), lifeProps.getLowEnergyState());
        } else {
            universe = new Universe(uniProps.getSize());
        }
    }

    private void updateAllValues() {
        universe.forEachCellInEnergyMap(cell -> universe.setDeltaValue(cell, calculateOwnEnergyDelta(cell)));
        universe.forEachCellInEnergyMap(cell -> cell.value += universe.getDeltaCell(cell).value - calculateEnergyDeltaFromNeighbourhood(cell));
    }

    private float calculateOwnEnergyDelta(Cell energyCell) {
        List<Cell> neighbourhood = universe.findEnergyCellsInNeighbourhood(energyCell, lifeProps.getLifeNeighbourhoodRadius());
        int livingNeighbours = (int) cellStateCalculator.countLivingCells(neighbourhood);
        return cellStateCalculator.calculateEnergyDelta(energyCell, livingNeighbours);
    }

    private float calculateEnergyDeltaFromNeighbourhood(Cell energyCell) {
        List<Cell> deltaNeighbourhood = universe.findDeltaCellsInNeighbourhood(energyCell, lifeProps.getEnergyNeighbourhoodRadius());
        if (deltaNeighbourhood.size() == 0) {
            return 0;
        }
        Float energy = deltaNeighbourhood.stream()
                .map(cell -> cell.value)
                .reduce(Float::sum).orElse(0f);
        return energy / deltaNeighbourhood.size();
    }

    public void reset() {
        universe.initializeRandom(uniProps.getSpawnSize(), System.nanoTime(), lifeProps.getHighEnergyState(), lifeProps.getLowEnergyState());
        generation = 0;
    }

    public void nextGeneration() {
        generation++;
        updateAllValues();
    }

    public int getGeneration() {
        return generation;
    }

    public Universe getUniverse() {
        return universe;
    }
}
