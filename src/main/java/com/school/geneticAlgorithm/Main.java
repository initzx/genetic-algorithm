package com.school.geneticAlgorithm;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main extends PApplet {
    final static int POPULATION_SIZE = 50;
    ArrayList<Backpack> population = new ArrayList<>();

    @Override
    public void setup() {
        for (int p = 0; p < POPULATION_SIZE; p++) {
            Backpack backpack = new Backpack();
            population.add(backpack);
        }
        frameRate(1);
    }

    @Override
    public void draw() {
        HashMap<Double, Backpack> fittest = new HashMap<>();
//        double fitnessSum = population.stream()
//                .filter(b -> b.calcFitness() >= 2000)
//                .mapToDouble(Backpack::calcFitness).sum();
        ArrayList<Backpack> fittestArray = new ArrayList<>();
        double fitnessSum = 0;
        for (Backpack backpack: population) {
            double fitness = backpack.calcFitness();
            if (fitness < 2000) continue;
//            System.out.println(backpack);
            fitnessSum += fitness;
            fittestArray.add(backpack);
        }
        System.out.println("fitSum: "+fitnessSum);

        for (Backpack backpack: fittestArray) {
            double fitness = backpack.calcFitness();
            fittest.put(fitness/fitnessSum, backpack);
        }

        double cSum = fittest.keySet().stream().mapToDouble(v -> v).sum();
        System.out.println("csum: "+cSum);

        ArrayList<Backpack> newGeneration = new ArrayList<>();
        for (int p = 0; p < POPULATION_SIZE; p++) {
            Backpack random1 = pickRandomBackpack(fittest);
            Backpack random2 = pickRandomBackpack(fittest);

            Backpack child = random1.crossover(random2);
            newGeneration.add(child);
        }

        this.population = newGeneration;
    }

    public static Backpack pickRandomBackpack(HashMap<Double, Backpack> probabilityMap) {
        double p = Math.random();
        double cumulativeProbability = 0;
        for (Map.Entry<Double, Backpack> entry: probabilityMap.entrySet()) {
            cumulativeProbability += entry.getKey();
            if (p <= cumulativeProbability)
                return entry.getValue();
        }
        return (Backpack) probabilityMap.values().toArray()[0];
    }

    public static void main(String[] args) {
        PApplet.main("com.school.geneticAlgorithm.Main");
    }
}
