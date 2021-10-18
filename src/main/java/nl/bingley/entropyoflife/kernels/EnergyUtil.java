package nl.bingley.entropyoflife.kernels;

import java.util.List;

public class EnergyUtil {

    public static long countLivingCells(List<Float> energyValues, float lifeThreshold) {
        return energyValues.stream()
                .filter(value -> value > lifeThreshold)
                .count();
    }

    public static int wrapUniverse(int coordinate, int universeSize) {
        int trueCoordinate = coordinate < 0 ? universeSize + coordinate : coordinate;
        trueCoordinate = coordinate >= universeSize ? coordinate - universeSize : trueCoordinate;
        return trueCoordinate;
    }
}
