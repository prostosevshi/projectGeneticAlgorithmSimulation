package controller;

import java.util.Random;

public class Mutation {

    public static double[] crossover(double[] genome1, double[] genome2) {
        double[] newGenome = new double[genome1.length];
        Random random = new Random();

        for (int i = 0; i < genome1.length; i++) {
            // Randomly take gene from one of the parents
            newGenome[i] = random.nextBoolean() ? genome1[i] : genome2[i];
        }

        return newGenome;
    }

    public static double[] mutate(double[] genome, double mutationRate) {
        Random random = new Random();

        for (int i = 0; i < genome.length; i++) {
            if (random.nextDouble() < mutationRate) {
                genome[i] += random.nextGaussian() * 0.1;  // Small random mutation
            }
        }

        return genome;
    }
}
