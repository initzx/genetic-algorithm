package com.school.geneticAlgorithm;

import processing.core.PApplet;
import processing.core.PFont;

import java.util.*;
import java.util.stream.Collectors;

public class Algorithm extends PApplet {
    final static int POPULATION_SIZE = 50;
    final static int SPEED = 10;
    final static int STOP_THRESHOLD = 0;
    final static int RECENT_PACKS_SIZE = SPEED*10;

    PFont font = null;
    PFont fontBold = null;

    ArrayList<Pack> population = new ArrayList<>();
    ArrayList<Pack> recentBestPacks = new ArrayList<>();
    Pack currentBestpack = null;
    Pack genBestPack = null;
    int bestFoundGen = -1;

    @Override
    public void setup() {
        frameRate(SPEED);
        font = createFont("Courier", 12, true);
        fontBold = createFont("Courier Bold", 12, true);
    }

    @Override
    public void settings() {
        size(1000,500);
    }

    @Override
    public void draw() {
        displayGen();
        if (bestFoundGen == -1) {
            newGeneration1();
            checkStop();
        }
    }

    void genStartPopulation() {
        population.clear();
        for (int p = 0; p < POPULATION_SIZE; p++) {
            Pack backpack = new Pack();
            population.add(backpack);
        }
    }

    void newGeneration1() {
        HashMap<Pack, Double> fittest = getFittest();
        while (fittest.isEmpty()) {
            genStartPopulation();
            fittest = getFittest();
        }
        genBestPack = fittest.entrySet().iterator().next().getKey();
        if (currentBestpack == null || genBestPack.calcFitness() > currentBestpack.calcFitness()) {
            currentBestpack = genBestPack;
        }
        recentBestPacks.add(currentBestpack);
        this.population = getNewGeneration(fittest);
    }

    void printGen() {
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

    void drawGen() {
        background(255);
        fill(0);
        if (currentBestpack == null)
            return;
        textFont(fontBold, 25);
        text("Current best backpack", 10, 30);

        textFont(font, 20);
        text("Generation: "+(bestFoundGen == -1 ? frameCount : bestFoundGen), 700, 55);
        text("Mutation rate: "+Pack.MUTATION_RATE, 700, 72);
        text("Max weight: "+Pack.MAX_WEIGHT, 700, 89);
        if (bestFoundGen != -1) {
            fill(0, 255, 0);
            text("Found the best backpack after "+bestFoundGen+" generations", 450, 120);
            fill(0);
        }

        textFont(font, 20);
        text("Fitness: "+ currentBestpack.calcFitness(), 15, 55);
        text("Weight: "+ currentBestpack.getTotalWeight(), 15, 72);
        text("Value: "+ currentBestpack.getTotalValue(), 15, 89);
        text("Items:", 15, 115);
        textFont(font, 15);
        text(currentBestpack.getItemsAsString(), 20, 130);
    }

    void drawGraph() {
        if (recentBestPacks.isEmpty())
            return;
        Pack fittest = recentBestPacks.stream().max(Comparator.comparingDouble(Pack::calcFitness)).orElse(null);

        float hMax = 200;
        float wMax = 100;
        float yStart = 470;
        float xStart = 290;
        float graphLength = 690;
        float xEnd = xStart+graphLength;

        float w = Math.min(graphLength/recentBestPacks.size(), wMax);
        float gapLength = w*3/4;


        float ratio = hMax/(float)fittest.calcFitness();
        Iterator<Pack> iter = recentBestPacks.iterator();

        textFont(font, 15);
        text("Fitness", xStart-70, (yStart+yStart-hMax)/2);
        text("f: "+fittest.calcFitness(), xStart-70, yStart-hMax-10);

        text("The last "+recentBestPacks.size()+" Backpacks",(xStart+xStart+graphLength)/2, yStart+15);
        for (float x=xStart; x < xEnd; x+=w+gapLength) {
            if (!iter.hasNext())
                return;
            Pack current = iter.next();
            float h = (float)current.calcFitness()*ratio;
            rect(x,yStart-h, w, h);
        }
    }

    void displayGen() {
//        printGen();
        drawGen();
        drawGraph();
    }

    void checkStop() {
        if (recentBestPacks.size() <= RECENT_PACKS_SIZE) {
            return;
        }
        double maxFitness = recentBestPacks.stream().mapToDouble(Pack::calcFitness).max().orElse(-1);
        double minFitness = recentBestPacks.stream().mapToDouble(Pack::calcFitness).min().orElse(-1);
        if (maxFitness-minFitness <= STOP_THRESHOLD && maxFitness != -1) {
            print("Found solution, stopping now\nGenerations: "+frameCount);
            bestFoundGen = frameCount;
        }
        recentBestPacks.remove(0);
    }

    HashMap<Pack, Double> getFittest() {
        HashMap<Pack, Double> fittest = new HashMap<>();
        if (population.isEmpty())
            return fittest;

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
