package nl.bingley.entropyoflife.config.properties;

import org.springframework.core.env.Environment;

public class LifeProperties {

    private static final String LIFE_ENERGY_THRESHOLD = "life.%s.life-energy-threshold";
    private static final String HIGH_ENERGY_STATE = "life.%s.high-energy-state";
    private static final String LOW_ENERGY_STATE = "life.%s.low-energy-state";
    private static final String MIN_ENERGY_STATE = "life.%s.min-energy-state";
    private static final String LIFE_NEIGHBOURHOOD_RADIUS = "life.%s.life-neighbourhood-radius";
    private static final String ENERGY_NEIGHBOURHOOD_RADIUS = "life.%s.energy-neighbourhood-radius";
    private static final String BIRTH_MAX = "life.%s.birth-max";
    private static final String BIRTH_MIN = "life.%s.birth-min";
    private static final String SURVIVE_MAX = "life.%s.survive-max";
    private static final String SURVIVE_MIN = "life.%s.survive-min";

    private final int birthMax;
    private final int birthMin;
    private final int surviveMax;
    private final int surviveMin;

    private final int lifeNeighbourhoodRadius;
    private final int energyNeighbourhoodRadius;

    private final float lifeEnergyThreshold;
    private final float highEnergyState;
    private final float lowEnergyState;
    private final float minEnergyState;

    private final float energyJump;
    private final float energyStep;

    private final Environment env;
    private final String ruleset;

    public LifeProperties(Environment env, String ruleset) {
        this.env = env;
        this.ruleset = ruleset;
        birthMax = env.getRequiredProperty(String.format(BIRTH_MAX, ruleset), Integer.class);
        birthMin = env.getRequiredProperty(String.format(BIRTH_MIN, ruleset), Integer.class);
        surviveMax = env.getRequiredProperty(String.format(SURVIVE_MAX, ruleset), Integer.class);
        surviveMin = env.getRequiredProperty(String.format(SURVIVE_MIN, ruleset), Integer.class);
        lifeNeighbourhoodRadius = env.getRequiredProperty(String.format(LIFE_NEIGHBOURHOOD_RADIUS, ruleset), Integer.class);
        lifeEnergyThreshold = setOptional(LIFE_ENERGY_THRESHOLD);
        highEnergyState = setOptional(HIGH_ENERGY_STATE);
        lowEnergyState = setOptional(LOW_ENERGY_STATE);
        minEnergyState = setOptional(MIN_ENERGY_STATE);
        energyNeighbourhoodRadius = setOptional(ENERGY_NEIGHBOURHOOD_RADIUS).intValue();
        energyJump = highEnergyState - lowEnergyState;
        energyStep = Math.min(lowEnergyState * 0.5f, energyJump * 0.5f);
    }

    private Float setOptional(String property) {
        Float value = env.getProperty(String.format(property, ruleset), Float.class);
        if (value != null) {
            return value;
        } else {
            return env.getProperty(String.format(property, "default"), Float.class);
        }
    }

    public float getLifeEnergyThreshold() {
        return lifeEnergyThreshold;
    }

    public float getHighEnergyState() {
        return highEnergyState;
    }

    public float getLowEnergyState() {
        return lowEnergyState;
    }

    public float getMinEnergyState() {
        return minEnergyState;
    }

    public int getLifeNeighbourhoodRadius() {
        return lifeNeighbourhoodRadius;
    }

    public int getEnergyNeighbourhoodRadius() {
        return energyNeighbourhoodRadius;
    }

    public int getBirthMax() {
        return birthMax;
    }

    public int getBirthMin() {
        return birthMin;
    }

    public int getSurviveMax() {
        return surviveMax;
    }

    public int getSurviveMin() {
        return surviveMin;
    }

    public float getEnergyJump() {
        return energyJump;
    }

    public float getEnergyStep() {
        return energyStep;
    }
}
