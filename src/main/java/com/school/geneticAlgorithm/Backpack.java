package com.school.geneticAlgorithm;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;

public class Backpack {
    public static final HashMap<String, AbstractMap.SimpleImmutableEntry<Integer, Integer>> AVAILABLE_ITEMS = new HashMap<>();
    public static final int maxWeight = 5000;
    public static final double mutationRate = 0.1;

    static {
        AVAILABLE_ITEMS.put("kort", new AbstractMap.SimpleImmutableEntry<>(90, 150));
        AVAILABLE_ITEMS.put("sokker", new AbstractMap.SimpleImmutableEntry<>(40, 50));
        AVAILABLE_ITEMS.put("solcreme", new AbstractMap.SimpleImmutableEntry<>(110, 70));
        AVAILABLE_ITEMS.put("sukker", new AbstractMap.SimpleImmutableEntry<>(150, 60));
        AVAILABLE_ITEMS.put("pung", new AbstractMap.SimpleImmutableEntry<>(220, 80));
        AVAILABLE_ITEMS.put("sandwich", new AbstractMap.SimpleImmutableEntry<>(500, 160));
        AVAILABLE_ITEMS.put("solbriller", new AbstractMap.SimpleImmutableEntry<>(70, 20));
        AVAILABLE_ITEMS.put("kompas", new AbstractMap.SimpleImmutableEntry<>(130, 35));
        AVAILABLE_ITEMS.put("banan", new AbstractMap.SimpleImmutableEntry<>(270, 60));
        AVAILABLE_ITEMS.put("vandtætte_overtøj", new AbstractMap.SimpleImmutableEntry<>(430, 75));
        AVAILABLE_ITEMS.put("vandtætte_bukser", new AbstractMap.SimpleImmutableEntry<>(420, 70));
        AVAILABLE_ITEMS.put("vand", new AbstractMap.SimpleImmutableEntry<>(1530, 200));
        AVAILABLE_ITEMS.put("ost", new AbstractMap.SimpleImmutableEntry<>(230, 30));
        AVAILABLE_ITEMS.put("æble", new AbstractMap.SimpleImmutableEntry<>(390, 40));
        AVAILABLE_ITEMS.put("kamera", new AbstractMap.SimpleImmutableEntry<>(320, 30));
        AVAILABLE_ITEMS.put("telt", new AbstractMap.SimpleImmutableEntry<>(2000, 150));
        AVAILABLE_ITEMS.put("håndklæde", new AbstractMap.SimpleImmutableEntry<>(180, 12));
        AVAILABLE_ITEMS.put("dåsemad", new AbstractMap.SimpleImmutableEntry<>(680, 45));
        AVAILABLE_ITEMS.put("tshirt", new AbstractMap.SimpleImmutableEntry<>(240, 15));
        AVAILABLE_ITEMS.put("paraply", new AbstractMap.SimpleImmutableEntry<>(730, 40));
        AVAILABLE_ITEMS.put("bog", new AbstractMap.SimpleImmutableEntry<>(300, 10));
        AVAILABLE_ITEMS.put("bukser", new AbstractMap.SimpleImmutableEntry<>(480, 10));
        AVAILABLE_ITEMS.put("øl", new AbstractMap.SimpleImmutableEntry<>(520, 10));
        AVAILABLE_ITEMS.put("notesbog", new AbstractMap.SimpleImmutableEntry<>(900, 1));
    }

    private ArrayList<String> items = new ArrayList<>();

    public Backpack() {
        for (String item : AVAILABLE_ITEMS.keySet()) {
            if (Math.random() < 0.5)
                items.add(item);
        }
    }

    public Backpack(ArrayList<String> givenItems) {
        items.addAll(givenItems);

        for (String item: AVAILABLE_ITEMS.keySet()) {
            if (!items.contains(item) && Math.random() < mutationRate)
                items.add(item);
        }
    }

    public double calcFitness() {
        double value = 0;
        double weight = 0;
        for (String item: items) {
            AbstractMap.SimpleImmutableEntry<Integer, Integer> weightValue = AVAILABLE_ITEMS.get(item);
            weight += weightValue.getKey();
            value += weightValue.getValue();
        }
        return maxWeight-weight+value;
    }

    public Backpack crossover(Backpack partner) {
        ArrayList<String> common = new ArrayList<>(items);
        common.containsAll(partner.getItems());
        return new Backpack(common);
    }

    @Override
    public String toString() {
        return "Backpack{" +
                "fitness=" + calcFitness() +
                ", items=" + items +
                '}';
    }

    public ArrayList<String> getItems() {
        return items;
    }
}
