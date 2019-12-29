package com.school.geneticAlgorithm;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Algorithm extends PApplet {
    final static int POPULATION_SIZE = 50;
    final static int SPEED = 10;
    final static int STOP_THRESHOLD = 0;

    ArrayList<Pack> population = new ArrayList<>();
    ArrayList<Pack> recentBestPacks = new ArrayList<>();
    Pack currentBestpack = null;
    Pack genBestPack = null;

    @Override
    public void setup() {
        for (int p = 0; p < POPULATION_SIZE; p++) {
            Pack backpack = new Pack();
            population.add(backpack);
        }
        frameRate(SPEED);
    }

    @Override
    public void draw() {
        newGeneration1();
    }

    void newGeneration1() {
        printGeneration();
        checkStop();
        HashMap<Pack, Double> fittest = getFittest();

        genBestPack = fittest.entrySet().iterator().next().getKey();
        if (currentBestpack == null || genBestPack.calcFitness() > currentBestpack.calcFitness()) {
            currentBestpack = genBestPack;
        }
        recentBestPacks.add(currentBestpack);
        this.population = getNewGeneration(fittest);
    }

    void printGeneration() {
        double fitnessSum = population.stream()
                .mapToDouble(Pack::calcFitness).sum();
        double valueSum = population.stream()
                .mapToDouble(Pack::getTotalValue).sum();
        double weightSum = population.stream()
                .mapToDouble(Pack::getTotalWeight).sum();
        double itemCount = population.stream()
                .mapToInt(backpack1 -> backpack1.getItems().length).sum();

        println("favg: "+fitnessSum/population.size());
        println("wavg: "+weightSum/population.size());
        println("vavg: "+valueSum/population.size());
        println("iavg: "+itemCount/population.size());
        println("bestpack: "+currentBestpack);
        println("genbestpack: "+genBestPack);

        println();
    }

    void checkStop() {
        if (recentBestPacks.size() <= SPEED*5) {
            return;
        }
        double maxFitness = recentBestPacks.stream().mapToDouble(Pack::calcFitness).max().orElse(-1);
        double minFitness = recentBestPacks.stream().mapToDouble(Pack::calcFitness).min().orElse(-1);
        if (maxFitness-minFitness <= STOP_THRESHOLD && maxFitness != -1) {
            print("Found solution, stopping now\nGenerations: "+frameCount);
            exit();
        }
        recentBestPacks.clear();
    }

    HashMap<Pack, Double> getFittest() {
        HashMap<Pack, Double> fittest = new HashMap<>();
        ArrayList<Pack> reverseSorted = population.stream().sorted(Comparator.comparingDouble(Pack::calcFitness).reversed())
                .collect(Collectors.toCollection(ArrayList::new));

        for (int i = 0; i < 5; i++) {
            Pack backpack = reverseSorted.get(i);
            double fitness = backpack.calcFitness();
            if (fitness < 0) break;
            fittest.put(backpack, fitness);
        }

        return fittest;
    }

    ArrayList<Pack> getNewGeneration(HashMap<Pack, Double> fittest) {
        ArrayList<Pack> newGeneration = new ArrayList<>();
        for (int p = 0; p < POPULATION_SIZE; p++) {
            Pack random1 = pickRandomPack(fittest);
            Pack random2 = pickRandomPack(fittest);

            Pack child = random1.crossover(random2);
            newGeneration.add(child);
        }
        return newGeneration;
    }

    public static Pack pickRandomPack(HashMap<Pack, Double> probabilityMap) {
        double fitnessSum = probabilityMap.values().stream().mapToDouble(v -> v).sum();
        double p = Math.random()*fitnessSum;
        double cumulativeProbability = 0;

        for (Map.Entry<Pack, Double> entry: probabilityMap.entrySet()) {
            cumulativeProbability += entry.getValue();
            if (p <= cumulativeProbability)
                return entry.getKey();
        }
        return null;
    }

    public static void main(String[] args) {
        PApplet.main("com.school.geneticAlgorithm.Algorithm");
    }

}
