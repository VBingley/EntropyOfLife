package nl.bingley.entropyoflife.services;

import nl.bingley.entropyoflife.config.LifeProperties;
import nl.bingley.entropyoflife.config.UniverseProperties;
import nl.bingley.entropyoflife.models.Cell;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;

@Component
public class CellStateCalculator {

    private final LifeProperties props;

    public CellStateCalculator(UniverseProperties universeProperties) {
        props = universeProperties.getLifeProperties();
    }

    public float calculateEnergyDelta(Cell energyCell, int livingNeighbours) {
        boolean alive = isAlive(energyCell);
        float energyValue = energyCell.value;
        if (alive && (livingNeighbours < props.getSurviveMin() || livingNeighbours > props.getSurviveMax())) {
            // Cell dies, lower energy state
            return -props.getEnergyJump();
        } else if (alive && energyValue < props.getHighEnergyState() - props.getEnergyStep() * 0.5) {
            // Cell stays alive, restore to high energy state
            return props.getEnergyStep();
        } else if (alive && energyValue > props.getHighEnergyState() + props.getEnergyStep() * 0.5) {
            // Cell stays alive, restore to high energy state
            return -props.getEnergyStep();
        } else if (alive) {
            // Cell stays alive, maintain high energy state
            return 0;
        } else if (livingNeighbours >= props.getBirthMin() && livingNeighbours <= props.getBirthMax()) {
            // Cell is born, raise energy state
            return props.getEnergyJump();
        } else if (energyValue > props.getLowEnergyState()) {
            // Cell stays dead, restore to low energy state
            return props.getLowEnergyState() - energyValue;
        } else if (energyValue < props.getMinEnergyState()) {
            // Cell stays dead, restore low energy state
            return props.getMinEnergyState() - energyValue + props.getEnergyStep();
        } else {
            // Cell stays dead, maintain low energy state
            return 0;
        }
    }

    public Color calculateCellColor(float cellValue) {
        if (cellValue > 1) {
            return new Color(0.75f, 0, 0);
        }
        if (cellValue > props.getMinEnergyState()) {
            float red = cellValue > props.getLifeEnergyThreshold() ?
                    0.5f * gradient(cellValue, 1 - props.getLifeEnergyThreshold(), 1) : 0;
            float green = cellValue > props.getLifeEnergyThreshold() ?
                    0.5f * gradient(cellValue, props.getHighEnergyState() - props.getLowEnergyState(), props.getHighEnergyState()) : 0;
            float blue = 0.5f * gradient(cellValue, props.getLifeEnergyThreshold() - props.getMinEnergyState(), props.getLifeEnergyThreshold());
            return new Color(red, green, blue);
        } else {
            float abs = Math.abs(cellValue);
            float value = abs > 1 ? 1 : abs;
            return new Color(value * 0.5f, 0, value * 0.5f);
        }
    }

    private float gradient(float value, float radius, float peak) {
        // min = 0 mid = pi/2 max = pi
        if (value < 0 || value < peak - radius || value > peak + radius) {
            return 0;
        }
        float location = value - peak + radius;
        float result = (float) Math.sin((location / (radius * 2)) * Math.PI);
        return result < 0 ? 0 : result;
    }

    public long countLivingCells(List<Cell> energyValues) {
        return energyValues.stream()
                .filter(cell -> cell.value > props.getLifeEnergyThreshold())
                .count();
    }

    public boolean isAlive(Cell energyCell) {
        return energyCell.value > props.getLifeEnergyThreshold();
    }
}
