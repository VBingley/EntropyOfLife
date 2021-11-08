package nl.bingley.entropyoflife;

import nl.bingley.entropyoflife.config.properties.LifeProperties;
import nl.bingley.entropyoflife.config.properties.UniverseProperties;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UniverseTestUtil {

    private static final float floatRoundingErrorMargin = 0.000001f;

    private UniverseTestUtil() {
    }

    public static float countTotalEnergy(float[][] energyMatrix) {
        float energy = 0;
        for (float[] row : energyMatrix) {
            for (float energyValue : row) {
                energy += energyValue;
            }
        }
        return energy;
    }

    public static void assertEnergyValue(float expected, float actual) {
        assertTrue(Math.abs(expected - actual) < floatRoundingErrorMargin,
                String.format("Expected value: %f, actual value: %f", expected, actual));
    }

    public static UniverseProperties mockGameOfLifeProperties(int size) {
        LifeProperties lifeProps = mock(LifeProperties.class);
        when(lifeProps.getLifeNeighbourhoodRadius()).thenReturn(1);
        when(lifeProps.getBirthMax()).thenReturn(3);
        when(lifeProps.getBirthMin()).thenReturn(3);
        when(lifeProps.getSurviveMax()).thenReturn(3);
        when(lifeProps.getSurviveMin()).thenReturn(2);
        when(lifeProps.getLifeEnergyThreshold()).thenReturn(0.5f);
        when(lifeProps.getHighEnergyState()).thenReturn(1f);
        when(lifeProps.getLowEnergyState()).thenReturn(0f);
        when(lifeProps.getMinEnergyState()).thenReturn(0f);
        when(lifeProps.getEnergyNeighbourhoodRadius()).thenReturn(0);

        UniverseProperties universeProps = mock(UniverseProperties.class);
        when(universeProps.getSize()).thenReturn(size);
        when(universeProps.getLifeProperties()).thenReturn(lifeProps);
        when(universeProps.isRandomized()).thenReturn(false);
        return universeProps;
    }

    public static UniverseProperties mockEntropyOfLifeProperties(int size) {
        LifeProperties lifeProps = mock(LifeProperties.class);
        when(lifeProps.getLifeNeighbourhoodRadius()).thenReturn(1);
        when(lifeProps.getBirthMax()).thenReturn(3);
        when(lifeProps.getBirthMin()).thenReturn(3);
        when(lifeProps.getSurviveMax()).thenReturn(3);
        when(lifeProps.getSurviveMin()).thenReturn(2);
        when(lifeProps.getLifeEnergyThreshold()).thenReturn(0.5f);
        when(lifeProps.getHighEnergyState()).thenReturn(0.75f);
        when(lifeProps.getLowEnergyState()).thenReturn(0.125f);
        when(lifeProps.getMinEnergyState()).thenReturn(0f);
        when(lifeProps.getEnergyNeighbourhoodRadius()).thenReturn(2);

        UniverseProperties universeProps = mock(UniverseProperties.class);
        when(universeProps.getSize()).thenReturn(size);
        when(universeProps.getLifeProperties()).thenReturn(lifeProps);
        when(universeProps.isRandomized()).thenReturn(false);
        return universeProps;
    }
}
