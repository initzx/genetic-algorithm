package com.school.geneticAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Pack {

    public static final String[] ITEMS = new String[] {"kort", "sokker", "solcreme", "sukker", "pung", "sandwich", "solbriller", "kompas", "banan", "vandtætte_overtøj", "vandtætte_bukser", "vand", "ost", "æble", "kamera", "telt", "håndklæde", "dåsemad", "tshirt", "paraply", "bog", "bukser", "øl", "notesbog", "spisepinde"};
    public static final int[] WEIGHTS = new int[]{90, 40, 110, 150, 220, 500, 70, 130, 270, 430, 420, 1530, 230, 390, 320, 2000, 180, 680, 240, 730, 300, 480, 520, 900, 100};
    public static final int[] PRICES = new int[]{150, 50, 70, 60, 80, 160, 20, 35, 60, 75, 70, 200, 30, 40, 30, 150, 12, 45, 15, 40, 10, 10, 10, 1, 100};

    public static final int MAX_WEIGHT = 5000;
    public static final double MUTATION_RATE = 0.11;
    public static final double BASE_PROBABILITY = 0.7;

    private int[] items = new int[ITEMS.length];
    private Random random = new Random();

    public Pack() {
        for (int i=0; i<ITEMS.length; i++) {
            if (Math.random() < BASE_PROBABILITY) {
                items[i] = 1;
            }
        }
    }

    public Pack(int[] childItems) {
        setAndMutateA(childItems);
    }

    public void setAndMutateA(int[] childItems) {
        for (int i=0; i<ITEMS.length; i++) {
            if (Math.random() > MUTATION_RATE) {
                items[i] = childItems[i];
            } else {
                int index = random.nextInt(ITEMS.length);
                items[index] = (items[index]+1)%2;
            }
        }
    }

    public void setAndMutateB(int[] childItems) {
        System.arraycopy(childItems, 0, items, 0, ITEMS.length);

        int index = random.nextInt(ITEMS.length);
        items[index] = items[index] == 1 ? 0 : 1;
    }

    public double calcFitness() {
        double value = getTotalValue();
        double weight = getTotalWeight();
        return weight <= MAX_WEIGHT ? value : -1;
    }

    public Pack crossover(Pack partner) {
        int splitAt = random.nextInt(ITEMS.length);
        int[] childItems = new int[ITEMS.length];

        for (int i=0; i<ITEMS.length; i++) {
            childItems[i] = i <= splitAt ? items[i] : partner.items[i];
        }

        return new Pack(childItems);
    }

    @Override
    public String toString() {
        return "Backpack{" +
                "fitness=" + calcFitness() +
                ", weight=" + getTotalWeight() +
                ", value=" + getTotalValue() +
                ", items=" + Arrays.toString(getItems()) +
                '}';
    }

    public String[] getItems() {
        ArrayList<String> itemsArray = new ArrayList<>();
        for (int i=0; i<ITEMS.length; i++) {
            if (items[i] != 0) {
                itemsArray.add(ITEMS[i]);
            }
        }
        return itemsArray.toArray(new String[0]);
    }

    public Double getTotalValue() {
        double v = 0;
        for (int i=0; i<PRICES.length; i++) {
            if (items[i] != 0) {
                v += PRICES[i];
            }
        }
        return v;
    }

    public Double getTotalWeight() {
        double w = 0;
        for (int i=0; i<WEIGHTS.length; i++) {
            if (items[i] != 0) {
                w += WEIGHTS[i];
            }
        }
        return w;
    }
}
