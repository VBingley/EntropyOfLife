package nl.bingley.customlife;

import nl.bingley.customlife.config.LifeProperties;
import nl.bingley.customlife.model.Cell;

import java.util.List;
import java.util.stream.Collectors;

public class CellStateUtil {

    private final LifeProperties props;
    private final float energyJump;
    private final float energyStep;

    public CellStateUtil(LifeProperties lifeProperties) {
        props = lifeProperties;
        energyJump = props.getHighEnergyState() - props.getLowEnergyState();
        energyStep = Math.min(props.getLowEnergyState() *0.5f, energyJump * 0.5f);
    }

    public void gainEnergy(float energy, Cell cell, List<Cell> energyNeighbours) {
        if (energy > 0 && props.getEnergyNeighbourhoodRadius() != 0) {
            List<Cell> energeticNeighbours = energyNeighbours.stream()
                    .filter(neighbour -> neighbour.oldValue > 0)
                    .collect(Collectors.toList());
            if (energeticNeighbours.size() > 0) {
                cell.value += cell.oldValue + energy;
                energeticNeighbours.forEach(neighbour -> neighbour.value -= energy * (1f / energeticNeighbours.size()));
            } else {
                cell.value += cell.oldValue;
            }
        } else {
            cell.value += cell.oldValue + energy;
            energyNeighbours.forEach(neighbour -> neighbour.value -= energy * (1f / energyNeighbours.size()));
        }
    }

    public float calculateEnergyDelta(Cell cell, int livingNeighbours) {
        boolean alive = isAlive(cell);
        if (alive && (livingNeighbours < props.getSurviveMin() || livingNeighbours > props.getSurviveMax())) {
            // Cell dies, lower energy state
            return -energyJump;
        } else if (alive && cell.oldValue < props.getHighEnergyState() - energyStep * 0.5) {
            // Cell stays alive, restore to high energy state
            return energyStep;
        } else if (alive && cell.oldValue > props.getHighEnergyState() + energyStep * 0.5) {
            // Cell stays alive, restore to high energy state
            return -energyStep;
        } else if (alive) {
            // Cell stays alive, maintain high energy state
            return 0;
        } else if (livingNeighbours >= props.getBirthMin() && livingNeighbours <= props.getBirthMax()) {
            // Cell is born, raise energy state
            return energyJump;
        } else if (cell.oldValue > props.getLowEnergyState()) {
            // Cell stays dead, restore to low energy state
            return props.getLowEnergyState() - cell.oldValue;
        } else if (cell.oldValue < props.getMinEnergyState()) {
            // Cell stays dead, restore low energy state
            return props.getMinEnergyState() - cell.oldValue + energyStep;
        } else {
            // Cell stays dead, maintain low energy state
            return 0;
        }
    }

    public int countLivingCells(List<Cell> cells) {
        return (int) cells.stream().filter(this::isAlive).count();
    }

    private boolean isAlive(Cell cell) {
        return cell.oldValue > props.getLifeEnergyThreshold();
    }
}
