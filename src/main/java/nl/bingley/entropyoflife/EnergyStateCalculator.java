package nl.bingley.entropyoflife;

import nl.bingley.entropyoflife.config.LifeProperties;
import nl.bingley.entropyoflife.config.UniverseProperties;
import nl.bingley.entropyoflife.model.Cell;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EnergyStateCalculator {

    private final LifeProperties props;

    public EnergyStateCalculator(UniverseProperties universeProperties) {
        props = universeProperties.getLifeProperties();
    }

    public float calculateEnergyDelta(Cell cell, int livingNeighbours) {
        boolean alive = isAlive(cell);
        if (alive && (livingNeighbours < props.getSurviveMin() || livingNeighbours > props.getSurviveMax())) {
            // Cell dies, lower energy state
            return -props.getEnergyJump();
        } else if (alive && cell.value < props.getHighEnergyState() - props.getEnergyStep() * 0.5) {
            // Cell stays alive, restore to high energy state
            return props.getEnergyStep();
        } else if (alive && cell.value > props.getHighEnergyState() + props.getEnergyStep() * 0.5) {
            // Cell stays alive, restore to high energy state
            return -props.getEnergyStep();
        } else if (alive) {
            // Cell stays alive, maintain high energy state
            return 0;
        } else if (livingNeighbours >= props.getBirthMin() && livingNeighbours <= props.getBirthMax()) {
            // Cell is born, raise energy state
            return props.getEnergyJump();
        } else if (cell.value > props.getLowEnergyState()) {
            // Cell stays dead, restore to low energy state
            return props.getLowEnergyState() - cell.value;
        } else if (cell.value < props.getMinEnergyState()) {
            // Cell stays dead, restore low energy state
            return props.getMinEnergyState() - cell.value + props.getEnergyStep();
        } else {
            // Cell stays dead, maintain low energy state
            return 0;
        }
    }

    public int countLivingCells(List<Cell> cells) {
        return (int) cells.stream().filter(this::isAlive).count();
    }

    private boolean isAlive(Cell cell) {
        return cell.value > props.getLifeEnergyThreshold();
    }
}
