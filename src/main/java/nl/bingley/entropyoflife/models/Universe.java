package nl.bingley.entropyoflife.models;

import java.util.Random;

public class Universe {

    public final float[][] deltaMatrix;
    public final float[][] energyMatrix;

    private long generation = 0;

    public Universe(int size) {
        deltaMatrix = new float[size][size];
        energyMatrix = new float[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                deltaMatrix[x][y] = 0;
                energyMatrix[x][y] = 0;
            }
        }
    }

    public Universe(int size, int spawnSize, long seed, float highEnergy, float lowEnergy) {
        this(size);
        initializeRandom(spawnSize, seed, highEnergy, lowEnergy);
    }

    public void initializeRandom(int spawnSize, long seed, float highEnergy, float lowEnergy) {
        generation = 0;
        Random random = new Random(seed);
        System.out.println("Random seed: " + seed);

        int spawnMin = energyMatrix.length / 2 - spawnSize / 2;
        int spawnMax = energyMatrix.length / 2 + spawnSize / 2;
        for (int x = 0; x < energyMatrix.length; x++) {
            for (int y = 0; y < energyMatrix.length; y++) {
                if (x < spawnMin || x > spawnMax || y < spawnMin || y > spawnMax) {
                    energyMatrix[x][y] = 0.5f * lowEnergy + random.nextFloat() * 0.5f * lowEnergy;
                } else {
                    energyMatrix[x][y] = random.nextBoolean() ? highEnergy : lowEnergy;
                }
            }
        }
    }

    public void incrementGeneration() {
        generation++;
    }

    public int getSize() {
        return energyMatrix.length;
    }

    public long getGeneration() {
        return generation;
    }
}
