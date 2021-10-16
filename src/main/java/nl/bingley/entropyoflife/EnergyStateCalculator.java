package nl.bingley.entropyoflife;

import nl.bingley.entropyoflife.config.LifeProperties;
import nl.bingley.entropyoflife.config.UniverseProperties;
import nl.bingley.entropyoflife.model.Cell;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class EnergyStateCalculator {

    private final LifeProperties props;
    private final Random random;

    private final float energyJump;
    private final float energyStep;

    public EnergyStateCalculator(UniverseProperties universeProperties) {
        props = universeProperties.getLifeProperties();
        random = new Random();
        energyJump = props.getHighEnergyState() - props.getLowEnergyState();
        energyStep = Math.min(props.getLowEnergyState() * 0.5f, energyJump * 0.5f);
    }

    public float calculateEnergyDelta(Cell cell, int livingNeighbours) {
        boolean alive = isAlive(cell);
        if (alive && (livingNeighbours < props.getSurviveMin() || livingNeighbours > props.getSurviveMax())) {
            // Cell dies, lower energy state
            return -energyJump;
        } else if (alive && cell.value < props.getHighEnergyState() - energyStep * 0.5) {
            // Cell stays alive, restore to high energy state
            return energyStep;
        } else if (alive && cell.value > props.getHighEnergyState() + energyStep * 0.5) {
            // Cell stays alive, restore to high energy state
            return -energyStep;
        } else if (alive) {
            // Cell stays alive, maintain high energy state
            return 0;
        } else if (livingNeighbours >= props.getBirthMin() && livingNeighbours <= props.getBirthMax()) {
            // Cell is born, raise energy state
            return energyJump;
        } else if (cell.value > props.getLowEnergyState()) {
            // Cell stays dead, restore to low energy state
            return props.getLowEnergyState() - cell.value;
        } else if (cell.value < props.getMinEnergyState()) {
            // Cell stays dead, restore low energy state
            return props.getMinEnergyState() - cell.value + energyStep;
        } else {
            // Cell stays dead, maintain low energy state
            return 0;
        }
    }

    public int countLivingCells(List<Cell> cells) {
        return (int) cells.stream().filter(this::isAlive).count();
    }

    public float randomHighEnergyState() {
        return random.nextBoolean() ? props.getHighEnergyState() : props.getLowEnergyState();
    }

    public float randomLowEnergyState() {
        return 0.5f * props.getLowEnergyState() + 0.5f * random.nextFloat() * props.getLowEnergyState();
    }

    private boolean isAlive(Cell cell) {
        return cell.value > props.getLifeEnergyThreshold();
    }
}
